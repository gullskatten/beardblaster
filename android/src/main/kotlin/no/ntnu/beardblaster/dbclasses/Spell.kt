package no.ntnu.beardblaster.dbclasses

import androidx.room.Entity
import androidx.room.PrimaryKey
import no.ntnu.beardblaster.commons.spell.Spell

@Entity(tableName = "spell_table")
data class Spell (
        @PrimaryKey(autoGenerate = false)
        val spellID : Int,
        val spellName : String,
        val spellDamage : Int,
        val spellDescription : String
        ) {
    fun toEntity(): Spell {
        return no.ntnu.beardblaster.commons.spell.Spell(
            spellID,
            spellName,
            spellDamage,
            spellDescription
        )
    }
}
