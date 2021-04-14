package no.ntnu.beardblaster.game

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import ktx.log.error
import ktx.log.info
import ktx.log.logger
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.game.Game
import no.ntnu.beardblaster.commons.game.SpellCast
import no.ntnu.beardblaster.commons.game.Turn
import no.ntnu.beardblaster.lobby.GameRepository
import no.ntnu.beardblaster.models.Spell
import java.util.*

private val LOG = logger<GameData>()

class GameData private constructor() : Observable() {
    var game: Game? = null
    var isHost: Boolean = false

    var error: String? = null
        private set

    var isLoading: Boolean = false
        private set

    fun createTurn(currentTurn: Int): Flow<State<Turn>>? {
        if (game != null && game!!.id.isNotEmpty()) {
            return GameRepository().createTurn(currentTurn)
        }
        return null
    }

    fun endTurn(currentTurn: Int, chosenSpell: Spell?): Flow<State<SpellCast>>? {
        if (game != null && game!!.id.isNotEmpty()) {
            return GameRepository().endTurn(currentTurn, chosenSpell?.id ?: 0)
        }
        return null
    }

    @ExperimentalCoroutinesApi
    private suspend fun subscribeToUpdatesOn(id: String) {
        LOG.info { "Subscribing to updates on $id" }

        GameRepository().subscribeToGameUpdates(id).collect {
            when (it) {
                is State.Success -> {
                    notifyObservers(it.data)
                }
                is State.Loading -> {
                }
                is State.Failed -> {
                    LOG.error { it.message }
                    error = it.message
                    notifyObservers(error)
                }
            }
            setChanged()
        }
    }

    companion object {
        val instance = GameData()
    }
}
