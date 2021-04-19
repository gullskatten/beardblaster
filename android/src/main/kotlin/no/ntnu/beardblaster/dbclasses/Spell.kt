package no.ntnu.beardblaster.dbclasses

import androidx.room.Entity
import androidx.room.PrimaryKey
import no.ntnu.beardblaster.commons.spell.Spell

@Entity(tableName = "spell_table")
data class Spell (
        @PrimaryKey(autoGenerate = false)
        val spellID : Int,
        val spellName : String,
        val spellHealing : Int,
        val spellDamage : Int,
        val spellMitigation : Int,
        val spellDescription : String,
        val duration : Int
        ) {
    fun toEntity(): Spell {
        return Spell(
            spellID,
            spellName,
            spellHealing,
            spellDamage,
            spellMitigation,
            spellDescription,
            duration
        )
    }
}
