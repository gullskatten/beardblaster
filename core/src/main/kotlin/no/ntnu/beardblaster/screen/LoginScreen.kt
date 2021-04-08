package no.ntnu.beardblaster.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.scene2d.button
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.WORLD_HEIGHT
import no.ntnu.beardblaster.WORLD_WIDTH
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.ui.*
import no.ntnu.beardblaster.user.UserAuth

class LoginScreen(
    game: BeardBlasterGame,
    batch: Batch,
    assets: AssetStorage,
    camera: OrthographicCamera
) : BaseScreen(game, batch, assets, camera) {
    private val loginBtn = scene2d.textButton(Nls.logIn())
    private val backBtn = scene2d.button(ButtonStyle.Cancel.name)
    private val emailInput = inputField(Nls.emailAddress())
    private val passwordInput = passwordField(Nls.password())

    private val stage: Stage by lazy {
        val result = BeardBlasterStage()
        Gdx.input.inputProcessor = result
        result
    }

    override fun show() {
        setBtnEventListeners()
        // TODO: find out why input fields renders with wrong width
        val content = scene2d.table {
            defaults().pad(30f)
            background = skin[Image.Modal]
            add(headingLabel(Nls.login()))
            row()
            add(emailInput).width(570f)
            row()
            add(passwordInput).width(570f)
            row()
            add(loginBtn).center()
        }
        val table = scene2d.table {
            setBounds(0f, 0f, WORLD_WIDTH, WORLD_HEIGHT)
            background = skin[Image.Background]
            add(backBtn).expandY().top().padTop(50f).width(91f)
            add(content).width(WORLD_WIDTH * 0.9f).fillY()
        }
        stage.addActor(table)
        Gdx.input.inputProcessor = stage
    }

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

    override fun update(delta: Float) {}

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        update(delta)
        stage.act(delta)
        stage.draw()
    }
}
