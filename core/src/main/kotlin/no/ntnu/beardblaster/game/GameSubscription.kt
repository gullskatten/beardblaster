package no.ntnu.beardblaster.game

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import ktx.log.error
import ktx.log.info
import ktx.log.logger
import no.ntnu.beardblaster.commons.State
import java.util.*

private val LOG = logger<GameSubscription>()

class GameSubscription : Observable() {
    var error: String? = null

    @ExperimentalCoroutinesApi
    suspend fun subscribeToUpdatesOn(id: String) {
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
}
