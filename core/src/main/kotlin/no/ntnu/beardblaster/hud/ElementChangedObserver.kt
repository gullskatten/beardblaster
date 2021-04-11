package no.ntnu.beardblaster.hud

import java.util.*

class ElementChangedObserver private constructor() : Observable() {
    fun notifyChanged() {
        setChanged()
        notifyObservers()
    }

    companion object {
        val instance = ElementChangedObserver()
    }
}
