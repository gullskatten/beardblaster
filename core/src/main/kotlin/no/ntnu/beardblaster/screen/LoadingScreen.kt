package no.ntnu.beardblaster.screen

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ktx.app.KtxScreen
import ktx.graphics.use
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.assets.Atlas
import no.ntnu.beardblaster.assets.Font
import no.ntnu.beardblaster.assets.load
import no.ntnu.beardblaster.ui.createSkin
import no.ntnu.beardblaster.user.UserAuth
import kotlin.math.roundToInt

class LoadingScreen(
    private val game: BeardBlasterGame,
    private val batch: Batch,
    private val assets: AssetManager,
    private val camera: OrthographicCamera,
) : KtxScreen {
    private val renderer = ShapeRenderer()
    private val font = BitmapFont()

    override fun show() {
        Font.values().forEach { assets.load(it) }
        Atlas.values().forEach { assets.load(it) }
        font.data.scale(1.5f)
    }

    override fun render(delta: Float) {
        assets.update()
        camera.update()

        val x = 200f
        val height = 25f
        val width = camera.viewportWidth - (x * 2)
        val y = (camera.viewportHeight / 2) - (height / 2)

        renderer.use(ShapeRenderer.ShapeType.Filled, camera.combined) {
            it.color = Color.PINK
            it.rect(x, y, assets.progress * width, height)
        }

        batch.use(camera.combined) {
            font.draw(it, "Loading assets (${assets.progress.roundToInt() * 100}%)", x, y - 100f)
        }

        if (assets.isFinished) {
            createSkin(assets)
            addGameScreens()
            when (UserAuth().isLoggedIn()) {
                true -> game.setScreen<MenuScreen>()
                false -> game.setScreen<LoginMenuScreen>()
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
        game.addScreen(HighscoreScreen(game, batch, assets, camera))
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
