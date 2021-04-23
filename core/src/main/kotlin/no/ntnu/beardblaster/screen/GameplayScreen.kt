package no.ntnu.beardblaster.screen

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.graphics.use
import ktx.log.debug
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
import no.ntnu.beardblaster.game.GameData
import no.ntnu.beardblaster.game.GameInstance
import no.ntnu.beardblaster.hud.SpellBar
import no.ntnu.beardblaster.hud.spellbar
import no.ntnu.beardblaster.sprites.WizardTexture
import no.ntnu.beardblaster.sprites.WizardTextures
import no.ntnu.beardblaster.ui.*

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
) : BaseScreen(game, batch, assets, camera) {
    private lateinit var gameInstance: GameInstance
    private lateinit var quitBtn: TextButton
    private lateinit var fireElementBtn: Button
    private lateinit var iceElementBtn: Button
    private lateinit var natureElementBtn: Button
    private lateinit var elementButtonsTable: Table
    private lateinit var spellBar: SpellBar
    private lateinit var spellInfo: SpellInfo
    private var goodWizard: WizardTexture = WizardTexture()
    private var evilWizard: WizardTexture = WizardTexture()
    private lateinit var hostLabel: Label
    private lateinit var opponentLabel: Label
    private lateinit var myHealthPointsLabel: Label
    private lateinit var opponentHealthPointsLabel: Label
    private lateinit var countDownLabel: Label
    private lateinit var headingLabel: Label
    private lateinit var waitingLabel: Label
    private lateinit var hostInfo: Table
    private lateinit var opponentInfo: Table

    override fun initComponents() {

        if(GameData.instance.game == null) {
            game.setScreen<MenuScreen>()
            return
        }

        gameInstance = GameInstance(30, GameData.instance.game!!)
        headingLabel = headingLabel(Nls.preparationPhase())
        hostLabel = bodyLabel("${gameInstance.wizardState.getCurrentUserAsWizard()}", 1.25f)
        myHealthPointsLabel = gameInstance.wizardState.getCurrentUserAsWizard()?.let { bodyLabel(it.getHealthPoints()) } ?: bodyLabel("Unknown")
        opponentLabel = bodyLabel("${GameData.instance.game?.opponent}", 1.25f)
        myHealthPointsLabel = gameInstance.wizardState.getEnemyAsWizard()?.let { bodyLabel(it.getHealthPoints()) } ?: bodyLabel("Unknown")

        hostInfo = scene2d.table {
            add(hostLabel)
            row()
            row()
            add(myHealthPointsLabel)
        }

        opponentInfo = scene2d.table {
            add(opponentLabel)
            row()
            row()
            add(opponentHealthPointsLabel)
        }

        hostInfo.setPosition(hostLabel.width + 10f, WORLD_HEIGHT / 2 + 50f)
        opponentInfo.setPosition(WORLD_WIDTH - 100f - opponentLabel.width, WORLD_HEIGHT / 2 + 50f)
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
        LOG.debug { "Gameplay screen" }
        initPreparationPhase()
        stage.addActor(quitBtn)
    }

    private fun initPreparationPhase() {
        spellBar.update()
        goodWizard.setAnimation(0f, 0f, assets, WizardTextures.GoodWizardIdle)
        evilWizard.setAnimation(0f, 0f, assets, WizardTextures.EvilWizardIdle)
        headingLabel.setText(Nls.preparationPhase())

        val table = fullSizeTable().apply {
            add(headingLabel).pad(50f)
            row()
        }
        stage.clear()
        addWizards()
        stage.addActor(table)
        stage.addActor(countDownLabel)
        stage.addActor(elementButtonsTable)
        stage.addActor(spellBar)
        stage.addActor(spellInfo)
        stage.addActor(quitBtn)
    }

    private fun initWaitingForPlayerPhase() {
        val table = fullSizeTable().apply {
            add(waitingLabel).pad(50f)
        }
        stage.clear()
        stage.addActor(table)
    }


    private fun initActionPhase() {
        headingLabel.setText(Nls.actionPhase())
        val table = fullSizeTable().apply {
            background = skin[Image.Background]
            add(headingLabel("Action Phase"))
        }
        stage.clear()
        addWizards()
        stage.addActor(table)
        stage.addActor(quitBtn)

        //If wizardA attacks first -> wizardA.setAnimation(Attack) + wizardB.setAnimation(takeHit)
        //If wizardA | B is dead -> setAnimation(dead)
        goodWizard.setAnimation(0f, 0f, assets, WizardTextures.GoodWizardAttack1)
        evilWizard.setAnimation(0f, 0f, assets, WizardTextures.EvilWizardTakeHit)
    }

    private fun initGameOver() {
        headingLabel.setText(Nls.gameOverPhase())

        val table = fullSizeTable().apply {
            add(headingLabel).pad(50f)
            row()
            add(quitBtn)
        }
        stage.clear()
        stage.addActor(table)
    }

    private fun addWizards() {
        stage.addActor(hostInfo)
        stage.addActor(opponentInfo)
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
           gameInstance.lockTurn()
        }
    }

    override fun update(delta: Float) {
        gameInstance.updateCounter(delta)

        when (gameInstance.currentPhase) {

            Phase.Preparation -> {
                myHealthPointsLabel.setText(gameInstance.wizardState.getCurrentUserAsWizard()?.getHealthPoints())
                opponentHealthPointsLabel.setText(gameInstance.wizardState.getEnemyAsWizard()?.getHealthPoints())
                countDownLabel.setText(gameInstance.timeRemaining.toInt().toString())
            }
            Phase.Waiting -> {

            }
            Phase.Action -> {
                gameInstance.spellsForTurn
            }

            Phase.GameOver -> {

            }
        }

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

            if (gameInstance.currentPhase != Phase.Preparation) {
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
}

