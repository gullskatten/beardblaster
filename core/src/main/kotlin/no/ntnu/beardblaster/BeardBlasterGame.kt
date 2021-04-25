package no.ntnu.beardblaster

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.inject.Context
import ktx.inject.register

const val WORLD_WIDTH = 1920f
const val WORLD_HEIGHT = 1080f

class BeardBlasterGame : KtxGame<KtxScreen>() {
    private val context = Context()

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        KtxAsync.initiate()
        context.register {
            // Using singletons and dependency injection
            bindSingleton<SpriteBatch>(SpriteBatch())
            bindSingleton(AssetStorage())
            bindSingleton(OrthographicCamera().apply {
                setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT)
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
