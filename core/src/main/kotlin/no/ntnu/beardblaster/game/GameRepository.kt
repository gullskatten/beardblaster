package no.ntnu.beardblaster.game

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.game.AbstractGameRepository
import no.ntnu.beardblaster.commons.game.Game
import no.ntnu.beardblaster.commons.game.Prize
import no.ntnu.beardblaster.commons.game.Turn
import no.ntnu.beardblaster.commons.spell.SpellAction
import pl.mk5.gdx.fireapp.PlatformDistributor

class GameRepository : PlatformDistributor<AbstractGameRepository<Game>>(),
    AbstractGameRepository<Game> {

    override fun getIOSClassName(): String {
        TODO("Not yet implemented")
    }

    override fun getAndroidClassName(): String {
        return "no.ntnu.beardblaster.GameRepository"
    }

    override fun getWebGLClassName(): String {
        TODO("Not yet implemented")
    }

    @ExperimentalCoroutinesApi
    override fun subscribeToGameUpdates(id: String): Flow<State<Game>> {
        return platformObject.subscribeToGameUpdates(id)
    }

    @ExperimentalCoroutinesApi
    override fun subscribeToSpellsOnTurn(collection: String, currentTurn: Int): Flow<State<SpellAction>> {
       return platformObject.subscribeToSpellsOnTurn(collection, currentTurn)
    }

    override fun castSpell(currentTurn: Int, spell: SpellAction): Flow<State<SpellAction>> {
        return platformObject.castSpell(currentTurn, spell)
    }

    override fun endGame(id: String): Flow<State<Boolean>> {
        return platformObject.endGame(id)
    }

    override fun distributePrizes(prizes: List<Prize>): Flow<State<Boolean>> {
        return platformObject.distributePrizes(prizes)
    }

    override fun createTurn(currentTurn: Int): Flow<State<Turn>> {
        return platformObject.createTurn(currentTurn)
    }
}
