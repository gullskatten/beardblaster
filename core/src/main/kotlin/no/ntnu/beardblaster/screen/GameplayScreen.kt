package no.ntnu.beardblaster.screen

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
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
import no.ntnu.beardblaster.WORLD_WIDTH
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.hud.SpellBar
import no.ntnu.beardblaster.hud.spellbar
import no.ntnu.beardblaster.models.SpellCasting
import no.ntnu.beardblaster.sprites.WizardTexture
import no.ntnu.beardblaster.sprites.WizardTextures
import no.ntnu.beardblaster.ui.*

private val log = logger<GameplayScreen>()

class GameplayScreen(
    game: BeardBlasterGame,
    batch: SpriteBatch,
    assets: AssetStorage,
    camera: OrthographicCamera,
) : BaseScreen(game, batch, assets, camera) {
    private lateinit var quitBtn: TextButton
    private lateinit var fireElementBtn: Button
    private lateinit var iceElementBtn: Button
    private lateinit var natureElementBtn: Button
    private lateinit var spellBar: SpellBar
    private lateinit var spellCasting: SpellCasting
    private var countDownTimer = 5f
    private var goodWizard: WizardTexture = WizardTexture()
    private var evilWizard: WizardTexture = WizardTexture()
    private enum class Phase {
        PREP,
        ACTION1,
        ACTION2,
        ACTION3
    }
    private lateinit var gamePhase: Phase
    override fun initScreen() {
        log.debug { "Gameplay screen" }
        initPreparationPhase()
        gamePhase = Phase.PREP
    }

    private fun initPreparationPhase() {
        goodWizard.setAnimation(0f, 0f, assets , WizardTextures.GoodWizardIdle)
        spellCasting = SpellCasting()
        quitBtn = scene2d.textButton(Nls.quit())
        fireElementBtn = scene2d.button(ElementType.Fire.name)
        iceElementBtn = scene2d.button(ElementType.Ice.name)
        natureElementBtn = scene2d.button(ElementType.Nature.name)

        val elementBtnSize = 200f
        spellBar = scene2d.spellbar(selected = spellCasting.selectedElements)

        val table = fullSizeTable().apply {
            add(headingLabel(Nls.preparationPhase()))
            row()
            add(quitBtn)
        }

        val elementButtonsTable = scene2d.table {
            add(scene2d.table {
                background = skin[Image.ModalSkull]
                padTop(50f)
                add(fireElementBtn).width(elementBtnSize).height(elementBtnSize).colspan(2).center()
                row()
                add(iceElementBtn).width(elementBtnSize).height(elementBtnSize).colspan(1)
                add(natureElementBtn).width(elementBtnSize).height(elementBtnSize).colspan(1)
            }).width(elementBtnSize * 2)
        }

        spellBar.setPosition(WORLD_WIDTH / 2 - spellBar.width, spellBar.height + 130f)
        elementButtonsTable.setPosition(
            WORLD_WIDTH - 210f,
            220f
        )

        stage.clear()
        stage.addActor(table)
        stage.addActor(spellBar)
        stage.addActor(elementButtonsTable)
    }


    private fun initActionPhase() {
        val table = fullSizeTable().apply {
            background = skin[Image.Background]
            add(headingLabel("FIGHT"))
            row()
            row()
            row()
            add(bodyLabel("BOOM, BLAST, POOF, BEARD GONE"))
        }
        //When prep. phase is done this needs to be changed to update and not clear the stage
        stage.clear()
        stage.addActor(table)

        //If wizardA attacks first -> wizardA.setAnimation(Attack) + wizardB.setAnimation(takeHit)
        //If wizardA | B is dead -> setAnimation(dead)
        goodWizard.setAnimation(0f,0f, assets, WizardTextures.GoodWizardAttack1)
        evilWizard.setAnimation(0f, 0f, assets, WizardTextures.EvilWizardTakeHit)
    }

    private fun initSecondActionPhase() {
        goodWizard.setAnimation(0f,0f, assets, WizardTextures.GoodWizardHit)
        evilWizard.setAnimation(0f, 0f, assets, WizardTextures.EvilWizardAttack)
    }

    private fun initSecondDeathPhase() {
        goodWizard.setAnimation(0f,0f, assets, WizardTextures.GoodWizardIdle)
        evilWizard.setAnimation(0f, 0f, assets, WizardTextures.EvilWizardDeath)
    }


    override fun setBtnEventListeners() {
        quitBtn.onClick {
            game.removeScreen<GameplayScreen>()
            game.addScreen(GameplayScreen(game, batch, assets, camera))
            game.setScreen<MenuScreen>()
        }

        fireElementBtn.onClick {
            spellCasting.addFire()
        }
        iceElementBtn.onClick {
            spellCasting.addIce()
        }
        natureElementBtn.onClick {
            spellCasting.addNature()
        }
    }

    override fun update(delta: Float) {
        goodWizard.update(delta)
        evilWizard.update(delta)

        countDownTimer -= delta

        val table = smallTable().apply {
            background = skin[Image.TimerSlot]
            add(bodyLabel(countDownTimer.toInt().toString()))
        }
        if (countDownTimer <= 10) {
            stage.addActor(table)
        }

        if (countDownTimer <= 0 && gamePhase == Phase.PREP) {
            gamePhase = Phase.ACTION1
            initActionPhase()

        }
        if (countDownTimer <= -5f && gamePhase == Phase.ACTION1) {
            gamePhase = Phase.ACTION2
            initSecondActionPhase()
        }

        if (countDownTimer <= -10f && gamePhase == Phase.ACTION2){
            gamePhase = Phase.ACTION3
            initSecondDeathPhase()
        }
        camera.update()
    }

    override fun render(delta: Float) {
        update(delta)
        stage.act(delta)
        stage.draw()
        if (gamePhase == Phase.PREP)
        {
            batch.use() {
                it.projectionMatrix = camera.combined
                it.draw(goodWizard.getWizard(), -300f, -200f, goodWizard.getBounds().width*5f, goodWizard.getBounds().height*5f)
                }
        }
        if (gamePhase != Phase.PREP) {
            batch.use() {
                it.draw(goodWizard.getWizard(), -300f,-200f, goodWizard.getBounds().width*4f, goodWizard.getBounds().height*4f)
                it.draw(evilWizard.getWizard(), camera.viewportWidth - evilWizard.getBounds().width + 520f, -440f, -evilWizard.getBounds().width*9, evilWizard.getBounds().height*9)

            }
        }
    }
}
