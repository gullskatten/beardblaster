package no.ntnu.beardblaster.view

import com.badlogic.gdx.Gdx
import ktx.log.debug
import ktx.log.logger
import no.ntnu.beardblaster.AbstractScreen
import no.ntnu.beardblaster.BeardBlasterGame
private val LOG = logger<RegisterScreen>()
class RegisterScreen(game: BeardBlasterGame) : AbstractScreen(game) {
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
