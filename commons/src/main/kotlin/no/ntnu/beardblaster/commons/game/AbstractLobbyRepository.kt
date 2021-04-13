package no.ntnu.beardblaster.commons.game

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import no.ntnu.beardblaster.commons.State

interface AbstractLobbyRepository<T> {
    fun createLobby(): Flow<State<T>>
    fun cancelLobbyWithId(id: String) : Flow<State<Boolean>>
    fun startGame(id: String): Flow<State<Boolean>>
    fun endGame(id: String): Flow<State<Boolean>>
    @ExperimentalCoroutinesApi
    fun subscribeToLobbyUpdates(id: String): Flow<State<Game>>
    fun joinLobbyWithCode(code: String, opponent: GameOpponent): Flow<State<Game>>
    fun leaveLobbyWithId(id: String): Flow<State<Boolean>>
}
