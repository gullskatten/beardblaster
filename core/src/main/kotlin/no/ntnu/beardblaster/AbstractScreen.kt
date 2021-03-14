package no.ntnu.beardblaster

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxScreen

abstract class AbstractScreen(
        val game: BeardBlasterGame,
        val batch: Batch = game.batch
        )
    : KtxScreen {
    val cam = game.stage.camera as OrthographicCamera
    val viewport = game.stage.viewport as FitViewport
    val stage: Stage = game.stage

    override fun show() {

    }
    abstract fun update(delta: Float)


    override fun render(delta: Float) {

    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {

    }
}
