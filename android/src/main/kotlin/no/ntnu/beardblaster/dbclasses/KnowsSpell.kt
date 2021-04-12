package no.ntnu.beardblaster.dbclasses

import androidx.room.*

@Entity(tableName = "knows_spell_table",
        primaryKeys = ["wizardID", "spellID"],
        indices = [Index("spellID"), Index("wizardID")]
)
data class KnowsSpell(
        val wizardID : Int,
        val spellID : Int
)

data class SpellsOfWizard(
        @Embedded val wizardDB: WizardDB,
        @Relation(
                parentColumn = "wizardID",
                entityColumn = "spellID",
                associateBy = Junction(KnowsSpell::class)
        )
        val spells: List<Spell>
)