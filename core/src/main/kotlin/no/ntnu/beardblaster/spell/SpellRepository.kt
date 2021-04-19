package no.ntnu.beardblaster.spell

import no.ntnu.beardblaster.commons.spell.AbstractSpellRepository
import no.ntnu.beardblaster.commons.spell.Element
import no.ntnu.beardblaster.commons.spell.Spell
import pl.mk5.gdx.fireapp.PlatformDistributor

class SpellRepository : PlatformDistributor<AbstractSpellRepository>(), AbstractSpellRepository {
    override fun getIOSClassName(): String {
        TODO("Not yet implemented")
    }

    override fun getAndroidClassName(): String {
       return "no.ntnu.beardblaster.dbclasses.SpellController"
    }

    override fun getWebGLClassName(): String {
        TODO("Not yet implemented")
    }

    override fun getSpellById(id: Int): Spell? {
       return platformObject.getSpellById(id)
    }

    override fun getAllElements(): List<Element> {
        return platformObject.getAllElements()
    }
    /*override fun readAllSpellData(): List<Spell?> {
        return platformObject.readAllSpellData()
    }*/
}
