package no.ntnu.beardblaster.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.actors.onClick
import ktx.scene2d.button
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.HEIGHT
import no.ntnu.beardblaster.WIDTH
import no.ntnu.beardblaster.ui.*
import no.ntnu.beardblaster.user.UserAuth

class RegisterScreen(
    game: BeardBlasterGame,
    batch: Batch,
    assets: AssetManager,
    camera: OrthographicCamera,
) : BaseScreen(game, batch, assets, camera) {
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
        val content = scene2d.table {
            defaults().pad(30f)
            background = skin[Image.Modal]
            add(headingLabel("Create Wizard"))
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
        val table = scene2d.table {
            setBounds(0f, 0f, WIDTH, HEIGHT)
            background = skin[Image.Background]
            add(backBtn).expandY().top().padTop(50f).width(91f)
            add(content).width(WIDTH * 0.9f).fillY()
        }
        stage.addActor(table)
        Gdx.input.inputProcessor = stage
    }

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

    override fun update(delta: Float) {}

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        update(delta)
        stage.act(delta)
        stage.draw()
    }
}
