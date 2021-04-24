package no.ntnu.beardblaster.commons.leaderboard

import kotlinx.coroutines.flow.Flow
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.wizard.Wizard

interface AbstractLeaderBoardRepository<T> {
    fun getBeardLengthForUser(userId: String): Flow<State<Float>>
    fun getTopTenBeards(): Flow<State<List<BeardScore>>>
    fun updateBeardLength(wizard: Wizard, beardLengthIncrease: Float): Flow<State<BeardScore>>
}
