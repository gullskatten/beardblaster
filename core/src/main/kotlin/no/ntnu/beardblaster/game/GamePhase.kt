package no.ntnu.beardblaster.game

import ktx.log.info
import ktx.log.logger
import java.util.*

private val LOG = logger<GamePhase>()
class GamePhase : Observable() {
    private var currentPhase: Phase = Phase.Preparation

    fun setCurrentPhase(phase: Phase) {
        LOG.info { "Set phase was called with arg $phase - is $currentPhase"}
        // Limit updates to changes only
        if (currentPhase != phase) {
            currentPhase = phase
            LOG.info { " Notifying observers (${countObservers()}) of new phase "}
            setChanged()
            notifyObservers(currentPhase)
        }
    }

    fun getCurrentPhase(): Phase {
        return currentPhase
    }
}
