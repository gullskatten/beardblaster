package no.ntnu.beardblaster.dbclasses

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SpellDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSpell(spell : Spell)

    @Query("SELECT * FROM spell_table ORDER BY spellID ASC")
    fun readAllData(): LiveData<List<Spell>>

    @Transaction
    @Query("SELECT * FROM spell_table join element_table")
    fun getSpellsOfElement(): LiveData<List<SpellsOfElement>>
}