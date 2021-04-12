package no.ntnu.beardblaster.dbclasses

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SpellDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSpell(spell : Spell)

    @Query("SELECT * FROM spell_table ORDER BY spellID ASC")
    fun readAllData(): LiveData<List<Spell>>

    @Query("SELECT * FROM spell_table WHERE spellID = :spellID")
    fun readSpellData(spellID : Int): LiveData<List<Spell>>

    @Transaction
    @Query("SELECT * FROM spell_table JOIN element_table")
    fun getSpellsOfElement(): LiveData<List<SpellsOfElement>>

    @Transaction
    @Query("SELECT * FROM spell_table JOIN wizard_table")
    fun getSpellsOfWizard(): LiveData<List<SpellsOfWizard>>

}