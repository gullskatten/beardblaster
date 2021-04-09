package no.ntnu.beardblaster.commons

import kotlinx.coroutines.flow.Flow

interface AbstractGameRepository<T> {
    fun joinLobbyWithId(id: String) : Flow<State<T>>
    fun createLobby(): Flow<State<T>>
    fun cancelLobbyWithId(id: String)
    fun startGame(): Flow<State<Boolean>>
    fun endGame(): Flow<State<Boolean>>
}
