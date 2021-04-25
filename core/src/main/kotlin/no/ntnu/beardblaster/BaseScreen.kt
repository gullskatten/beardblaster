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

    // Template method pattern: https://refactoring.guru/design-patterns/template-method
    final override fun show() {
        stage.clear()
        initComponents()
        initScreen()
        setBtnEventListeners()
        Gdx.input.inputProcessor = stage
    }

    abstract fun initScreen() // Abstract step
    open fun initComponents() {}
    abstract fun setBtnEventListeners()
    abstract fun update(delta: Float)

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        update(delta)
        stage.act(delta)
        stage.draw()
        additionalRender(delta)
    }

    open fun additionalRender(delta: Float) {}

    override fun dispose() {
        stage.clear()
    }
}
