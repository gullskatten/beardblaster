package no.ntnu.beardblaster.screen

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.assets.disposeSafely
import ktx.async.KtxAsync
import ktx.graphics.use
import ktx.log.debug
import ktx.log.error
import ktx.log.info
import ktx.log.logger
import ktx.scene2d.button
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.ElementType
import no.ntnu.beardblaster.WORLD_HEIGHT
import no.ntnu.beardblaster.WORLD_WIDTH
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.game.GameData
import no.ntnu.beardblaster.game.GameInstance
import no.ntnu.beardblaster.game.GamePhase
import no.ntnu.beardblaster.hud.SpellBar
import no.ntnu.beardblaster.hud.spellbar
import no.ntnu.beardblaster.spell.SpellLockState
import no.ntnu.beardblaster.sprites.WizardTexture
import no.ntnu.beardblaster.sprites.WizardTextures
import no.ntnu.beardblaster.ui.*
import no.ntnu.beardblaster.user.UserData
import java.util.*
import kotlin.concurrent.schedule

private val LOG = logger<GameplayScreen>()

enum class Phase {
    Preparation,
    Waiting,
    Action,
    GameOver
}

class GameplayScreen(
    game: BeardBlasterGame,
    batch: SpriteBatch,
    assets: AssetStorage,
    camera: OrthographicCamera,
) : BaseScreen(game, batch, assets, camera), Observer {
    private lateinit var gameInstance: GameInstance
    private lateinit var quitBtn: TextButton
    private lateinit var fireElementBtn: Button
    private lateinit var iceElementBtn: Button
    private lateinit var natureElementBtn: Button
    private lateinit var elementButtonsTable: Table
    private lateinit var spellBar: SpellBar
    private lateinit var spellInfo: SpellInfo
    private var spellAction: SpellActionDialog = scene2d.spellActionsDialog {
        setPosition(
            (WORLD_WIDTH / 2) - (width / 2),
            (WORLD_HEIGHT / 2) - (height / 2),
        )
    }
    private lateinit var lootDialog: LootDialog
    private var goodWizard: WizardTexture = WizardTexture()
    private var evilWizard: WizardTexture = WizardTexture()
    private lateinit var hostLabel: Label
    private lateinit var opponentLabel: Label
    private lateinit var myHealthPointsLabel: Label
    private lateinit var opponentHealthPointsLabel: Label
    private lateinit var countDownLabel: Label
    private lateinit var headingLabel: Label
    private lateinit var waitingLabel: Label
    private lateinit var myHealthPointsTable: Table
    private lateinit var opponentHealthPointsTable: Table
    private var hostHealthbar = Healthbar()
    private val opponentHealthbar = Healthbar()

    override fun initComponents() {

        if (GameData.instance.game == null) {
            game.setScreen<MenuScreen>()
            return
        }

        gameInstance = GameInstance(160, GameData.instance.game!!)
        gameInstance.currentPhase.addObserver(this)
        headingLabel = headingLabel(Nls.preparationPhase())
        hostLabel = bodyLabel("${gameInstance.wizardState.getCurrentUserAsWizard()}", 1.25f)
        myHealthPointsLabel = gameInstance.wizardState.getCurrentUserAsWizard()
            ?.let { bodyLabel(it.getHealthPoints()) } ?: bodyLabel("Unknown")
        opponentLabel = bodyLabel("${gameInstance.wizardState.getEnemyAsWizard()}", 1.25f)
        opponentHealthPointsLabel =
            gameInstance.wizardState.getEnemyAsWizard()?.let { bodyLabel(it.getHealthPoints()) }
                ?: bodyLabel("Unknown")

        myHealthPointsTable = scene2d.table {
            add(hostLabel)
            row()
            add(myHealthPointsLabel).center()
            row()
            add(hostHealthbar.healthbarContainer).width(300f).height(70f)
        }

        opponentHealthPointsTable = scene2d.table {
            add(opponentLabel)
            row()
            add(opponentHealthPointsLabel).center()
            row()
            add(opponentHealthbar.healthbarContainer).width(300f).height(70f)
        }

        myHealthPointsTable.setPosition(hostLabel.width + 100f, WORLD_HEIGHT / 2 + 50f)
        opponentHealthPointsTable.setPosition(
            WORLD_WIDTH - 100f - opponentLabel.width,
            WORLD_HEIGHT / 2 + 50f
        )
        waitingLabel = headingLabel(Nls.waitingPhase())
        countDownLabel = headingLabel(gameInstance.timeRemaining.toInt().toString())
        countDownLabel.setPosition(10f, WORLD_HEIGHT - countDownLabel.height - 100f)

        quitBtn = scene2d.textButton(Nls.quit())
        quitBtn.setPosition(WORLD_WIDTH - quitBtn.width - 50f, WORLD_HEIGHT - quitBtn.height - 50f)
        fireElementBtn = scene2d.button(ElementType.Fire.name)
        iceElementBtn = scene2d.button(ElementType.Ice.name)
        natureElementBtn = scene2d.button(ElementType.Nature.name)
        spellInfo = scene2d.spellInfo(gameInstance.spellCasting) {
            setPosition(
                (WORLD_WIDTH / 2) - (width / 2),
                (WORLD_HEIGHT / 2) - (height / 2),
            )
        }
        spellBar = scene2d.spellbar(spellCasting = gameInstance.spellCasting)
        spellBar.setPosition(WORLD_WIDTH / 2 - spellBar.width, spellBar.height + 130f)
        val elementBtnSize = 200f
        elementButtonsTable = scene2d.table {
            add(scene2d.table {
                background = skin[Image.ModalSkull]
                padTop(50f)
                add(fireElementBtn).width(elementBtnSize).height(elementBtnSize).colspan(2).center()
                row()
                add(iceElementBtn).width(elementBtnSize).height(elementBtnSize).colspan(1)
                add(natureElementBtn).width(elementBtnSize).height(elementBtnSize).colspan(1)
            }).width(elementBtnSize * 2)
        }
        elementButtonsTable.setPosition(
            WORLD_WIDTH - 210f,
            220f
        )
    }

    override fun initScreen() {
        initPreparationPhase()
        stage.addActor(quitBtn)
    }

    private fun initPreparationPhase() {
        LOG.debug { "INIT GAME OVER PHASE" }
        spellInfo.updateButtonLabel(SpellLockState.UNLOCKED)
        stage.clear()
        spellBar.update()
        goodWizard.setAnimation(0f, 0f, assets, WizardTextures.GoodWizardIdle)
        evilWizard.setAnimation(0f, 0f, assets, WizardTextures.EvilWizardIdle)
        headingLabel.setText(Nls.preparationPhase())

        val table = fullSizeTable().apply {
            add(headingLabel).pad(50f)
            row()
        }
        addWizards()
        stage.addActor(table)
        stage.addActor(countDownLabel)
        stage.addActor(elementButtonsTable)
        stage.addActor(spellBar)
        stage.addActor(spellInfo)
        stage.addActor(quitBtn)
        myHealthPointsLabel.setText(
            gameInstance.wizardState.getCurrentUserAsWizard()?.getHealthPoints()
        )
        opponentHealthPointsLabel.setText(
            gameInstance.wizardState.getEnemyAsWizard()?.getHealthPoints()
        )
        hostHealthbar.updateWidth(gameInstance.wizardState.getCurrentUserAsWizard()!!.currentHealthPoints, gameInstance.wizardState.getCurrentUserAsWizard()!!.maxHealthPoints)
        opponentHealthbar.updateWidth(gameInstance.wizardState.getEnemyAsWizard()!!.currentHealthPoints, gameInstance.wizardState.getEnemyAsWizard()!!.maxHealthPoints)
    }

    private fun initWaitingForPlayerPhase() {
        val table = fullSizeTable().apply {
            add(waitingLabel).pad(50f)
        }
        stage.clear()
        stage.addActor(table)
    }

    private fun initActionPhase() {
        LOG.debug { "INIT ACTION PHASE" }
        spellInfo.updateButtonLabel(SpellLockState.UNLOCKED)
        headingLabel.setText(Nls.actionPhase())
        stage.clear()

        addWizards()
        val table = fullSizeTable().apply {
            background = skin[Image.Background]
            add(headingLabel("Action Phase"))
            row()
            add(spellAction).center()
            row()
            add(myHealthPointsTable).left()
            add(opponentHealthPointsTable).right()
        }
        stage.addActor(table)
        stage.addActor(quitBtn)
        cycleSpells()
    }

    private fun cycleSpells() {
        LOG.debug { "CYCLING SPELLS (ANIMATION) " }
        val spellsThisTurn = gameInstance.getSpellsCastCurrentTurn()
        LOG.debug { "Got spells this turn - ${spellsThisTurn.size} spells" }
        if (spellsThisTurn.isNotEmpty()) {
            spellsThisTurn.forEachIndexed { idx, spell ->
                try {
                    Timer("HealthPointUpdate", true).schedule(3000 * idx.toLong()) {
                        LOG.info { "Pushing health point updates!" }
                        if (spell.caster == UserData.instance.user!!.id) {
                            myHealthPointsLabel.setText(spell.casterWizard?.getHealthPoints())
                            opponentHealthPointsLabel.setText(spell.receiverWizard?.getHealthPoints())
                            hostHealthbar.updateWidth(spell.casterWizard!!.currentHealthPoints, spell.casterWizard!!.maxHealthPoints)
                            opponentHealthbar.updateWidth(spell.receiverWizard!!.currentHealthPoints, spell.receiverWizard!!.maxHealthPoints)
                        } else {
                            myHealthPointsLabel.setText(spell.receiverWizard?.getHealthPoints())
                            opponentHealthPointsLabel.setText(spell.casterWizard?.getHealthPoints())
                            hostHealthbar.updateWidth(spell.receiverWizard!!.currentHealthPoints, spell.receiverWizard!!.maxHealthPoints)
                            opponentHealthbar.updateWidth(spell.casterWizard!!.currentHealthPoints, spell.casterWizard!!.maxHealthPoints)
                        }
                    }
                } catch (e: Exception) {
                    LOG.error { "Health Timer failed: ${e.message}" }
                }

                try {
                    Timer("SpellDialog", true).schedule(4000 * idx.toLong()) {
                        LOG.info { "Updating spell dialog" }
                        spellAction.updateNameLabelText(spell.casterWizard?.displayName ?: "Unknown?")
                        spellAction.updateDescLabelText(spell.toString())

                        goodWizard.setAnimation(0f, 0f, assets, spell.determineMyAnimation())
                        evilWizard.setAnimation(0f, 0f, assets, spell.determineEnemyAnimation())
                        goodWizard.update(deltaTime = 0f)
                        evilWizard.update(deltaTime = 0f)
                    }
                } catch (e: Exception) {
                    LOG.error { "SpellDialog Timer failed: ${e.message}" }
                }
            }
            try {
                val delay = 4000L * (spellsThisTurn.size)
                LOG.info { "Swapping to prepare phase in $delay milliseconds" }
                Timer("SwapToPreparePhase", true).schedule(delay) {
                   gameInstance.currentPhase.setCurrentPhase(Phase.Preparation)
                }
            } catch (e: Exception) {
                LOG.error { "Phasing Timer failed: ${e.message}" }
            }
        } else {
            spellAction.updateNameLabelText("Both wizards were idle!")
            spellAction.updateDescLabelText("It looks like no wizards wanted to cast any spells this turn. Moving back..")
            Timer("SwapToPreparePhase", true).schedule(4000L) {
                gameInstance.currentPhase.setCurrentPhase(Phase.Preparation)
            }
        }
    }

    private fun initGameOver() {
        LOG.debug { "INIT GAME OVER PHASE" }
        stage.clear()
        headingLabel.setText(Nls.gameOverPhase())

       lootDialog = scene2d.lootDialog(gameInstance.gameLoot) {
            setPosition(
                (WORLD_WIDTH / 2) - (width / 2),
                (WORLD_HEIGHT / 2) - (height / 2),
            )
        }

        lootDialog.closeBtn.onClick {
            disposeSafely()
            game.setScreen<MenuScreen>()
        }
        addWizards()

        val table = fullSizeTable().apply {
            add(headingLabel).pad(50f)
            row()
            add(lootDialog)
            row()
        }
        stage.addActor(table)
    }

    private fun addWizards() {
        stage.addActor(myHealthPointsTable)
        stage.addActor(opponentHealthPointsTable)
    }

    override fun setBtnEventListeners() {
        quitBtn.onClick {
            gameInstance.forfeit()
        }
        fireElementBtn.onClick {
            gameInstance.spellCasting.addFire()
        }
        iceElementBtn.onClick {
            gameInstance.spellCasting.addIce()
        }
        natureElementBtn.onClick {
            gameInstance.spellCasting.addNature()
        }

        spellInfo.lockBtn.onClick {
            LOG.debug { "User locked turn!" }
            KtxAsync.launch {
                gameInstance.lockTurn().collect {
                    when (it) {
                        is State.Success -> {
                            spellInfo.updateButtonLabel(SpellLockState.LOCKED)
                        }
                        is State.Loading -> {
                            spellInfo.updateButtonLabel(SpellLockState.LOCKING)
                        }
                        is State.Failed -> {
                            spellInfo.updateButtonLabel(SpellLockState.FAILED)
                        }
                    }
                }
            }
        }
    }

    override fun update(delta: Float) {
        gameInstance.updateCounter(delta)
        countDownLabel.setText(gameInstance.timeRemaining.toInt().toString())
        camera.update()
        goodWizard.update(delta)
        evilWizard.update(delta)
    }

    override fun additionalRender(delta: Float) {
        update(delta)
        stage.act(delta)
        stage.draw()

        batch.use {
            it.projectionMatrix = camera.combined
            if (goodWizard.getWizard() != null) {
                it.draw(
                    goodWizard.getWizard(),
                    -400f,
                    -230f,
                    goodWizard.getBounds().width * 5,
                    goodWizard.getBounds().height * 5
                )

            }
            if (gameInstance.currentPhase.getCurrentPhase() != Phase.Preparation) {
                if (evilWizard.getWizard() != null) {
                    it.draw(
                        evilWizard.getWizard(),
                        2300f,
                        -370f,
                        -evilWizard.getBounds().width * 8,
                        evilWizard.getBounds().height * 8
                    )
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun dispose() {
        super.dispose()
        gameInstance.dispose();
    }

    override fun update(p0: Observable?, p1: Any?) {
        LOG.debug { "$p0 - $p1" }
        if(p0 is GamePhase && p1 is Phase) {
            when(p1) {
                Phase.GameOver -> {
                    LOG.debug { " CALLING GAME OVER PHASE " }
                    initGameOver()
                }
                Phase.Action -> {
                    LOG.debug { " CALLING ACTION PHASE " }
                    initActionPhase()
                }
                Phase.Preparation -> {
                    LOG.debug { " CALLING PREPARATION PHASE " }
                    initPreparationPhase()
                    }
                Phase.Waiting -> {
                    LOG.debug { " CALLING WAITING PHASE " }
                    initWaitingForPlayerPhase()
                }
            }
        }
    }
}

