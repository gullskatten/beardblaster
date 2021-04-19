package no.ntnu.beardblaster.dbclasses

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(primaryKeys = ["wizardID", "spellID"])
data class SpellBook(
        val wizardID : Int,
        val spellID : Int
)

data class SpellsOfWizard(
    @Embedded val wizard: Wizard,
    @Relation(
                parentColumn = "wizardID",
                entityColumn = "spellID",
                associateBy = Junction(SpellBook::class)
        )
        val spells: List<Spell>
)
