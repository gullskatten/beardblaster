package no.ntnu.beardblaster

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.inject.Context
import ktx.inject.register
import ktx.log.debug
import ktx.log.logger
import no.ntnu.beardblaster.screen.*

const val WIDTH = 1920f
const val HEIGHT = 1080f

private val log = logger<BeardBlasterGame>()

class BeardBlasterGame : KtxGame<KtxScreen>() {
    private val context = Context()

    override fun create() {
        log.debug { "Creating BeardBlaster game" }
        context.register {
            bindSingleton<Batch>(SpriteBatch())
            bindSingleton(AssetManager())
            bindSingleton(OrthographicCamera().apply { setToOrtho(false, WIDTH, HEIGHT) })
            addScreen(LoadingScreen(this@BeardBlasterGame, inject(), inject(), inject()))
        }
        log.debug { "Setting LoadingScreen" }
        setScreen<LoadingScreen>()
        super.create()
    }
}
