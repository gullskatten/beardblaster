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

class RegisterScreen(
    game: BeardBlasterGame,
    batch: Batch,
    assets: AssetManager,
    camera: OrthographicCamera,
) : BaseScreen(game, batch, assets, camera) {
    private val skin: Skin = Scene2DSkin.defaultSkin
    private val backBtn = scene2d.button("cancel")
    private val createBtn = scene2d.textButton("Create Wizard")
    private val userNameInput = inputField("Wizard name")
    private val emailInput = inputField("Email address")
    private val passwordInput = passwordField("Password")
    private val rePasswordInput = passwordField("Re-enter password")

    private val stage: Stage by lazy {
        val result = BeardBlasterStage()
        Gdx.input.inputProcessor = result
        result
    }

    override fun show() {
        setBtnEventListeners()
        val heading = scene2d.label("Create Wizard", "heading") {
            setAlignment(Align.center)
            setFontScale(2f)
        }
        val left = scene2d.table(skin) {
            add(backBtn).expandY().align(Align.top).padTop(50f)
        }
        val right = scene2d.table(skin) {
            defaults().pad(30f)
            background = skin.getDrawable("modal_fancy")
            add(heading)
            row()
            add(userNameInput).width(570f)
            row()
            add(emailInput).width(570f)
            row()
            add(passwordInput).width(570f)
            row()
            add(rePasswordInput).width(570f)
            row()
            add(createBtn).center()
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
        createBtn.onClick {
            if (emailInput.text.isNotEmpty() && passwordInput.text.isNotEmpty() && userNameInput.text.isNotEmpty()) {
                UserAuth().createUser(emailInput.text, passwordInput.text, userNameInput.text)
            }
            // TODO: Future: Don't proceed unless signup actually successful
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
