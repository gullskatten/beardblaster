package no.ntnu.beardblaster.screen

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import kotlinx.coroutines.launch
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.graphics.use
import ktx.log.logger
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.assets.*
import no.ntnu.beardblaster.commons.User
import no.ntnu.beardblaster.ui.createSkin
import no.ntnu.beardblaster.user.UserAuth


class LoadingScreen(
    private val game: BeardBlasterGame,
    private val batch: Batch,
    private val assets: AssetStorage,
    private val camera: OrthographicCamera,
) : KtxScreen {
    private val renderer = ShapeRenderer()
    private val font = BitmapFont()
    private var currentUser: User? = null
    private val isFinishedLoading: Boolean
        get() = assets.progress.percent >= 1f

    override fun show() {
        KtxAsync.launch {
            FontAsset.values().map { assets.loadAsync(it) }
            AtlasAsset.values().map { assets.loadAsync(it) }
            I18NAsset.values().map { assets.loadAsync(it) }
        }
        font.data.scale(1.5f)
    }

    override fun render(delta: Float) {
        val x = 200f
        val height = 25f
        val width = camera.viewportWidth - (x * 2)
        val y = (camera.viewportHeight / 2) - (height / 2)
        val progress = assets.progress.percent

        renderer.use(ShapeRenderer.ShapeType.Filled, camera.combined) {
            it.color = Color.PINK
            it.rect(x, y, progress * width, height)
        }

        batch.use(camera.combined) {
            font.draw(it, "Loading assets (${progress * 100}%)", x, y - 100f)
        }

        if (isFinishedLoading) {
            Nls.i18nBundle = assets[I18NAsset.Default]
            createSkin(assets)
            addGameScreens()
            if (UserAuth().isLoggedIn()) {
                game.setScreen<MenuScreen>()
            } else {
                game.setScreen<LoginMenuScreen>()
            }
            game.removeScreen<LoadingScreen>()
            dispose()
        }
    }

    private fun addGameScreens() {
        game.addScreen(LoginMenuScreen(game, batch, assets, camera))
        game.addScreen(LoginScreen(game, batch, assets, camera))
        game.addScreen(RegisterScreen(game, batch, assets, camera))
        game.addScreen(MenuScreen(game, batch, assets, camera))
        game.addScreen(TutorialScreen(game, batch, assets, camera))
        game.addScreen(HighScoreScreen(game, batch, assets, camera))
        game.addScreen(LobbyScreen(game, batch, assets, camera))
        game.addScreen(JoinLobbyScreen(game, batch, assets, camera))
        game.addScreen(GameplayScreen(game, batch, assets, camera))
    }

    override fun dispose() {
        renderer.dispose()
        font.dispose()
        super.dispose()
    }
}
