package no.ntnu.beardblaster.dbclasses

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.spell.Spell
import no.ntnu.beardblaster.commons.spell.AbstractSpellRepository

class SpellController(val db : SpellDatabase = SpellDatabase.getInstance()!!) : AbstractSpellRepository {

    override fun getSpellById(id: Int): Spell? {
        return db.spellDao().getSpellById(id)?.toEntity()
    }
}
