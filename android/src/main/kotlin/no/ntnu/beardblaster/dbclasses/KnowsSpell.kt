package no.ntnu.beardblaster.dbclasses

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(primaryKeys = ["wizardID", "spellID"])
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