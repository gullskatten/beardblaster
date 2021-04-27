package no.ntnu.beardblaster.lobby

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.game.Game
import no.ntnu.beardblaster.game.GameData
import no.ntnu.beardblaster.game.GamePlayScreen
import no.ntnu.beardblaster.menu.MenuScreen
import no.ntnu.beardblaster.user.UserData
import pl.mk5.gdx.fireapp.GdxFIRAuth
import java.util.*

class JoinLobbyPresenter(private val view: View, val game: BeardBlasterGame) : Observer {
    private var lobbyHandler: LobbyHandler = LobbyHandler()

    interface View {
        fun setErrorLabel(msg: String)
        fun setErrorLabelVisibility(isVisible: Boolean)
        fun isErrorLabelVisible(): Boolean
        fun setWaitingLabel(msg: String)
        fun setWaitingLabelFontScale(scale: Float)
        fun setWaitingLabelVisibility(isVisible: Boolean)
        fun setSubmitCodeBtnVisibility(isVisible: Boolean)
    }

    fun init() {
        // Listen for updates on LobbyData
        lobbyHandler.addObserver(this)
    }

    fun onSubmitCodeBtnClick(codeInput: String) {
        if (codeInput.isNotEmpty() && codeInput.isNotBlank())

            if (UserData.instance.user == null || GdxFIRAuth.inst().currentUser == null) {
                view.setErrorLabel(Nls.failedToJoinLobby())
                view.setErrorLabelVisibility(true)
                return
            }
        // This will eventually trigger "update()"
        lobbyHandler.joinLobbyWithCode(codeInput)
        view.setWaitingLabel(Nls.verifyingCode())
        view.setWaitingLabelVisibility(true)
    }

    fun onBackBtnClick() {
        KtxAsync.launch {
            if (lobbyHandler.game != null) {
                lobbyHandler.leaveLobby()?.collect {
                    when (it) {
                        is State.Success -> {
                            game.setScreen<MenuScreen>()
                        }
                        is State.Failed -> {
                            view.setErrorLabel(Nls.failedToLeaveLobby())
                            view.setErrorLabelVisibility(true)
                            if (view.isErrorLabelVisible()) {
                                // Just force quit if it fails once more.
                                game.setScreen<MenuScreen>()
                            }
                        }
                    }
                }
            } else {
                game.setScreen<MenuScreen>()
            }
        }
    }

    // Listens for changes on lobby -> when entering lobby and when lobby starts.
    override fun update(o: Observable?, arg: Any?) {
        if (o is LobbyHandler) {
            if (arg is String) {
                view.setErrorLabel(arg)
                view.setErrorLabelVisibility(true)
                view.setWaitingLabelVisibility(false)
            }
            if (arg is Game) {
                if (arg.startedAt > 0L) {
                    GameData.instance.game = arg
                    dispose()
                    game.setScreen<GamePlayScreen>()
                } else {
                    view.setErrorLabelVisibility(false)
                    view.setWaitingLabelVisibility(true)
                    view.setWaitingLabelFontScale(0.8f)
                    view.setWaitingLabel(Nls.waitingForHostToStart())
                    view.setSubmitCodeBtnVisibility(false)
                }
            }
        }
    }

    fun dispose() {
        lobbyHandler.deleteObserver(this)
    }
}

