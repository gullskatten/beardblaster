package no.ntnu.beardblaster.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.scene2d.scene2d
import ktx.scene2d.textButton
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.ui.Image
import no.ntnu.beardblaster.ui.fullSizeTable
import no.ntnu.beardblaster.ui.get
import no.ntnu.beardblaster.ui.headingLabel

class LoginMenuScreen(
    game: BeardBlasterGame,
    batch: Batch,
    assets: AssetStorage,
    camera: OrthographicCamera
) : BaseScreen(game, batch, assets, camera) {
    private val exitBtn = scene2d.textButton(Nls.exitGame())
    private val loginBtn = scene2d.textButton(Nls.logIn())
    private val registerBtn = scene2d.textButton(Nls.register())

    private val stage: Stage by lazy {
        val result = BeardBlasterStage()
        Gdx.input.inputProcessor = result
        result
    }

    override fun show() {
        setBtnEventListeners()
        val table = fullSizeTable().apply {
            background = skin[Image.Modal]
            add(headingLabel(Nls.appName())).pad(50f)
            row()
            add(loginBtn).pad(40f)
            row()
            add(registerBtn).pad(40f)
            row()
            add(exitBtn).pad(40f)
        }
        stage.addActor(table)
        Gdx.input.inputProcessor = stage
    }

    override fun setBtnEventListeners() {
        loginBtn.onClick {
            game.setScreen<LoginScreen>()
        }
        registerBtn.onClick {
            game.setScreen<RegisterScreen>()
        }
        exitBtn.onClick {
            Gdx.app.exit()
        }
    }

    override fun update(delta: Float) {}

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        update(delta)
        stage.act(delta)
        stage.draw()
    }
}
