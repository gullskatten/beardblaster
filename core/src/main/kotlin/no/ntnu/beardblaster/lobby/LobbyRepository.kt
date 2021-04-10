package no.ntnu.beardblaster.lobby

import kotlinx.coroutines.flow.Flow
import no.ntnu.beardblaster.commons.*
import no.ntnu.beardblaster.commons.game.Game
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

    override fun joinLobbyWithId(id: String): Flow<State<Game>> {
        return platformObject.joinLobbyWithId(id)
    }
    override fun createLobby(): Flow<State<Game>> {
        return platformObject.createLobby()
    }

    override fun cancelLobbyWithId(id: String): Flow<State<Boolean>>  {
        return platformObject.cancelLobbyWithId(id)
    }

    override fun startGame(): Flow<State<Boolean>> {
        return platformObject.startGame()
    }

    override fun endGame(): Flow<State<Boolean>> {
        return platformObject.endGame()
    }


}
