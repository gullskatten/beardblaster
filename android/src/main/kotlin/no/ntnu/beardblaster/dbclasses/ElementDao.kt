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
    @Query("SELECT element_table.* FROM composition_table INNER JOIN element_table ON composition_table.element_id = element_table.element_id WHERE spell_id = :spellID")
    fun getElementsOfSpell(spellID : Int): List<Element>

    @Transaction
    @Query("SELECT element_table.* FROM proficiency_table INNER JOIN element_table ON proficiency_table.element_id = element_table.element_id WHERE wizard_id = :wizardID")
    fun getElementsOfWizard(wizardID : Int): List<Element>

}
