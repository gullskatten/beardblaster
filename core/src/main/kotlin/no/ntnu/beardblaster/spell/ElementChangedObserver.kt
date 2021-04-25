package no.ntnu.beardblaster.spell

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
