package no.ntnu.beardblaster.lobby

import kotlinx.coroutines.flow.collect
import ktx.log.info
import ktx.log.logger
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.game.Game
import java.util.*

private val LOG = logger<LobbyData>()

class LobbyData private constructor() : Observable() {
    var game: Game? = null
        private set

    var error: String? = null
        private set

    var isLoading: Boolean = false
        private set

    fun setGame(game: Game?) {
        this.game = game
    }


    suspend fun createLobby() {
            LobbyRepository().createLobby().collect {
                setChanged()
                when(it) {
                    is State.Success -> {
                        LOG.info { "Notifying observers of lobby with code ${it.data.lobbyCode}" }
                        notifyObservers(it.data.lobbyCode)
                        setGame(it.data)
                    }
                    is State.Loading -> {
                        notifyObservers("Loading..")
                    }
                    is State.Failed -> {
                        notifyObservers(it.message)
                    }
                }
            }
    }

    companion object {
        val instance = LobbyData()
    }
}
