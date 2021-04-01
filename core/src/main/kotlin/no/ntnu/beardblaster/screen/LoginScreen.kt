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
import no.ntnu.beardblaster.ui.inputField
import no.ntnu.beardblaster.ui.passwordField
import no.ntnu.beardblaster.user.UserAuth

class LoginScreen(
    game: BeardBlasterGame,
    batch: Batch,
    assets: AssetManager,
    camera: OrthographicCamera
) : BaseScreen(game, batch, assets, camera) {
    private val skin: Skin = Scene2DSkin.defaultSkin
    private val loginBtn = scene2d.textButton("Login")
    private val backBtn = scene2d.button("cancel")
    private val emailInput = inputField("Email address")
    private val passwordInput = passwordField("Password")

    private val stage: Stage by lazy {
        val result = BeardBlasterStage()
        Gdx.input.inputProcessor = result
        result
    }

    override fun show() {
        setBtnEventListeners()
        val heading = scene2d.label("Login", "heading") {
            setAlignment(Align.center)
            setScale(2f)
        }
        val left = scene2d.table(skin) {
            add(backBtn).expandY().align(Align.top).padTop(50f)
        }
        // TODO: find out why input fields renders with wrong width
        val right = scene2d.table(skin) {
            defaults().pad(30f)
            background = skin.getDrawable("modal_fancy")
            add(heading)
            row()
            add(emailInput).width(570f)
            row()
            add(passwordInput).width(570f)
            row()
            add(loginBtn).center()
        }
        val table = scene2d.table(skin) {
            setBounds(0f, 0f, WIDTH, HEIGHT)
            background = skin.getDrawable("background")
            add(left).width(91f).expandY().fillY()
            add(right).width(WIDTH * 0.9f).fillY()
        }
        stage.addActor(table)
        Gdx.input.inputProcessor = stage
    }

    override fun update(delta: Float) {}

    override fun setBtnEventListeners() {
        loginBtn.onClick {
            if (!UserAuth().isLoggedIn() && emailInput.text.isNotEmpty() && passwordInput.text.isNotEmpty()) {
                UserAuth().signIn(emailInput.text, passwordInput.text)
            }
            // TODO: Future: Don't proceed unless login actually successful
            game.setScreen<MenuScreen>()
        }
        backBtn.onClick {
            game.setScreen<LoginMenuScreen>()
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        update(delta)
        stage.act(delta)
        stage.draw()
    }
}
