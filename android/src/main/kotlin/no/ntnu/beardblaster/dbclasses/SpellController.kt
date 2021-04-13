package no.ntnu.beardblaster.dbclasses

import no.ntnu.beardblaster.commons.spell.Spell
import no.ntnu.beardblaster.commons.spell.AbstractSpellRepository

class SpellController(val db : SpellDatabase = SpellDatabase.getInstance()!!) : AbstractSpellRepository {

    override fun getSpellById(id: Int): Spell? {
        return db.spellDao().getSpellById(id)?.toEntity()
    }
}
