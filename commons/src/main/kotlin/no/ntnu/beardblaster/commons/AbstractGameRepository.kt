package no.ntnu.beardblaster.commons

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import no.ntnu.beardblaster.commons.game.Game
import no.ntnu.beardblaster.commons.game.SpellCast
import no.ntnu.beardblaster.commons.game.Turn

interface AbstractGameRepository<T> {
    fun createTurn(currentTurn: Int): Flow<State<Turn>>

    fun endTurn(currentTurn: Int, chosenSpellId: Int): Flow<State<SpellCast>>

    @ExperimentalCoroutinesApi
    fun subscribeToGameUpdates(id: String): Flow<State<Game>>
}
