package no.ntnu.beardblaster.game

import kotlinx.coroutines.flow.Flow
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.game.Game
import no.ntnu.beardblaster.commons.game.SpellCast
import no.ntnu.beardblaster.commons.game.Turn
import no.ntnu.beardblaster.commons.spell.Spell
import no.ntnu.beardblaster.lobby.GameRepository
import java.util.*

class GameData private constructor() : Observable() {
    var game: Game? = null
    var isHost: Boolean = false

    var error: String? = null
        private set

    fun createTurn(currentTurn: Int): Flow<State<Turn>>? {
        if (game != null && game!!.id.isNotEmpty()) {
            return GameRepository().createTurn(currentTurn)
        }
        return null
    }

    fun endTurn(currentTurn: Int, chosenSpell: Spell?): Flow<State<SpellCast>>? {
        if (game != null && game!!.id.isNotEmpty()) {
            return GameRepository().endTurn(currentTurn, chosenSpell?.spellID ?: 0)
        }
        return null
    }

    companion object {
        val instance = GameData()
    }
}
