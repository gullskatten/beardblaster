package no.ntnu.beardblaster

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.inject.Context
import ktx.inject.register
import no.ntnu.beardblaster.screen.*

const val WIDTH = 1920f
const val HEIGHT = 1080f

class BeardBlasterGame : KtxGame<KtxScreen>() {
    private val context = Context()

    override fun create() {
        context.register {
            bindSingleton<Batch>(SpriteBatch())
            bindSingleton(AssetManager())
            bindSingleton(OrthographicCamera().apply {
                setToOrtho(false, WIDTH, HEIGHT)
            })
            addScreen(LoadingScreen(this@BeardBlasterGame, inject(), inject(), inject()))
        }
        setScreen<LoadingScreen>()
        super.create()
    }
}
