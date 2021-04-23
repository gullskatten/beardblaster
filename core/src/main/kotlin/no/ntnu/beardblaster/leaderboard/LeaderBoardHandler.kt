package no.ntnu.beardblaster.leaderboard

import kotlinx.coroutines.flow.collect
import ktx.log.debug
import ktx.log.info
import ktx.log.logger
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.game.GamePlayer
import no.ntnu.beardblaster.commons.leaderboard.BeardScore
import java.util.*

private val LOG = logger<LeaderBoardHandler>()

class LeaderBoardHandler : Observable() {
    var topTen: List<BeardScore> = listOf()
        private set

    var error: String? = null
        private set

    var isLoading: Boolean = false
        private set

    suspend fun getTopTenBeards() {
        topTen = listOf()
        LeaderBoardRepository().getTopTenBeards().collect {
            setChanged()
            when (it) {
                is State.Success -> {
                    topTen = it.data
                    LOG.info { "Notifying observers of leaderboard list ${it.data}" }
                    notifyObservers(it.data)
                }
                is State.Loading -> {
                    notifyObservers("Loading..")
                }
                is State.Failed -> {
                    notifyObservers(it.message)
                }
            }
        }
    }


    suspend fun growBeard(user: GamePlayer, amount: Float) {
        if (amount < 0) throw IllegalArgumentException("You can't grow a beard by a negative amount")
        LeaderBoardRepository().updateBeardLength(user, user.beardLength + amount).collect {
            when (it) {
                is State.Success -> {
                    LOG.debug { "New length of ${user.displayName}'s beard after growing is ${it.data.beardLength}  }" }
                }
                is State.Loading -> {
                }
                is State.Failed -> {
                    LOG.debug { it.message }
                }
            }
        }
    }

    suspend fun trimBeard(user: GamePlayer, amount: Float) {
        var newBeardLength = user.beardLength - amount
        if (amount < 0) throw IllegalArgumentException("The amount to trim the beard must be positive")
        if (user.beardLength - amount < 0) newBeardLength = 0f
        LeaderBoardRepository().updateBeardLength(user, newBeardLength).collect {
            when (it) {
                is State.Success -> {
                    LOG.debug { "New length of ${user.displayName}'s beard after trimming is ${it.data.beardLength}  }" }
                }
                is State.Loading -> {
                }
                is State.Failed -> {
                    LOG.debug { it.message }
                }
            }
        }
    }
}
