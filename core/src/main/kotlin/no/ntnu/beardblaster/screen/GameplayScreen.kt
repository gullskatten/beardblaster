package no.ntnu.beardblaster.screen

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.graphics.use
import ktx.log.debug
import ktx.log.logger
import ktx.scene2d.*
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.ElementType
import no.ntnu.beardblaster.WORLD_HEIGHT
import no.ntnu.beardblaster.WORLD_WIDTH
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.wizard.Wizard
import no.ntnu.beardblaster.game.GameData
import no.ntnu.beardblaster.hud.SpellBar
import no.ntnu.beardblaster.hud.spellbar
import no.ntnu.beardblaster.models.SpellCasting
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
    private val PREPARATION_TIME = 10f
    private lateinit var hostWizard: Wizard
    private lateinit var opponentWizard: Wizard
    private lateinit var quitBtn: TextButton
    private lateinit var fireElementBtn: Button
    private lateinit var iceElementBtn: Button
    private lateinit var natureElementBtn: Button
    private lateinit var elementButtonsTable: Table
    private lateinit var spellBar: SpellBar
    private lateinit var spellInfo: SpellInfo
    private lateinit var spellCasting: SpellCasting
    private var goodWizard: WizardTexture = WizardTexture()
    private var evilWizard: WizardTexture = WizardTexture()
    private lateinit var hostLabel: Label
    private lateinit var opponentLabel: Label
    private lateinit var hostHP: Label
    private lateinit var opponentHP: Label
    private lateinit var countDownLabel: Label
    private lateinit var headingLabel: Label
    private lateinit var waitingLabel: Label
    private lateinit var hostInfo: Table
    private lateinit var opponentInfo: Table

    private var countDown = PREPARATION_TIME
    private var currentPhase: Phase = Phase.Preparation
    private var currentTurn = 1
    private var canDo = false // TODO: To be removed when things come together properly
    private var hostHealthbar = Healthbar()
    private val opponentHealthbar = Healthbar()


    override fun initComponents() {
        LOG.debug { "Set up components" }
        hostWizard = Wizard(0f)
        opponentWizard = Wizard(0f)
        spellCasting = SpellCasting()

        headingLabel = headingLabel(Nls.preparationPhase())
        hostLabel = bodyLabel("${GameData.instance.game?.host}", 1.25f)
        hostHP = bodyLabel(hostWizard.getCurrentHPString())
        opponentLabel = bodyLabel("${GameData.instance.game?.opponent}", 1.25f)
        opponentHP = bodyLabel(opponentWizard.getCurrentHPString())

        hostInfo = scene2d.table {
            add(hostLabel)
            row()
            row()
            add(hostHealthbar.healthbarContainer).width(300f).height(70f)
        }
        opponentInfo = scene2d.table {
            add(opponentLabel)
            row()
            row()
            add(opponentHealthbar.healthbarContainer).width(300f).height(70f)
        }

        hostInfo.setPosition(hostLabel.width + 100f, WORLD_HEIGHT / 2 + 50f)
        opponentInfo.setPosition(WORLD_WIDTH - 100f - opponentLabel.width, WORLD_HEIGHT / 2 + 50f)
        waitingLabel = headingLabel(Nls.waitingPhase())

        countDownLabel = headingLabel(countDown.toInt().toString())
        countDownLabel.setPosition(10f, WORLD_HEIGHT - countDownLabel.height - 100f)

        quitBtn = scene2d.textButton(Nls.quit())
        quitBtn.setPosition(WORLD_WIDTH - quitBtn.width - 50f, WORLD_HEIGHT - quitBtn.height - 50f)
        fireElementBtn = scene2d.button(ElementType.Fire.name)
        iceElementBtn = scene2d.button(ElementType.Ice.name)
        natureElementBtn = scene2d.button(ElementType.Nature.name)

        spellInfo = scene2d.spellInfo(spellCasting) {
            setPosition(
                (WORLD_WIDTH / 2) - (width / 2),
                (WORLD_HEIGHT / 2) - (height / 2),
            )
        }
        spellBar = scene2d.spellbar(spellCasting = spellCasting)
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
        LOG.debug { "Init $currentPhase turn $currentTurn" }
        spellCasting.reset()
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
        LOG.debug { "Init $currentPhase turn $currentTurn" }

        val table = fullSizeTable().apply {
            add(waitingLabel).pad(50f)
        }
        stage.clear()
        addWizards()
        stage.addActor(table)
    }


    private fun initActionPhase() {
        LOG.debug { "Init $currentPhase turn $currentTurn" }
        headingLabel.setText(Nls.actionPhase())

        val table = fullSizeTable().apply {
            background = skin[Image.Background]
            add(headingLabel("FIGHT"))
            row()
            row()
            row()
            add(bodyLabel("BOOM, BLAST, POOF, BEARD GONE"))
        }

        stage.clear()
        addWizards()
        stage.addActor(countDownLabel)
        stage.addActor(table)
        stage.addActor(quitBtn)

        //If wizardA attacks first -> wizardA.setAnimation(Attack) + wizardB.setAnimation(takeHit)
        //If wizardA | B is dead -> setAnimation(dead)
        goodWizard.setAnimation(0f, 0f, assets, WizardTextures.GoodWizardAttack1)
        evilWizard.setAnimation(0f, 0f, assets, WizardTextures.EvilWizardTakeHit)
    }

    private fun initGameOver() {
        LOG.debug { "Init $currentPhase" }
        headingLabel.setText(Nls.gameOverPhase())

        val table = fullSizeTable().apply {
            add(headingLabel).pad(50f)
            row()
            add(quitBtn)
        }
        stage.clear()
        addWizards()
        stage.addActor(table)
    }

    private fun addWizards() {
        stage.addActor(hostInfo)
        stage.addActor(opponentInfo)
    }

    override fun setBtnEventListeners() {
        quitBtn.onClick {
            // TODO: Notify firebase of player leaving
            game.removeScreen<GameplayScreen>()
            game.addScreen(GameplayScreen(game, batch, assets, camera))
            game.setScreen<MenuScreen>()
        }

        fireElementBtn.onClick {
            hostWizard.updateHP(-1)
            spellCasting.addFire()
        }
        iceElementBtn.onClick {
            spellCasting.addIce()
        }
        natureElementBtn.onClick {
            hostWizard.updateHP(1)
            spellCasting.addNature()
        }

        spellInfo.lockBtn.onClick {
            LOG.debug { "Wizard locks selected spell" }
        }
    }

    override fun update(delta: Float) {
        when (currentPhase) {
            Phase.Preparation -> {
                hostHealthbar.updateWidth(hostWizard.currentHP, hostWizard.maxHP)
                opponentHealthbar.updateWidth(opponentWizard.currentHP, opponentWizard.maxHP)
                countDown -= delta * 0.5f

                if (countDown <= PREPARATION_TIME) {
                    countDownLabel.setText(countDown.toInt().toString())
                }

                if (countDown <= 0) {
                    currentPhase = Phase.Waiting
                    initWaitingForPlayerPhase()
                    KtxAsync.launch {
                        GameData.instance.createTurn(currentTurn)
                            ?.collect { it ->
                                when (it) {
                                    is State.Loading -> {
                                        waitingLabel.setText("Creating turn")
                                    }
                                    is State.Failed -> {
                                        waitingLabel.setText(it.message)
                                    }
                                    is State.Success -> {
                                        KtxAsync.launch {
                                            GameData.instance.endTurn(
                                                currentTurn,
                                                spellCasting.getSelectedSpell()
                                            )
                                                ?.collect { iti ->
                                                    when (iti) {
                                                        is State.Loading -> {
                                                            waitingLabel.setText("Ending your turn")
                                                        }
                                                        is State.Failed -> {
                                                            waitingLabel.setText(iti.message)
                                                        }
                                                        is State.Success -> {
                                                            currentTurn += 1
                                                        }
                                                    }
                                                }
                                        }
                                    }
                                }
                            }
                    }
                }
            }
            Phase.Waiting -> {
                // TODO: Wait for other player and fetch the latest game updates
                currentPhase = Phase.Action

                initActionPhase()

                countDown = PREPARATION_TIME
            }
            Phase.Action -> {
                // Simulate time taken for animations to fly across the screen
                countDown -= delta
                if (countDown <= PREPARATION_TIME) {
                    countDownLabel.setText(countDown.toInt().toString())
                }

                if (countDown <= 0) {
                    currentPhase = Phase.Preparation
                    initPreparationPhase()
                    countDown = PREPARATION_TIME
                }
            }
            Phase.GameOver -> {

            }
        }
        if (currentTurn > 3 && !canDo) {
            // Temporary solution to get game over
            currentPhase = Phase.GameOver
            initGameOver()
            canDo = true
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

            if (currentPhase != Phase.Preparation) {
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
}

