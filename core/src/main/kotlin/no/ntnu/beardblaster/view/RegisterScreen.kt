package no.ntnu.beardblaster.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.log.debug
import ktx.log.logger
import no.ntnu.beardblaster.AbstractScreen
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.worldHeight
import no.ntnu.beardblaster.worldWidth

private val LOG = logger<RegisterScreen>()
class RegisterScreen(game: BeardBlasterGame) : AbstractScreen(game) {

    private val registrationStage: Stage by lazy {
        val result = Stage(FitViewport(worldWidth, worldHeight))
        Gdx.input.inputProcessor = result
        result
    }
    override fun show() {
        LOG.debug { "Registration Screen Shown" }
    }

    override fun update(delta: Float) {
        if (Gdx.input.justTouched()) {
            //Change screen
            game.setScreen<LoginMenuScreenScreen>()
        }
    }

    override fun render(delta: Float) {
        update(delta)

    }

    override fun resize(width: Int, height: Int) {

    }

    override fun dispose() {


    }
}
