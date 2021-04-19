package no.ntnu.beardblaster.dbclasses

import androidx.room.Dao
import androidx.room.Query


@Dao
interface WizardDao {
    @Query("SELECT * FROM wizard_table")
    fun getAllWizards(): List<Wizard>
}
