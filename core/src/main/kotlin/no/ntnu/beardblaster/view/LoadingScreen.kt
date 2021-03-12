package no.ntnu.beardblaster.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import ktx.graphics.use
import ktx.log.debug
import ktx.log.logger
import no.ntnu.beardblaster.BeardBlasterGame

private val LOG = logger<LoadingScreen>()

class LoadingScreen(game: BeardBlasterGame) : AbstractScreen(game) {
    private val texture = Texture(Gdx.files.internal("graphics/load_bar.png"))
    private val sprite = Sprite(texture)

    override fun show() {
        LOG.debug { "LOADING screen" }
        sprite.setPosition(1f,1f)
    }

    override fun update(delta: Float) {
        if (Gdx.input.justTouched()) {
            game.setScreen<LoginScreen>()
        }
    }

    override fun render(delta: Float) {
        update(delta)
        batch.use {
            sprite.draw(it)
        }

    }

}