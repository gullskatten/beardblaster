package no.ntnu.beardblaster.commons.leaderboard

import kotlinx.coroutines.flow.Flow
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.game.GamePlayer

interface AbstractLeaderBoardRepository<T> {
    fun getTopTenBeards(): Flow<State<List<BeardScore>>>
    fun updateBeardLength(user: GamePlayer, newLength: Float): Flow<State<BeardScore>>
}
