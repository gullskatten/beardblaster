package no.ntnu.beardblaster.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.actions.Actions.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ktx.async.newAsyncContext
import ktx.graphics.use
import ktx.log.debug
import ktx.log.logger
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.assets.Assets
import no.ntnu.beardblaster.commons.User
import no.ntnu.beardblaster.firestore.Firestore
import no.ntnu.beardblaster.user.UserAuth
import pl.mk5.gdx.fireapp.GdxFIRAuth
import kotlin.math.roundToInt

private val LOG = logger<LoadingScreen>()

class LoadingScreen(game: BeardBlasterGame) : AbstractScreen(game) {
    init {
        // Load assets
        queueAsset()
    }

    private var font: BitmapFont = BitmapFont(Gdx.files.internal("font_nevis/nevis.fnt"))
    private val shapeRenderer = ShapeRenderer()
    private var progress = 0f
    private var getUserJob: Job? = null
    private var currentUser: User?  = null

    override fun show() {
        LOG.debug { "LOADING Screen" }
        getUserJob = GlobalScope.launch {
           currentUser = GdxFIRAuth.inst().currentUser?.userInfo?.uid?.let { Firestore<User>().getDocument(it, "users", User.Companion::fromHashMap) }
        }
    }

    override fun update(delta: Float) {
        progress = Assets.assetManager.progress.roundToInt().toFloat()
        // Check if assetManager is done loading
        if (Assets.assetManager.update() && getUserJob?.isCompleted == true) {
            // Go to correct menu screen when done loading
            if (UserAuth().isLoggedIn()) {
                if (currentUser != null) {
                    LOG.debug { currentUser!!.displayName }
                } else {
                    LOG.debug { "User not found" }
                }
                game.setScreen<MenuScreen>()
            } else game.setScreen<LoginMenuScreen>()
        }
    }

    override fun render(delta: Float) {
        update(delta)

        // Draw the progress bar
        shapeRenderer.use(ShapeRenderer.ShapeType.Filled, cam.combined)
        {
            it.rect(50f, (cam.viewportHeight / 2) - 12f, cam.viewportWidth - 50, 25f)
            it.color = Color.RED
            it.rect(50f, (cam.viewportHeight / 2) - 12f, progress * (cam.viewportWidth - 50), 25f)
        }

        // Draw the progress text
        batch.use(cam.combined)
        {
            font.draw(it, (100 * progress).toString() + "% Loading Assets", 100f, 200f)
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    fun queueAsset() {
        Assets.loadTextures()
    }

    override fun dispose() {
        super.dispose()
        Assets.dispose()
    }

    override fun setBtnEventListeners() {
    }
}



