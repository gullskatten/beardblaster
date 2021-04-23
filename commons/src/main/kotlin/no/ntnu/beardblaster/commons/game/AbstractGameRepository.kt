package no.ntnu.beardblaster.commons.game

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.spell.SpellAction

interface AbstractGameRepository<T> {
    fun castSpell(currentTurn: Int, spell: SpellAction): Flow<State<SpellAction>>
    fun createTurn(currentTurn: Int): Flow<State<Turn>>
    fun endGame(id: String): Flow<State<Boolean>>
    fun distributePrizes(prizes: List<Prize>): Flow<State<Boolean>>
    @ExperimentalCoroutinesApi
    fun subscribeToGameUpdates(id: String): Flow<State<Game>>
    @ExperimentalCoroutinesApi
    fun subscribeToSpellsOnTurn(collection: String): Flow<State<SpellAction>>
}
