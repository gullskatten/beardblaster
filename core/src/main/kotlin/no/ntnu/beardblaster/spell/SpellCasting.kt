package no.ntnu.beardblaster.spell

import ktx.log.debug
import ktx.log.error
import ktx.log.logger
import no.ntnu.beardblaster.commons.spell.Element
import no.ntnu.beardblaster.commons.spell.Spell
import java.util.*

private val LOG = logger<SpellCasting>()

class SpellCasting : Observer, Observable() {
    private val fire: Element = Element(1, "Fire")
    private val ice:  Element =  Element(2, "Ice")
    private val nature:  Element =  Element(3, "Nature")
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

    fun getSelectedSpell(): Spell? {
        if (null in selectedElements) return null
        return SpellRepository().getSpellById(selectedElements.map { elem -> elem?.elementID!! }.reduce { sum, element -> sum * element })
    }

    fun reset() {
        selectedElements = mutableListOf(null, null, null)
        ElementChangedObserver.instance.notifyChanged()
    }

    override fun update(o: Observable, arg: Any) {
        if (o is ElementClickedObserver) {
            removeElement(arg as Int)
        }
    }
}
