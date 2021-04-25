package no.ntnu.beardblaster.screen

import com.badlogic.gdx.Gdx
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.commons.user.User
import no.ntnu.beardblaster.user.UserAuth
import no.ntnu.beardblaster.user.UserData
import java.util.*

class MenuPresenter(private val view: View, val game: BeardBlasterGame) : Observer {

    interface View {
        fun updateWizardLabel(string: String)
        fun updateBeardLengthLabel(beardLength: Float)
        fun setDisabledButtons(isDisabled: Boolean)
    }

    fun init() {
        if (UserData.instance.user == null) {
            view.setDisabledButtons(true)
        }
        if (UserData.instance.user == null && !UserData.instance.isLoading) {
            KtxAsync.launch {
                UserData.instance.loadUserData()
            }
            UserData.instance.addObserver(this)
        }
    }

    fun onCreateGameBtnClick() {
        game.setScreen<LobbyScreen>()
    }

    fun onJoinGameBtnClick() {
        game.setScreen<JoinLobbyScreen>()
    }

    fun onLeaderBoardBtnClick() {
        game.setScreen<LeaderBoardScreen>()
    }

    fun onTutorialBtnClick() {
        game.setScreen<TutorialScreen>()
    }

    fun onLogoutBtnClick() {
        if (UserAuth().isLoggedIn()) {
            UserData.instance.setUserData(null)
            UserAuth().signOut()
        }
        game.setScreen<LoginMenuScreen>()
    }

    fun onExitBtnClick() {
        Gdx.app.exit()
    }

    override fun update(p0: Observable?, p1: Any?) {
        if (p0 is UserData) {
            if (p1 is String) {
                view.updateWizardLabel(p1.toString())
            } else if (p1 is User) {
                view.updateWizardLabel(p1.displayName)
                view.updateBeardLengthLabel(p1.beardLength)
                view.setDisabledButtons(false)
            }
        }
    }

    fun dispose() {
        UserData.instance.deleteObserver(this)
    }
}
