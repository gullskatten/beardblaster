package no.ntnu.beardblaster.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ktx.graphics.use
import ktx.log.debug
import ktx.log.logger
import no.ntnu.beardblaster.AbstractScreen
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.assets.Assets
import kotlin.math.roundToInt

private val LOG = logger<LoadingScreen>()

class LoadingScreen(game: BeardBlasterGame) : AbstractScreen(game) {
    //Load assets
    init {
        queueAsset()
    }
    //private vars
    private var font: BitmapFont = BitmapFont(Gdx.files.internal("font_nevis/nevis.fnt"))
    private val shapeRenderer = ShapeRenderer()
    private var progress = 0f

    override fun show() {
        LOG.debug { "LOADING screen" }
    }

    override fun update(delta: Float) {
        progress = Assets.assetManager.progress.roundToInt().toFloat()
        //Check iff assetManager is done loading
        if (Assets.assetManager.update()) {
            if (Gdx.input.justTouched()) {
                //Change screen
                game.setScreen<LoginMenuScreenScreen>()
            }
        }
    }

    override fun render(delta: Float) {
        update(delta)

        //Draw the progress bar
        shapeRenderer.use(ShapeRenderer.ShapeType.Filled, cam.combined)
        {
            it.rect(50f,(cam.viewportHeight / 2) -12f, cam.viewportWidth -50, 25f )
            it.color = Color.RED
            it.rect(50f, (cam.viewportHeight / 2) -12f , progress* (cam.viewportWidth -50), 25f )
        }

        //Draw the progress text
        batch.use(cam.combined)
        {
            font.draw(it, (100*progress).toString() + "% Loading Assets", 100f, 200f)
        }
    }

    override fun resize(width:  Int, height: Int) {
        viewport.update(width, height, true)
    }

    fun queueAsset() {
        Assets.loadTextures()
    }

    override fun dispose() {
        super.dispose()
        Assets.dispose()
    }
}



