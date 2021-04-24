package no.ntnu.beardblaster.leaderboard

import kotlinx.coroutines.flow.Flow
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.game.Game
import no.ntnu.beardblaster.commons.leaderboard.AbstractLeaderBoardRepository
import no.ntnu.beardblaster.commons.leaderboard.BeardScore
import no.ntnu.beardblaster.commons.wizard.Wizard
import pl.mk5.gdx.fireapp.PlatformDistributor

class LeaderBoardRepository : PlatformDistributor<AbstractLeaderBoardRepository<Game>>(),
    AbstractLeaderBoardRepository<Game> {

    override fun getIOSClassName(): String {
        TODO("Not yet implemented")
    }

    override fun getAndroidClassName(): String {
        return "no.ntnu.beardblaster.LeaderBoardRepository"
    }

    override fun getWebGLClassName(): String {
        TODO("Not yet implemented")
    }

    override fun getTopTenBeards(): Flow<State<List<BeardScore>>> {
        return platformObject.getTopTenBeards()
    }

    override fun updateBeardLength(wizard: Wizard, beardLengthIncrease: Float): Flow<State<BeardScore>> {
        return platformObject.updateBeardLength(wizard, beardLengthIncrease)
    }
}
