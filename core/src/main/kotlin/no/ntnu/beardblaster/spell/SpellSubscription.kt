package no.ntnu.beardblaster.spell

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import ktx.log.debug
import ktx.log.error
import ktx.log.info
import ktx.log.logger
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.game.GameRepository
import java.util.*

private val LOG = logger<SpellSubscription>()

class SpellSubscription : Observable() {
    var error: String? = null

    @ExperimentalCoroutinesApi
    suspend fun subscribeToUpdatesOn(id: String, currentTurn: Int) {
        LOG.info { "Subscribing to updates on $id" }
        GameRepository().subscribeToSpellsOnTurn(id, currentTurn).collect {
            when (it) {
                is State.Success -> {
                    LOG.info { "Spell from FireStore: ${it.data.spell.spellName}" }
                    LOG.info { "Notifying ${countObservers()} observers"}
                    setChanged()
                    notifyObservers(it.data)
                }
                is State.Loading -> {
                }
                is State.Failed -> {
                    LOG.error { it.message }
                    error = it.message
                    setChanged()
                    notifyObservers(error)
                }
            }
        }
    }

    override fun addObserver(p0: Observer?) {
        super.addObserver(p0)
        LOG.debug { "Adding observer -> $p0" }
    }
}
