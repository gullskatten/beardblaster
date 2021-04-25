package no.ntnu.beardblaster.lobby

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.log.debug
import ktx.log.error
import ktx.log.logger
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.game.Game
import no.ntnu.beardblaster.game.GameData
import no.ntnu.beardblaster.game.GamePlayScreen
import no.ntnu.beardblaster.menu.MenuScreen
import java.util.*

private val LOG = logger<LobbyPresenter>()

class LobbyPresenter(private val view: View, val game: BeardBlasterGame) : Observer {
    private lateinit var subscription: Job
    private var lobbyHandler: LobbyHandler = LobbyHandler()

    interface View {
        fun setInfoLabel(string: String)
        fun setCodeLabel(code: String)
        fun setOpponentLabel(string: String)
        fun setOpponentBeardLengthLabel(beardLength: Float)
        fun setStartGameBtnVisibility(isVisible: Boolean)
    }

    fun init() {
        lobbyHandler.addObserver(this)
        KtxAsync.launch {
            lobbyHandler.createLobby()
        }
    }

    fun onStartGameBtnClick() {
        KtxAsync.launch {
            lobbyHandler.startGame()?.collect {
                when (it) {
                    is State.Loading -> {
                        view.setOpponentLabel("Starting game..")
                    }
                    is State.Failed -> {
                        view.setOpponentLabel(it.message)
                        LOG.error { it.message }
                    }
                    is State.Success -> {
                        game.setScreen<GamePlayScreen>()
                    }
                }
            }
        }
    }

    fun onBackBtnClick() {
        KtxAsync.launch {
            if (lobbyHandler.game == null) {
                game.setScreen<MenuScreen>()
            } else {
                lobbyHandler.cancelLobby()?.collect {
                    when (it) {
                        is State.Success -> {
                            lobbyHandler.setGame(null)
                            GameData.instance.game = null
                            dispose()
                            game.setScreen<MenuScreen>()
                        }
                        is State.Loading -> {

                        }
                        is State.Failed -> {
                            LOG.error { it.message }
                        }
                    }
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun update(o: Observable?, arg: Any?) {
        if (arg is Game) {
            view.setCodeLabel(arg.code)

            LOG.debug { "Game created - Subscribing to new game." }
            LOG.debug { "Should I subscribe? ${!::subscription.isInitialized || !subscription.isActive}." }
            LOG.debug { "Is initialized? ${::subscription.isInitialized}." }
            LOG.debug { "Is active? ${::subscription.isInitialized && subscription.isActive}." }

            if (::subscription.isInitialized && subscription.isActive) {
                subscription.cancel()
            }

            if (!::subscription.isInitialized || !subscription.isActive) {
                subscription = KtxAsync.launch {
                    LOG.debug { "Running subscribe to game with id ${arg.id}" }

                    LobbyRepository().subscribeToLobbyUpdates(arg.id).collect {
                        when (it) {
                            is State.Success -> {
                                // On received update: Check if opponent of updated Game is not null
                                if (it.data.opponent != null) {
                                    // Player may now start the game
                                    GameData.instance.game = it.data
                                    view.setInfoLabel("A worthy opponent joined!")
                                    view.setOpponentLabel("${it.data.opponent?.displayName}")
                                    view.setOpponentBeardLengthLabel(
                                        it.data.opponent?.beardLength ?: 0f
                                    )
                                    view.setStartGameBtnVisibility(true)
                                } else {
                                    view.setOpponentLabel("Waiting for opponent to join");
                                    view.setInfoLabel(Nls.shareGameCodeMessage())
                                    view.setStartGameBtnVisibility(false)
                                }
                            }
                            is State.Loading -> {
                            }
                            is State.Failed -> {
                                LOG.error { it.message }
                            }
                        }
                    }
                }
            }
        }
    }

    fun dispose() {
        if (subscription != null && subscription.isActive) {
            subscription.cancel()
        }
        lobbyHandler.deleteObserver(this)
    }
}

