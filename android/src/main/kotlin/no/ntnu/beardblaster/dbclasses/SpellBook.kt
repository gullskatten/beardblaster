package no.ntnu.beardblaster.dbclasses

import androidx.room.*

@Entity(
    tableName = "spell_book_table",
    primaryKeys = ["wizard_id", "spell_id"],
    indices = [Index("spell_id"), Index("wizard_id")]
)
data class SpellBook(
        @ColumnInfo(name = "wizard_id")
        val wizardID : Int,
        @ColumnInfo(name = "spell_id")
        val spellID : Int
)

data class SpellsOfWizard(
    @Embedded val wizard: Wizard,
    @Relation(
                parentColumn = "wizard_id",
                entityColumn = "spell_id",
                associateBy = Junction(SpellBook::class)
        )
        val spells: List<Spell>
)
