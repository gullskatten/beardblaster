package no.ntnu.beardblaster.dbclasses

import androidx.room.*

@Entity(
    tableName = "proficiency_table",
    primaryKeys = ["wizard_id", "element_id"],
    indices = [Index("element_id"), Index("wizard_id")]
)
data class Proficiency(
    @ColumnInfo(name = "wizard_id")
    val wizardID : Int,
    @ColumnInfo(name = "element_id")
    val elementID : Int
)

data class ElementsOfWizard(
    @Embedded val wizard: Wizard,
    @Relation(
        parentColumn = "wizard_id",
        entityColumn = "element_id",
        associateBy = Junction(Proficiency::class)
    )
    val elements: List<Element>
)

data class WizardsOfElement(
    @Embedded val element: Element,
    @Relation(
        parentColumn = "element_id",
        entityColumn = "wizard_id",
        associateBy = Junction(Proficiency::class)
    )
    val wizards: List<Wizard>
)
