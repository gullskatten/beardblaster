package no.ntnu.beardblaster.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import no.ntnu.beardblaster.BeardBlasterGame

abstract class BaseScreen(
    val game: BeardBlasterGame,
    val batch: Batch,
    val assets: AssetStorage,
    val camera: OrthographicCamera,
) : KtxScreen {
    val stage: Stage by lazy {
        val result = BeardBlasterStage()
        Gdx.input.inputProcessor = result
        result
    }

    // Template method pattern: https://refactoring.guru/design-patterns/template-method
    final override fun show() {
        initScreen()
        setBtnEventListeners()
        Gdx.input.inputProcessor = stage
    }

    abstract fun initScreen() // Abstract step
    abstract fun update(delta: Float)
    abstract fun setBtnEventListeners()
}
