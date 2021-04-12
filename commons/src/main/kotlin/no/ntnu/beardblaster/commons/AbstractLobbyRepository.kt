package no.ntnu.beardblaster.commons

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import no.ntnu.beardblaster.commons.game.Game
import no.ntnu.beardblaster.commons.game.GameOpponent

interface AbstractLobbyRepository<T> {
    fun createLobby(): Flow<State<T>>
    fun cancelLobbyWithId(id: String) : Flow<State<Boolean>>
    fun startGame(id: String): Flow<State<Boolean>>
    fun endGame(id: String): Flow<State<Boolean>>
    @ExperimentalCoroutinesApi
    fun subscribeToLobbyUpdates(id: String): Flow<State<Game>>
    fun joinLobbyWithCode(code: String, opponent: GameOpponent): Flow<State<Game>>
}
