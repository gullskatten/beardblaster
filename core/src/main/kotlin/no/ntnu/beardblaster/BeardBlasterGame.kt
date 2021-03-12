package no.ntnu.beardblaster

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxGame
import ktx.log.debug
import ktx.log.logger
import no.ntnu.beardblaster.view.AbstractScreen
import no.ntnu.beardblaster.view.LoadingScreen
import no.ntnu.beardblaster.view.LoginScreen
import no.ntnu.beardblaster.view.MenuScreen



private val UNIT_SCALE:Float = 1 / 16f
private val LOG = logger<BeardBlasterGame>()



class BeardBlasterGame : KtxGame<AbstractScreen>() {
    val batch : Batch by lazy { SpriteBatch() }
    /*val stage: Stage by lazy {
        val result = Stage(FitViewport(135f, 240f))
        Gdx.input.inputProcessor = result
        result
    }*/
    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        LOG.debug { "Create game instance" }

        addScreen(LoadingScreen(this))
        addScreen(LoginScreen(this))
        addScreen(MenuScreen(this))
        setScreen<LoadingScreen>()


    }
}