package no.ntnu.beardblaster

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage

abstract class BaseScreen(
    val game: BeardBlasterGame,
    val batch: SpriteBatch,
    val assets: AssetStorage,
    val camera: OrthographicCamera,
) : KtxScreen {
    val stage: Stage by lazy {
        val result = BeardBlasterStage()
        Gdx.input.inputProcessor = result
        result
    }

    // Template method pattern
    final override fun show() {
        stage.clear()
        initComponents()
        initScreen()
        setBtnEventListeners()
        Gdx.input.inputProcessor = stage
    }

    open fun initComponents() {} // Optional step
    abstract fun initScreen() // Abstract step
    abstract fun setBtnEventListeners() // Abstract step

    // Template method pattern
    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        update(delta)
        stage.act(delta)
        stage.draw()
        additionalRender(delta)
    }

    open fun update(delta: Float) {} // Optional step
    open fun additionalRender(delta: Float) {} // Optional step

    override fun dispose() {
        stage.clear()
    }
}
