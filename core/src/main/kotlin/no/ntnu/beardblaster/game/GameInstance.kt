package no.ntnu.beardblaster.game

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.log.debug
import ktx.log.error
import ktx.log.info
import ktx.log.logger
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.game.Game
import no.ntnu.beardblaster.commons.game.GamePrizeList
import no.ntnu.beardblaster.commons.game.Loot
import no.ntnu.beardblaster.commons.spell.SpellAction
import no.ntnu.beardblaster.commons.wizard.Wizard
import no.ntnu.beardblaster.leaderboard.LeaderBoardRepository
import no.ntnu.beardblaster.spell.SpellActionWithAnimation
import no.ntnu.beardblaster.spell.SpellCasting
import no.ntnu.beardblaster.spell.SpellExecutor
import no.ntnu.beardblaster.spell.SpellSubscription
import no.ntnu.beardblaster.wizard.WizardState
import java.util.*
import kotlin.random.Random

private val LOG = logger<GameInstance>()

@ExperimentalCoroutinesApi
class GameInstance(preparationTime: Int, game: Game) : Observer {

    val spellCasting: SpellCasting = SpellCasting()
    private val spellSubscription: SpellSubscription = SpellSubscription()
    private val gameSubscription: GameSubscription = GameSubscription()

    private val spellExecutor: SpellExecutor = SpellExecutor()
    private lateinit var spellsListener: Job
    private var gameListener: Job
    private val myWizard: Wizard?
    private val opponentWizard: Wizard?

    var gameLoot: GameLoot = GameLoot()
    var winningWizard: Wizard? = null
        private set
    private var losingWizard: Wizard? = null

    val wizardState: WizardState = WizardState(
        Wizard(MAX_HP_PLAYERS, game.host!!),
        Wizard(MAX_HP_PLAYERS, game.opponent!!)
    )

    var currentPhase: GamePhase = GamePhase()
    var timeRemaining: Float
        private set
    var currentTurn = 0
        private set
    private val preparationTime: Int
    private val gameId: String

    fun updateCounter(delta: Float) {
        if (currentPhase.getCurrentPhase() == Phase.Preparation) {
            timeRemaining -= delta * 0.5f
            if (timeRemaining <= 0) {
                currentPhase.setCurrentPhase(Phase.Action)
            }
            if (currentTurn == 0 && !::spellsListener.isInitialized && !isGameOver()) {
                incrementCurrentTurn()
                // Subscribe to events on the first preparation turn
                spellsListener = KtxAsync.launch {
                    spellSubscription.subscribeToUpdatesOn(
                        "games/$gameId/turns/$currentTurn/spells",
                        currentTurn
                    )
                }
            }
        }
    }

    private fun resetCounter() {
        timeRemaining = preparationTime.toFloat()
    }

    private fun incrementCurrentTurn() {

        currentTurn += 1
        LOG.debug { "TURN incremented to $currentTurn" }
    }

    fun lockTurn(): Flow<State<SpellAction>> {
        LOG.info { "Locking spell -> ${spellCasting.getSelectedSpell()!!.spellName}" }
        return GameRepository().castSpell(
            currentTurn, SpellAction(
                spellCasting.getSelectedSpell()!!, myWizard!!.id,
                opponentWizard!!.id
            )
        )
    }

    fun forfeit() {
        val forfeitSpell = SpellAction()
        forfeitSpell.isForfeit = true
        KtxAsync.launch {
            GameRepository().castSpell(currentTurn, forfeitSpell).collect {
                when (it) {
                    is State.Loading -> {
                        LOG.debug { "Forfeiting..." }
                    }
                    is State.Success -> {
                        LOG.debug { "Forfeited successfully." }
                    }
                    is State.Failed -> {
                        LOG.error { "Failed to forfeit: ${it.message}" }
                    }
                }
            }
        }
    }

    // SpellSubscription, GamePhase and GameSubscription (Observables) may send updates through this channel
    override fun update(o: Observable?, arg: Any?) {
        LOG.debug { "$o - $arg" }

        if (o is SpellSubscription) {
            LOG.debug { "o is SpellSubscription" }
            if (arg is SpellAction && currentPhase.getCurrentPhase() != Phase.GameOver) {
                LOG.debug { "arg is SpellAction" }
                // In this game, users actually send a spell to forfeit!
                // Since spells may only be sent by the users themselves (spell docId = userId),
                // Only the user themselves may send a "forfeit" spell
                if (arg.isForfeit) {
                    if (::spellsListener.isInitialized && spellsListener.isActive) {
                        spellsListener.cancel()
                    }
                    LOG.debug { "SpellAction was a forfeit spell!" }
                    when {
                        myWizard!!.id != arg.docId -> {
                            winningWizard = myWizard
                            losingWizard = opponentWizard
                        }
                        else -> {
                            winningWizard = opponentWizard
                            losingWizard = myWizard
                        }
                    }
                    currentPhase.setCurrentPhase(Phase.GameOver)
                    LOG.info { "Winner: ${winningWizard?.displayName}" }
                    LOG.info { "Loser: ${losingWizard?.displayName}" }
                } else {
                    LOG.debug { "!IMPORTANT - Pushing spell to executor: ${arg.spell.spellName}" }
                    spellExecutor.addSpell(arg, currentTurn)
                    if (spellExecutor.spellHistory[currentTurn]?.size == 2) {
                        // Both have sent their spells - let's fast forward to attack phase.
                        currentPhase.setCurrentPhase(Phase.Action)
                    }
                }
            }
        }
        if (o is GameSubscription) {
            if (arg is Game) {
                if (arg.winner != null) {
                    winningWizard = arg.winner
                }
                if (arg.loser != null) {
                    losingWizard = arg.loser
                }
                if (arg.isDraw == true) {
                    LOG.info { "It's a draw." }
                    isDraw = true
                }

                if (arg.loot.isNotEmpty()) {
                    LOG.info { "Prices found! Adding all prices to map." }
                    if (gameLoot.getLoot().isEmpty()) {
                        gameLoot.setLoot(arg.loot)
                    }
                    if (currentPhase.getCurrentPhase() != Phase.GameOver) {
                        currentPhase.setCurrentPhase(Phase.GameOver)
                    }
                } else if (arg.endedAt != 0L) {
                    LOG.info { "Game was ended since endedAt was > 0" }
                    if (currentPhase.getCurrentPhase() != Phase.GameOver) {
                        currentPhase.setCurrentPhase(Phase.GameOver)
                    }
                }
            }
        }

        if (o is GamePhase) {
            if (arg is Phase) {
                when (arg) {
                    Phase.GameOver -> {
                        if (::spellsListener.isInitialized && spellsListener.isActive) {
                            spellsListener.cancel()
                        }
                        if (GameData.instance.isHost && gameLoot.getLoot().isEmpty()) {
                            generateLoot()
                        }
                    }
                    Phase.Preparation -> {
                        if (!isGameOver()) {
                            incrementCurrentTurn()
                            // Subscribe to events on this preparation turn
                            spellsListener = KtxAsync.launch {
                                spellSubscription.subscribeToUpdatesOn(
                                    "games/$gameId/turns/$currentTurn/spells",
                                    currentTurn
                                )
                            }
                        }

                    }
                    Phase.Action -> {
                        if (!isGameOver()) {
                            resetCounter()
                            spellCasting.reset()
                            // Cancel subscription to events on from preparation turn
                            spellsListener.cancel()
                            // Set up next turn if host
                            if (GameData.instance.isHost) {
                                createTurn(currentTurn + 1)
                            }
                        }
                    }
                    Phase.Waiting -> {

                    }
                }
            }
        }
    }

    fun getSpellsCastCurrentTurn(): List<SpellActionWithAnimation> {
        return spellExecutor.getSpellResultForTurn(currentTurn, wizardState)
    }

    private fun determineWinner() {
        if (winningWizard == null || losingWizard == null) {
            if (wizardState.opponents[myWizard!!.id]!!.isWizardDefeated()) {
                winningWizard = opponentWizard
                losingWizard = myWizard
            } else if (wizardState.opponents[opponentWizard!!.id]!!.isWizardDefeated()) {
                winningWizard = myWizard
                losingWizard = opponentWizard
            }
        }
    }

    var isDraw: Boolean = false
        private set

    private fun generateLoot() {

        isDraw = wizardState.getCurrentUserAsWizard()!!
            .isWizardDefeated() && wizardState.getEnemyAsWizard()!!.isWizardDefeated()
        var winner: Wizard? = null
        var loser: Wizard? = null
        val winnerLoot: List<Loot>
        val loserLoot: List<Loot>
        var winnerBeardInc = 7
        var loserBeardInc = -5

        if (isDraw) {
            winnerBeardInc = -1
            loserBeardInc = -1
            winnerLoot = GamePrizeList().getLoserPrizes(1, winnerBeardInc)
            loserLoot = GamePrizeList().getLoserPrizes(1, loserBeardInc)
        } else {
            determineWinner()
            winner = winningWizard
            loser = losingWizard
            when {
                currentTurn < 3 -> {
                    winnerBeardInc = 1
                    winnerLoot = GamePrizeList().getWinnerPrizes(1, winnerBeardInc)
                    loserLoot = GamePrizeList().getLoserPrizes(1, loserBeardInc)
                }
                else -> {
                    winnerLoot = GamePrizeList().getWinnerPrizes(Random.nextInt(3), winnerBeardInc)
                    loserLoot = GamePrizeList().getLoserPrizes(Random.nextInt(3), loserBeardInc)
                }
            }
        }

        LOG.info { "Distributing loot to _winner = ${winner?.id ?: wizardState.getCurrentUserAsWizard()!!.id} amount: ${winnerLoot.size}" }
        LOG.info { "Winner id: ${winner?.id}" }

        winnerLoot.forEach {
            it.receiver =
                winner?.id ?: wizardState.getCurrentUserAsWizard()!!.id
        }

        LOG.info { "Distributing loot to _loser = ${loser?.id ?: wizardState.getEnemyAsWizard()!!.id} amount: ${loserLoot.size}" }
        LOG.info { "Loser id: ${loser?.id}" }

        loserLoot.forEach { it.receiver = loser?.id ?: wizardState.getEnemyAsWizard()!!.id }

        KtxAsync.launch {
            LeaderBoardRepository().updateBeardLength(
                winner ?: wizardState.getCurrentUserAsWizard()!!, winnerBeardInc.toFloat()
            ).collect()
            LeaderBoardRepository().updateBeardLength(
                loser ?: wizardState.getEnemyAsWizard()!!,
                loserBeardInc.toFloat()
            ).collect()

            GameRepository().distributeLoot(
                winnerLoot.plus(loserLoot),
                winner,
                loser,
                isDraw
            ).collect {
                when (it) {
                    is State.Success -> {
                        LOG.debug { "Distributed loot successfully" }
                    }
                    is State.Failed -> {
                        LOG.debug { "Failed to distribute loot: ${it.message}" }
                    }
                    is State.Loading -> {
                        LOG.debug { "Distributing loot…" }
                    }
                }
            }
        }
    }

    fun dispose() {
        if (gameListener.isActive) {
            gameListener.cancel()
        }

        if (spellsListener.isActive) {
            spellsListener.cancel()
        }
    }

    fun isGameOver(): Boolean {
        if (currentPhase.getCurrentPhase() != Phase.GameOver) {
            if (wizardState.isAnyWizardDead() || (winningWizard != null || losingWizard != null) || isDraw) {
                LOG.info { "A wizard is dead! Game is over!" }
                currentPhase.setCurrentPhase(Phase.GameOver)
                return true
            }
        }
        return currentPhase.getCurrentPhase() == Phase.GameOver
    }


    companion object {
        private const val MAX_HP_PLAYERS = 15
    }

    init {
        timeRemaining = preparationTime.toFloat()
        this.preparationTime = preparationTime
        myWizard = wizardState.getCurrentUserAsWizard()
        opponentWizard = wizardState.getEnemyAsWizard()
        gameId = game.id
        gameSubscription.addObserver(this)
        spellSubscription.addObserver(this)
        currentPhase.addObserver(this)
        gameListener = KtxAsync.launch {
            gameSubscription.subscribeToUpdatesOn(game.id)
        }
        if (GameData.instance.isHost) {
            LOG.info { "User is host -> pushing create turn" }
            createTurn(1)
        }
    }

    private fun createTurn(turnNumber: Int) {
        KtxAsync.launch {
            GameRepository().createTurn(turnNumber).collect {
                when (it) {
                    is State.Success -> {
                        LOG.info { "turn $turnNumber was created" }
                    }
                    is State.Failed -> {
                        LOG.error { it.message }
                    }
                    is State.Loading -> {
                        LOG.info { "Loading next turn" }
                    }
                }
            }
        }
    }
}
