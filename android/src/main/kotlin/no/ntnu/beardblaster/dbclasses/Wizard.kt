package no.ntnu.beardblaster.dbclasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wizard_table")
data class Wizard(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "wizard_id")
        val wizardID : Int,
        @ColumnInfo(name = "wizard_name")
        val wizardName : String,
        @ColumnInfo(name = "hit_points")
        val hitPoints : Int = 30
)
