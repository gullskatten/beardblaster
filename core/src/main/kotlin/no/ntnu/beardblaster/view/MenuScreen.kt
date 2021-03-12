package no.ntnu.beardblaster.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import ktx.graphics.use
import ktx.log.debug
import ktx.log.logger
import no.ntnu.beardblaster.BeardBlasterGame


private val LOG = logger<MenuScreen>()


class MenuScreen(game: BeardBlasterGame) : AbstractScreen(game) {
    private val texture = Texture(Gdx.files.internal("graphics/playbtn.png"))
    private val sprite = Sprite(texture)

    override fun show() {
        LOG.debug { "MENU Screen" }

    }

    override fun update(delta: Float) {
        if (Gdx.input.justTouched()) {
            game.setScreen<LoadingScreen>()

        }
    }

    override fun render(delta: Float) {
        update(delta)

        batch.use {
            sprite.draw(it)
        }
    }


}
