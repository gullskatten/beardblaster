package no.ntnu.beardblaster.screen

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.log.info
import ktx.scene2d.button
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.WORLD_WIDTH
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.ui.*
import no.ntnu.beardblaster.user.UserAuth
import pl.mk5.gdx.fireapp.auth.GdxFirebaseUser


class LoginScreen(
    game: BeardBlasterGame,
    batch: SpriteBatch,
    assets: AssetStorage,
    camera: OrthographicCamera
) : BaseScreen(game, batch, assets, camera) {
    private lateinit var loginBtn: TextButton
    private lateinit var backBtn: Button
    private lateinit var emailInput: TextField
    private lateinit var passwordInput: TextField
    private lateinit var error: Label

    override fun initScreen() {
        loginBtn = scene2d.textButton(Nls.logIn())
        backBtn = scene2d.button(ButtonStyle.Cancel.name)
        emailInput = inputField(Nls.emailAddress())
        passwordInput = passwordField(Nls.password())
        error = bodyLabel("", LabelStyle.Error.name)
        setBtnEventListeners()
        error.setText("")
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
            row()
            add(error)
        }
        val table = fullSizeTable().apply {
            background = skin[Image.Background]
            add(backBtn).expandY().top().padTop(50f).width(91f)
            add(content).width(WORLD_WIDTH * 0.9f).fillY()
        }
        stage.addActor(table)
    }

    override fun setBtnEventListeners() {
        loginBtn.onClick {
            if (!UserAuth().isLoggedIn() && emailInput.text.isNotEmpty() && passwordInput.text.isNotEmpty()) {
                UserAuth().signIn(emailInput.text, passwordInput.text)
                    .then<GdxFirebaseUser> { gdxFirebaseUser ->
                        game.setScreen<MenuScreen>()
                    }
                    .fail { s, _ ->
                        LOG.info { s }
                        error.setText(s)
                    }
            } else {
                error.setText("Username or password cannot be empty!")
            }
        }
        backBtn.onClick {
            error.setText("")
            game.setScreen<LoginMenuScreen>()
        }
    }

    override fun update(delta: Float) {}
}
