package no.ntnu.beardblaster.dbclasses

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction


@Dao
interface ElementDao {
    @Query("SELECT * FROM element_table")
    fun getAllElements(): List<Element>

    @Transaction
    @Query("SELECT * FROM element_table JOIN spell_table")
    fun getElementsOfSpell(): LiveData<List<ElementsOfSpell>>

}
