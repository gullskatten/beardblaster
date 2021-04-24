package no.ntnu.beardblaster.dbclasses

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SpellDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSpell(spell : Spell)

    @Query("SELECT * FROM spell_table ORDER BY spell_id ASC")
    fun readAllSpellData(): List<Spell>

    @Transaction
    @Query("SELECT spell_table.* FROM composition_table INNER JOIN spell_table ON composition_table.spell_id = spell_table.spell_id WHERE element_id = :elementID")
    fun getSpellsOfElement(elementID: Int): List<Spell>

    @Query("SELECT * FROM spell_table WHERE spell_id = :spellID LIMIT 1")
    fun getSpellById(spellID : Int) : Spell?
}
