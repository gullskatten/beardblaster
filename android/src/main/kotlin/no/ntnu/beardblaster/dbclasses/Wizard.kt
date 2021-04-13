package no.ntnu.beardblaster.dbclasses

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wizard_table")
data class Wizard(
        @PrimaryKey(autoGenerate = true)
        val wizardID : Int,
        val wizardName : String,
        val hitPoints : Int,
        val element1 : Int,
        val element2 : Int,
        val element3 : Int
)
