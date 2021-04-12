package no.ntnu.beardblaster.lobby

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import no.ntnu.beardblaster.commons.*
import no.ntnu.beardblaster.commons.game.Game
import no.ntnu.beardblaster.commons.game.GameOpponent
import pl.mk5.gdx.fireapp.PlatformDistributor

class LobbyRepository : PlatformDistributor<AbstractLobbyRepository<Game>>(), AbstractLobbyRepository<Game> {

    override fun getIOSClassName(): String {
        TODO("Not yet implemented")
    }

    override fun getAndroidClassName(): String {
        return "no.ntnu.beardblaster.LobbyRepository"
    }

    override fun getWebGLClassName(): String {
        TODO("Not yet implemented")
    }

    override fun joinLobbyWithCode(code: String, opponent: GameOpponent): Flow<State<Game>> {
        return platformObject.joinLobbyWithCode(code, opponent)
    }
    override fun createLobby(): Flow<State<Game>> {
        return platformObject.createLobby()
    }

    override fun cancelLobbyWithId(id: String): Flow<State<Boolean>>  {
        return platformObject.cancelLobbyWithId(id)
    }

    override fun startGame(id: String): Flow<State<Boolean>> {
        return platformObject.startGame(id)
    }

    override fun endGame(id: String): Flow<State<Boolean>> {
        return platformObject.endGame(id)
    }

    @ExperimentalCoroutinesApi
    override fun subscribeToLobbyUpdates(id: String): Flow<State<Game>> {
       return platformObject.subscribeToLobbyUpdates(id)
    }

    override fun leaveLobbyWithId(id: String): Flow<State<Boolean>> {
        return platformObject.leaveLobbyWithId(id)
    }
}
