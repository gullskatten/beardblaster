package no.ntnu.beardblaster.dbclasses

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SpellDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSpell(spell : Spell)

    @Query("SELECT * FROM spell_table ORDER BY spellID ASC")
    fun readAllSpellData(): List<Spell>

    @Transaction
    @Query("SELECT * FROM element_table WHERE elementID = :elementID")
    fun getSpellsOfElement(elementID: Int): LiveData<List<SpellsOfElement>>

    @Transaction
    @Query("SELECT * FROM spell_table WHERE spellID = :spellID")
    fun getElementsOfSpell(spellID : Int): LiveData<List<ElementsOfSpell>>

    @Query("SELECT * FROM spell_table WHERE spellID = :spellID LIMIT 1")
    fun getSpellById(spellID : Int) : Spell?
}
