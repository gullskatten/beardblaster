package no.ntnu.beardblaster.commons.lobby

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.game.Game
import no.ntnu.beardblaster.commons.game.GamePlayer

interface AbstractLobbyRepository<T> {
    fun createLobby(): Flow<State<T>>
    fun cancelLobbyWithId(id: String) : Flow<State<Boolean>>
    fun startGame(id: String): Flow<State<Boolean>>
    @ExperimentalCoroutinesApi
    fun subscribeToLobbyUpdates(id: String): Flow<State<Game>>
    fun joinLobbyWithCode(code: String, opponent: GamePlayer): Flow<State<Game>>
    fun leaveLobbyWithId(id: String): Flow<State<Boolean>>
}
