package no.ntnu.beardblaster.dbclasses

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spell_table")
data class Spell (
        @PrimaryKey
        val spellID : Int,
        val spellName : String,
        val elementName1 : String,
        val elementName2 : String,
        val elementName3 : String,
        val spellDescription : String
        )
