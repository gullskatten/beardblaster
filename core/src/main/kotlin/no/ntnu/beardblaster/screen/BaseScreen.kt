package no.ntnu.beardblaster.screen

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import ktx.app.KtxScreen
import no.ntnu.beardblaster.BeardBlasterGame


abstract class BaseScreen(
    val game: BeardBlasterGame,
    val batch: Batch,
    val assets: AssetManager,
    val camera: OrthographicCamera,
) : KtxScreen {
    abstract fun update(delta: Float)
    abstract fun setBtnEventListeners()
}
