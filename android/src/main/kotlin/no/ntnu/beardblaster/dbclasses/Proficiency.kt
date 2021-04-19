package no.ntnu.beardblaster.dbclasses

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(primaryKeys = ["wizardID", "elementID"])
data class Proficiency(
    val wizardID : Int,
    val elementID : Int
)

data class ElementsOfWizard(
    @Embedded val wizard: Wizard,
    @Relation(
        parentColumn = "wizardID",
        entityColumn = "elementID",
        associateBy = Junction(Proficiency::class)
    )
    val elements: List<Element>
)
