package no.ntnu.beardblaster.menu

import com.badlogic.gdx.Gdx
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.log.debug
import ktx.log.error
import ktx.log.info
import ktx.log.logger
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.user.User
import no.ntnu.beardblaster.user.UserAuth
import no.ntnu.beardblaster.user.UserRepository
import pl.mk5.gdx.fireapp.auth.GdxFirebaseUser

private val LOG = logger<LoginMenuPresenter>()

class LoginMenuPresenter(private val view: View, val game: BeardBlasterGame) {

    interface View {
        fun setErrorLabel(msg: String)
    }

    fun onLoginClick(isValid: Boolean, email: String, password: String) {
        if (!UserAuth().isLoggedIn() && isValid) {
            UserAuth().signIn(email, password)
                .then<GdxFirebaseUser> {
                    Gdx.app.postRunnable { game.setScreen<MenuScreen>() }
                }
                .fail { message, _ ->
                    LOG.error { message }
                    view.setErrorLabel(message)
                }
        }
    }

    fun onRegisterClick(username: String, email: String, password: String) {
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
                                view.setErrorLabel(state.message)
                            }
                            is State.Loading -> {
                                LOG.info { "Creating user.." }
                            }
                        }
                    }
                }
            }.fail { message, _ ->
                LOG.error { message }
                view.setErrorLabel(message)
            }
    }

    fun onExitBtnClick() {
        Gdx.app.exit()
    }
}
