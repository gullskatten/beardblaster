package no.ntnu.beardblaster.game

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
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

@ExperimentalCoroutinesApi
class GameInstance(preparationTime: Int, game: Game) : Observer {

    private lateinit var spellsInTurnSubscription: Job
    private var gameSubscription: Job
    private lateinit var gamePrizes: List<Prize>
    private var winnerWizard: Wizard? = null
    private var loosingWizard: Wizard? = null
    val spellCasting: SpellCasting = SpellCasting()
    val spellExecutor: SpellExecutor = SpellExecutor()
    val wizardState: WizardState
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
                    spellsInTurnSubscription.cancel()
                    spellsForTurn = spellExecutor.getSpellResultForTurn(currentTurn, wizardState)
                    lastFetchedActionsInTurn += 1
                }
            Phase.Waiting -> {

            }
            Phase.GameOver -> {
                if(gameSubscription.isActive) {
                    gameSubscription.cancel()
                }
            }
            Phase.Preparation -> {
                // Increment turn after action phase
                if (currentTurn < lastFetchedActionsInTurn) {
                    incrementCurrentTurn()
                    // Subscribe to events on this turn
                    spellsInTurnSubscription = KtxAsync.launch {
                        SpellSubscription().subscribeToUpdatesOn("games/$gameId/turns/$currentTurn/spells")
                    }
                }
                timeRemaining -= delta
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

    fun lockTurn() {
        val spell = SpellAction(
            spellCasting.getSelectedSpell()!!, myWizard!!.id,
            opponentWizard!!.id
        )

        KtxAsync.launch {
            GameRepository().castSpell(currentTurn, spell)
        }
    }

    fun forfeit() {
        val forfeitSpell = SpellAction()
        forfeitSpell.isForfeit = true
        GameRepository().castSpell(currentTurn, forfeitSpell)
    }

    // SpellSubscription and GameSubscription may send updates through this channel
    override fun update(p0: Observable?, p1: Any?) {
        if (p0 is SpellSubscription) {
            if (p1 is SpellAction) {
                // In this game, users actually send a spell to forfeit!
                // Since spells may only be sent by the users themselves (spell docId = userId),
                // Only the user themselves may send a "forfeit" spell
                if (p1.isForfeit) {
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

    companion object {
        private const val MAX_HP_PLAYERS = 30
    }

    init {
        wizardState = WizardState(
            Wizard(MAX_HP_PLAYERS, game.host!!),
            Wizard(MAX_HP_PLAYERS, game.opponent!!)
        )

        timeRemaining = preparationTime.toFloat()
        this.preparationTime = preparationTime
        myWizard = wizardState.getCurrentUserAsWizard()
        opponentWizard = wizardState.getEnemyAsWizard()
        gameId = game.id

        gameSubscription = KtxAsync.launch {
            GameSubscription().subscribeToUpdatesOn(game.id)
        }
    }

    fun dispose() {
        if(gameSubscription.isActive) {
            gameSubscription.cancel()
        }

        if(spellsInTurnSubscription.isActive) {
            spellsInTurnSubscription.cancel()
        }
    }
}
