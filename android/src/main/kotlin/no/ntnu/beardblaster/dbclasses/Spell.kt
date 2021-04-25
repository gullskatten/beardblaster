package no.ntnu.beardblaster.dbclasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import no.ntnu.beardblaster.commons.spell.Spell

@Entity(tableName = "spell_table")
data class Spell (
        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name = "spell_id")
        val spellID : Int,
        @ColumnInfo(name = "spell_name")
        val spellName : String,
        @ColumnInfo(name = "healing_value")
        val spellHealing : Int,
        @ColumnInfo(name = "damage_value")
        val spellDamage : Int,
        @ColumnInfo(name = "mitigation_value")
        val spellMitigation : Int,
        @ColumnInfo(name = "description")
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
