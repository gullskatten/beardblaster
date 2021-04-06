package no.ntnu.beardblaster

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.inject.Context
import ktx.inject.register
import no.ntnu.beardblaster.screen.*

const val WIDTH = 1920f
const val HEIGHT = 1080f

class BeardBlasterGame : KtxGame<KtxScreen>() {
    private val context = Context()

    override fun create() {
        KtxAsync.initiate()
        context.register {
            bindSingleton<Batch>(SpriteBatch())
            bindSingleton(AssetStorage())
            bindSingleton(OrthographicCamera().apply {
                setToOrtho(false, WIDTH, HEIGHT)
            })
            addScreen(LoadingScreen(this@BeardBlasterGame, inject(), inject(), inject()))
        }
        setScreen<LoadingScreen>()
        super.create()
    }

    override fun dispose() {
        context.dispose()
        super.dispose()
    }
}
