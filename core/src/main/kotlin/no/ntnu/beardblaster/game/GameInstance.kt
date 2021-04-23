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
import no.ntnu.beardblaster.commons.game.Prize
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

    var lastActionTurn: Int = 0
    private lateinit var spellsListener: Job
    private var gameListener: Job
    lateinit var gamePrizes: List<Prize>
    private var winnerWizard: Wizard? = null
    private var loosingWizard: Wizard? = null
    val spellCasting: SpellCasting = SpellCasting()
    private val spellExecutor: SpellExecutor = SpellExecutor()
    private val spellSubscription: SpellSubscription = SpellSubscription()
    private val gameSubscription: GameSubscription = GameSubscription()
    val wizardState: WizardState = WizardState(
        Wizard(MAX_HP_PLAYERS, game.host!!),
        Wizard(MAX_HP_PLAYERS, game.opponent!!)
    )
    private val myWizard: Wizard?
    private val opponentWizard: Wizard?
    var currentPhase = Phase.Preparation
        private set
    var timeRemaining: Float
        private set
    var currentTurn = 0
        private set
    private val preparationTime: Int
    var spellsForTurn: List<SpellActionWithAnimation>? = null
        private set
    private var lastFetchedActionsInTurn = 1
    private val gameId: String

    fun updateCounter(delta: Float) {
        when (currentPhase) {
            Phase.Action ->                 // Fetch actions once.
                if (currentTurn == lastFetchedActionsInTurn) {
                    spellsForTurn = ArrayList()
                    resetCounter()
                    spellCasting.reset()
                    // Cancel subscription to events on from preparation turn
                    spellsListener.cancel()
                    spellsForTurn = spellExecutor.getSpellResultForTurn(currentTurn, wizardState)
                    lastFetchedActionsInTurn += 1

                    // Set up next turn
                    if(GameData.instance.isHost) {
                        KtxAsync.launch {
                            GameRepository().createTurn(currentTurn + 1).collect {
                                when(it) {
                                    is State.Success -> {
                                        LOG.info { "Next turn was created" }
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
            Phase.Waiting -> {

            }
            Phase.GameOver -> {
                if(gameListener.isActive) {
                    gameListener.cancel()
                }
            }
            Phase.Preparation -> {
                // Increment turn after action phase
                if (currentTurn < lastFetchedActionsInTurn) {
                    incrementCurrentTurn()
                    // Subscribe to events on this turn
                    spellsListener = KtxAsync.launch {
                        spellSubscription.subscribeToUpdatesOn("games/$gameId/turns/$currentTurn/spells")
                    }
                }
                timeRemaining -= delta * 0.5f
                if (timeRemaining <= 0) {
                    currentPhase = Phase.Action
                }
            }
        }
    }


    private fun resetCounter() {
        timeRemaining = preparationTime.toFloat()
    }

    private fun incrementCurrentTurn() {
        currentTurn += 1
    }

    fun lockTurn() : Flow<State<SpellAction>> {
        LOG.info { "Locking spell -> ${spellCasting.getSelectedSpell()!!.spellName}" }
        return GameRepository().castSpell(currentTurn, SpellAction(
            spellCasting.getSelectedSpell()!!, myWizard!!.id,
            opponentWizard!!.id
        ))
    }

    fun forfeit() {
        val forfeitSpell = SpellAction()
        forfeitSpell.isForfeit = true
        GameRepository().castSpell(currentTurn, forfeitSpell)
    }

    // SpellSubscription and GameSubscription may send updates through this channel
    override fun update(p0: Observable?, p1: Any?) {
        if (p0 is SpellSubscription) {
            LOG.debug { "Got spell from SpellSubscription" }
            if (p1 is SpellAction) {
                LOG.debug { "Got SpellAction from SpellSubscription" }

                // In this game, users actually send a spell to forfeit!
                // Since spells may only be sent by the users themselves (spell docId = userId),
                // Only the user themselves may send a "forfeit" spell
                if (p1.isForfeit) {
                    LOG.debug { "SpellAction was a forfeit spell!" }

                    currentPhase = Phase.GameOver
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
                    if (GameData.instance.isHost) {
                        distributePrizes()
                    }
                } else {
                    LOG.debug { "Calling spell executor" }

                    spellExecutor.addSpell(p1, currentTurn)
                    if(spellExecutor.spellHistory[currentTurn]?.size == 2) {
                        // Both have sent their spells - let's fast forward to attack phase.
                        currentPhase = Phase.Action
                    }
                }
            }
        }

        if (p0 is GameSubscription) {
            if (p1 is Game) {
                if (p1.prizes.isNotEmpty()) {
                    gamePrizes = p1.prizes
                }
                if (p1.endedAt != 0L) {
                    if (currentPhase != Phase.GameOver) {
                        currentPhase = Phase.GameOver
                    }
                }
            }
        }
    }

    private fun distributePrizes() {
        if (winnerWizard == null || loosingWizard == null) {
            if (wizardState.opponents[myWizard!!.id]!!.isWizardDefeated()) {
                winnerWizard = opponentWizard
                loosingWizard = myWizard
            } else if (wizardState.opponents[opponentWizard!!.id]!!.isWizardDefeated()) {
                winnerWizard = myWizard
                loosingWizard = opponentWizard
            }
        }
        val winnerLoot: List<Prize> = GamePrizeList().getWinnerPrizes(Random().nextInt(3))
        winnerLoot.forEach {
            it.receiver = winnerWizard!!.id
        }
        val looserLoot: List<Prize> = GamePrizeList().getLooserPrizes(Random().nextInt(3))
        looserLoot.forEach {
            it.receiver = loosingWizard!!.id
        }
        GameRepository().distributePrizes(winnerLoot.plus(looserLoot))
    }

    fun dispose() {
        if(gameListener.isActive) {
            gameListener.cancel()
        }

        if(spellsListener.isActive) {
            spellsListener.cancel()
        }
    }

    fun resetPhase() {
        currentPhase = if(wizardState.isAnyWizardDead()) {
            LOG.info { "A wizard is dead! Game is over!" }
            if(GameData.instance.isHost) {
                distributePrizes()
                Phase.Waiting
            } else {
                Phase.Waiting
            }
        } else {
            LOG.info { "Resetting phase to PREPARATION" }
            Phase.Preparation
        }
    }

    fun incrementActionTurn() {
        lastActionTurn += 1;
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
        gameListener = KtxAsync.launch {
            gameSubscription.subscribeToUpdatesOn(game.id)
        }

        if(GameData.instance.isHost) {
            LOG.info { "User is host -> pushing create turn"}
            KtxAsync.launch {
                GameRepository().createTurn(1).collect {
                    when(it) {
                        is State.Success -> {
                            LOG.info { "Initial turn created successfully" }
                        }
                        is State.Loading -> {
                            LOG.info { "Loading turn" }
                        }
                        is State.Failed -> {
                            LOG.error { "Failed to create turn: ${it.message}" }
                        }
                    }
                }
            }
        }
    }
}
