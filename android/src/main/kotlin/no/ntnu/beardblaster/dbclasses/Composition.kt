package no.ntnu.beardblaster.dbclasses

import androidx.room.*

@Entity(
        tableName = "composition_table",
        primaryKeys = ["element_id", "spell_id"],
        indices = [Index("spell_id"), Index("element_id")]
)
data class Composition(
    @ColumnInfo(name = "element_id")
    val elementID : Int,
    @ColumnInfo(name = "spell_id")
    val spellID : Int
)

data class ElementsOfSpell(
    @Embedded val spell: Spell,
    @Relation(
                parentColumn = "spell_id",
                entityColumn = "element_id",
                associateBy = Junction(Composition::class)
        )
        val elements: List<Element>
)

data class SpellsOfElement(
        @Embedded val element: Element,
        @Relation(
                parentColumn = "element_id",
                entityColumn = "spell_id",
                associateBy = Junction(Composition::class)
        )
        val spells: List<Spell>
)
