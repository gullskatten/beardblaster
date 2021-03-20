package no.ntnu.beardblaster

import com.badlogic.gdx.graphics.g2d.Batch
import ktx.app.KtxScreen

interface IBeardBlasterScreen {
    fun setBtnEventListeners()
}

abstract class AbstractScreen(
        val game: BeardBlasterGame,
        val batch: Batch = game.batch
) : KtxScreen, IBeardBlasterScreen {
    val cam = game.cam
    val viewport = game.viewport

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
