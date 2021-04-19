package no.ntnu.beardblaster.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.log.debug
import ktx.log.error
import ktx.log.info
import ktx.log.logger
import ktx.scene2d.scene2d
import ktx.scene2d.textButton
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.User
import no.ntnu.beardblaster.ui.*
import no.ntnu.beardblaster.user.UserAuth
import no.ntnu.beardblaster.user.UserRepository
import pl.mk5.gdx.fireapp.auth.GdxFirebaseUser

private val LOG = logger<LoginMenuScreen>()

class LoginMenuScreen(
    game: BeardBlasterGame,
    batch: SpriteBatch,
    assets: AssetStorage,
    camera: OrthographicCamera
) : BaseScreen(game, batch, assets, camera) {
    private lateinit var exitBtn: TextButton
    private lateinit var loginBtn: TextButton
    private lateinit var registerBtn: TextButton
    private val errorLabel = bodyLabel("", 1.5f, LabelStyle.Error.name)

    override fun initScreen() {
        exitBtn = scene2d.textButton(Nls.exitGame())
        loginBtn = scene2d.textButton(Nls.logIn())
        registerBtn = scene2d.textButton(Nls.register())

        val table = fullSizeTable().apply {
            background = skin[Image.Modal]
            add(headingLabel(Nls.appName())).pad(50f)
            row()
            add(loginBtn).pad(40f)
            row()
            add(registerBtn).pad(40f)
            row()
            add(exitBtn).pad(40f)
            row()
            add(errorLabel)
        }
        stage.addActor(table)
    }

    override fun setBtnEventListeners() {
        loginBtn.onClick { showLoginDialog() }
        registerBtn.onClick { showRegisterDialog() }
        exitBtn.onClick { Gdx.app.exit() }
    }

    override fun update(delta: Float) {}

    private fun showLoginDialog() {
        LoginDialog().apply {
            okBtn.onChange {
                if (!UserAuth().isLoggedIn() && isValid) {
                    LOG.debug { "Signing in user: ($email, $password)" }
                    UserAuth().signIn(email, password)
                        .then<GdxFirebaseUser> { game.setScreen<MenuScreen>() }
                        .fail { message, _ ->
                            LOG.error { message }
                            errorLabel.setText(message)
                        }
                }
                hide()
            }
        }.show(stage)
    }

    private fun showRegisterDialog() {
        RegisterDialog().apply {
            okBtn.onChange {
                LOG.debug { "Creating new user: ($username, $email, $password)" }
                UserAuth().createUser(email, password, username)
                    .then<GdxFirebaseUser> {
                        val user = User(username, id = it.userInfo.uid)
                        KtxAsync.launch {
                            UserRepository().create(user, "users").collect { state: State<User> ->
                                when (state) {
                                    is State.Success -> {
                                        game.setScreen<MenuScreen>()
                                    }
                                    is State.Failed -> {
                                        LOG.error { state.message }
                                        errorLabel.setText(state.message)
                                    }
                                    is State.Loading -> {
                                        LOG.info { "Creating user.." }
                                    }
                                }
                            }
                        }
                    }.fail { message, _ ->
                        LOG.error { message }
                        errorLabel.setText(message)
                    }
                hide()
            }
        }.show(stage)
    }
}
