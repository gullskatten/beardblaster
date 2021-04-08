package no.ntnu.beardblaster.screen

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import no.ntnu.beardblaster.BeardBlasterGame

abstract class BaseScreen(
    val game: BeardBlasterGame,
    val batch: Batch,
    val assets: AssetStorage,
    val camera: OrthographicCamera,
) : KtxScreen {
    abstract fun update(delta: Float)
    abstract fun setBtnEventListeners()
}
