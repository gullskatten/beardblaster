package no.ntnu.beardblaster.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import no.ntnu.beardblaster.worldHeight
import no.ntnu.beardblaster.worldWidth

class BeardBlasterStage : Stage(FitViewport(worldWidth, worldHeight)) {

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        // A just good-enough solution to hide keyboard when not in use.
        // Causes "flicker" if touching to enter a different input field when keyboard is up already
        Gdx.input.setOnscreenKeyboardVisible(false)
        return super.touchDown(screenX, screenY, pointer, button)
    }
}