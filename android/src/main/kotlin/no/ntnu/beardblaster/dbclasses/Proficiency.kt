package no.ntnu.beardblaster.dbclasses

import androidx.room.*

@Entity(
        tableName = "proficiency_table",
        primaryKeys = ["wizardID", "elementID"],
        indices = [Index("elementID"), Index("wizardID")]
)
data class Proficiency(
        val wizardID : Int,
        val elementID : Int
)

data class ElementsOfWizard(
        @Embedded val wizardDB: WizardDB,
        @Relation(
                parentColumn = "wizardID",
                entityColumn = "elementID",
                associateBy = Junction(Proficiency::class)
        )
        val elements: List<Element>
)