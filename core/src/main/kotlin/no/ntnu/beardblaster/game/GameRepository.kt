package no.ntnu.beardblaster.lobby

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import no.ntnu.beardblaster.commons.AbstractGameRepository
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.game.Game
import no.ntnu.beardblaster.commons.game.SpellCast
import no.ntnu.beardblaster.commons.game.Turn
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

    override fun createTurn(currentTurn: Int): Flow<State<Turn>> {
        return platformObject.createTurn(currentTurn)
    }

    override fun endTurn(currentTurn: Int, chosenSpellId: Int): Flow<State<SpellCast>> {
        return platformObject.endTurn(currentTurn, chosenSpellId)
    }

    @ExperimentalCoroutinesApi
    override fun subscribeToGameUpdates(id: String): Flow<State<Game>> {
        return platformObject.subscribeToGameUpdates(id)
    }
}
