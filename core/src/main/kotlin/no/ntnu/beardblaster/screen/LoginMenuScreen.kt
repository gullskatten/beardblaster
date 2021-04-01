package no.ntnu.beardblaster.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import ktx.actors.onClick
import ktx.scene2d.*
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.HEIGHT
import no.ntnu.beardblaster.WIDTH

class LoginMenuScreen(
    game: BeardBlasterGame,
    batch: Batch,
    assets: AssetManager,
    camera: OrthographicCamera
) : BaseScreen(game, batch, assets, camera) {
    private val skin: Skin = Scene2DSkin.defaultSkin
    private val exitBtn = scene2d.textButton("Exit Game")
    private val loginBtn = scene2d.textButton("Log In")
    private val registerBtn = scene2d.textButton("Register")

    private val stage: Stage by lazy {
        val result = BeardBlasterStage()
        Gdx.input.inputProcessor = result
        result
    }

    override fun show() {
        setBtnEventListeners()
        val heading = scene2d.label("BeardBlaster", "heading") {
            setAlignment(Align.center)
            setFontScale(2f)
        }
        val table = scene2d.table(skin) {
            setBounds(0f, 0f, WIDTH, HEIGHT)
            background = skin.getDrawable("modal_fancy")
            add(heading).pad(50f)
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
