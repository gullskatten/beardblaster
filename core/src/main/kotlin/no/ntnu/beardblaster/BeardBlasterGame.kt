package no.ntnu.beardblaster

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxGame
import ktx.log.debug
import ktx.log.logger
import no.ntnu.beardblaster.assets.Assets
import no.ntnu.beardblaster.user.UserAuth
import no.ntnu.beardblaster.utils.AbstractScreen
import no.ntnu.beardblaster.view.*

private val LOG = logger<BeardBlasterGame>()
const val worldWidth = 1920f
const val worldHeight = 1080f

// Game class that extends KTX game with an abstract screen
class BeardBlasterGame : KtxGame<AbstractScreen>() {
    val batch: Batch by lazy { SpriteBatch() }
    val cam: OrthographicCamera = OrthographicCamera()
    val viewport: FitViewport = FitViewport(worldWidth, worldHeight, cam)

    override fun create() {
        // Set debug level
        Gdx.app.logLevel = Application.LOG_DEBUG
        LOG.debug { "Create game instance" }
        // Speed login
        if (!UserAuth().isLoggedIn()) {
            UserAuth().signIn("beard@blaster.com", "beardblaster")
        }
        initScreens()
    }

    // Add all screens and set loading screen
    private fun initScreens() {
        addScreen(LoadingScreen(this))
        addScreen(LoginMenuScreen(this))
        addScreen(LoginScreen(this))
        addScreen(RegisterScreen(this))
        addScreen(MenuScreen(this))
        addScreen(TutorialScreen(this))
        addScreen(HighscoreScreen(this))
        addScreen(LobbyScreen(this))
        addScreen(JoinLobbyScreen(this))
        addScreen(GameplayScreen(this))

        setScreen<LoadingScreen>()
    }

    override fun dispose() {
        super.dispose()
        Assets.dispose()
    }
}