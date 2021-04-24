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
import no.ntnu.beardblaster.models.SpellCasting
import no.ntnu.beardblaster.screen.Phase
import no.ntnu.beardblaster.spell.SpellActionWithAnimation
import no.ntnu.beardblaster.spell.SpellExecutor
import no.ntnu.beardblaster.spell.SpellSubscription
import no.ntnu.beardblaster.spell.WizardState
import java.util.*

private val LOG = logger<GameInstance>();

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
    private var winnerWizard: Wizard? = null
    private var loosingWizard: Wizard? = null

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

            if(currentTurn == 0 && !::spellsListener.isInitialized) {
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
                        currentPhase.setCurrentPhase(Phase.GameOver)
                    }
                    is State.Failed -> {
                        LOG.error { "Failed to forfeit: ${it.message}" }
                    }
                }
            }
        }
    }

    // SpellSubscription, GamePhase and GameSubscription (Observables) may send updates through this channel
    override fun update(p0: Observable?, p1: Any?) {
        LOG.debug { "$p0 - $p1" }

        if (p0 is SpellSubscription) {
            LOG.debug { "p0 is SpellSubscription" }
            if (p1 is SpellAction && currentPhase.getCurrentPhase() != Phase.GameOver) {
                LOG.debug { "p1 is SpellAction" }
                // In this game, users actually send a spell to forfeit!
                // Since spells may only be sent by the users themselves (spell docId = userId),
                // Only the user themselves may send a "forfeit" spell
                if (p1.isForfeit) {
                    LOG.debug { "SpellAction was a forfeit spell!" }
                    when {
                        myWizard!!.id != p1.docId -> {
                            winnerWizard = myWizard
                            loosingWizard = opponentWizard
                        }
                        else -> {
                            winnerWizard = opponentWizard
                            loosingWizard = myWizard
                        }
                    }
                    currentPhase.setCurrentPhase(Phase.GameOver)
                    LOG.info { "Winner: ${winnerWizard?.displayName}" }
                    LOG.info { "Looser: ${loosingWizard?.displayName}" }
                } else {
                    LOG.debug { "!IMPORTANT - Pushing spell to executor: ${p1.spell.spellName}" }
                    spellExecutor.addSpell(p1, currentTurn)
                    if (spellExecutor.spellHistory[currentTurn]?.size == 2) {
                        // Both have sent their spells - let's fast forward to attack phase.
                        currentPhase.setCurrentPhase(Phase.Action)
                    }
                }
            }
        }
        if (p0 is GameSubscription) {
            if (p1 is Game) {
                if (p1.loot.isNotEmpty()) {
                    LOG.info { "Prices found! Adding all prices to map." }
                    gameLoot.setLoot(p1.loot)
                    currentPhase.setCurrentPhase(Phase.GameOver)
                } else if (p1.endedAt != 0L) {
                    LOG.info { "Game was ended since endedAt was > 0" }
                    currentPhase.setCurrentPhase(Phase.GameOver)
                }
            }
        }

        if(p0 is GamePhase) {
            if (p1 is Phase) {
                when(p1) {
                    Phase.GameOver -> {
                        spellsListener.cancel()
                        if (GameData.instance.isHost && gameLoot.getLoot().isEmpty()) {
                            generateLoot()
                        }
                    }
                    Phase.Preparation -> {
                        if(!isGameOver()) {
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
                        resetCounter()
                        spellCasting.reset()
                        // Cancel subscription to events on from preparation turn
                        spellsListener.cancel()
                        // Set up next turn if host
                        if (GameData.instance.isHost) {
                            createTurn(currentTurn + 1)
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

    private fun generateLoot() {
        var winnerLoot: List<Loot>
        var looserLoot: List<Loot>

        if (winnerWizard == null || loosingWizard == null) {
            if (wizardState.opponents[myWizard!!.id]!!.isWizardDefeated()) {
                winnerWizard = opponentWizard
                loosingWizard = myWizard
            } else if (wizardState.opponents[opponentWizard!!.id]!!.isWizardDefeated()) {
                winnerWizard = myWizard
                loosingWizard = opponentWizard
            }
        }

        if(currentTurn < 3) {
            winnerLoot = GamePrizeList().getWinnerPrizes(1, 0)
            looserLoot = GamePrizeList().getLooserPrizes(1, -5)
        } else {
            winnerLoot = GamePrizeList().getWinnerPrizes(Random().nextInt(3), 7)
            winnerLoot.forEach {
                it.receiver = winnerWizard!!.id
            }
            looserLoot = GamePrizeList().getLooserPrizes(Random().nextInt(3), -5)
            looserLoot.forEach {
                it.receiver = loosingWizard!!.id
            }
        }

        KtxAsync.launch {
            GameRepository().distributeLoot(winnerLoot.plus(looserLoot)).collect {
                when (it) {
                    is State.Success -> {
                        LOG.debug { "Distributed loot successfully" }
                    }
                    is State.Failed -> {
                        LOG.debug { "Failed to distribute loot: ${it.message}" }
                    }
                    is State.Loading -> {
                        LOG.debug { "Distributing loot.." }
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

    private fun isGameOver() : Boolean {
        if (currentPhase.getCurrentPhase() != Phase.GameOver) {
            if (wizardState.isAnyWizardDead()) {
                LOG.info { "A wizard is dead! Game is over!" }
                currentPhase.setCurrentPhase(Phase.GameOver)
                return true;
            }
        }
        return false;
    }


    companion object {
        private const val MAX_HP_PLAYERS = 30
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
