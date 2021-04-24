package no.ntnu.beardblaster.leaderboard

import kotlinx.coroutines.flow.collect
import ktx.log.info
import ktx.log.logger
import no.ntnu.beardblaster.commons.State
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
}
