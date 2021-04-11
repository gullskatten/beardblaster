package no.ntnu.beardblaster.dbclasses

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "element_table")
data class Element(
    @PrimaryKey
    val elementID : Int,
    val elementName : String
    )
