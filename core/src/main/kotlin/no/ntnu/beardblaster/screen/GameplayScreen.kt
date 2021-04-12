package no.ntnu.beardblaster.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.*
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.log.info
import ktx.log.logger
import ktx.scene2d.*
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.ui.fullSizeTable
import no.ntnu.beardblaster.ui.headingLabel

private val log = logger<GameplayScreen>()

class GameplayScreen(
    game: BeardBlasterGame,
    batch: Batch,
    assets: AssetStorage,
    camera: OrthographicCamera,
) : BaseScreen(game, batch, assets, camera) {
    private val attackBtn = scene2d.textButton(Nls.attack())
    private val quitBtn = scene2d.textButton(Nls.quit())
    // CountDown
    private var countDownTimer = 10f
    override fun initScreen() {
        val table = fullSizeTable().apply {
            add(headingLabel(Nls.preparationPhase())).pad(50f)
            row()
            add(attackBtn).pad(40f)
            row()
            add(quitBtn).pad(40f)
            row()

        }
        stage.clear()
        stage.addActor(table)
    }

    fun initActionPhase() {
        val table = fullSizeTable().apply {
            add(headingLabel(Nls.actionPhase())).pad(50f)
            row()
        }
        //When prep. phase is done this needs to be updated to update and not clear the stage
        stage.clear()
        stage.addActor(table)
    }

    override fun setBtnEventListeners() {
        attackBtn.onClick {
            log.info { Nls.wizardNAttacks(1) }
        }
        quitBtn.onClick {
            game.removeScreen<GameplayScreen>()
            game.addScreen(GameplayScreen(game, batch, assets, camera))
            game.setScreen<MenuScreen>()
        }
    }

    override fun update(delta: Float) {
        countDownTimer -= delta

        if (countDownTimer <= 10)
        {
            stage.addActor(headingLabel(countDownTimer.toInt().toString()))
        }

        if (countDownTimer <= 0)
        {
            initActionPhase()
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        update(delta)
        stage.act(delta)
        stage.draw()
    }
}
