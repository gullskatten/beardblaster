package no.ntnu.beardblaster.models

import ktx.log.debug
import ktx.log.error
import ktx.log.logger
import no.ntnu.beardblaster.hud.ElementChangedObserver
import no.ntnu.beardblaster.hud.ElementClickedObserver
import java.util.*

private val LOG = logger<SpellCasting>()

class SpellCasting : Observer, Observable() {
    private val fire: Element = Element("Fire", 1)
    private val ice: Element = Element("Ice", 2)
    private val nature: Element = Element("Nature", 3)
    var selectedElements: MutableList<Element?> = mutableListOf(null, null, null)

    init {
        ElementClickedObserver.instance.addObserver(this)
    }

    private fun removeElement(index: Int) {
        try {
            selectedElements[index] = null
            LOG.debug { "Removed element at index $index: $selectedElements" }
            ElementChangedObserver.instance.notifyChanged()
        } catch (e: IndexOutOfBoundsException) {
            LOG.error { "No element to remove at index $index from $selectedElements" }
        }
    }

    private fun addElement(element: Element) {
        if (null in selectedElements) {
            selectedElements[selectedElements.indexOfFirst { it == null }] = element
            ElementChangedObserver.instance.notifyChanged()
            LOG.debug { "Added ${element.name}: $selectedElements" }
        } else {
            LOG.debug { "Cannot select more than 3 elements for a spell: $selectedElements" }
        }
    }

    fun addFire() {
        addElement(fire)
    }

    fun addIce() {
        addElement(ice)
    }

    fun addNature() {
        addElement(nature)
    }

/*    fun getSpellID(): Int {
        //Unique spell IDs can be created by using the fundamental theorem of arithmetic, as long as all the distinct elementIDs are uniquely prime. The number of elements needed to be added for this to be a
        //terrible way of uniquely identifying spells is quite high.
        return elementIDs[0] * elementIDs[1] * elementIDs[2]
    }*/

    override fun update(o: Observable, arg: Any) {
        if (o is ElementClickedObserver) {
            removeElement(arg as Int)
        }
    }
}
