package no.ntnu.beardblaster.dbclasses

import androidx.room.*

@Entity(
        tableName = "composition_table",
        primaryKeys = ["elementID", "spellID"],
        indices = [Index("spellID"), Index("elementID")]
)
data class Composition(
        val elementID : Int,
        val spellID : Int
)

data class ElementsOfSpell(
        @Embedded val spell: Spell,
        @Relation(
                parentColumn = "spellID",
                entityColumn = "elementID",
                associateBy = Junction(Composition::class)
        )
        val elements: List<Element>
)

data class SpellsOfElement(
        @Embedded val element: Element,
        @Relation(
                parentColumn = "elementID",
                entityColumn = "spellID",
                associateBy = Junction(Composition::class)
        )
        val spells: List<Spell>
)