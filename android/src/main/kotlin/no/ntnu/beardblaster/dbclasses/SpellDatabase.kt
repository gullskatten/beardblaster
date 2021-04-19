package no.ntnu.beardblaster.dbclasses

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Spell::class, Element::class, Wizard::class, Composition::class, SpellBook::class, Proficiency::class], version = 4)
abstract class SpellDatabase: RoomDatabase() {

    abstract fun elementDao() : ElementDao
    abstract fun spellDao() : SpellDao
    abstract fun wizardDao() : WizardDao


    companion object {
        // Use this to call on any place
        fun getInstance(): SpellDatabase? {
            return INSTANCE
        }

        @Volatile
        private var INSTANCE: SpellDatabase? = null
        fun initialize(context: Context): SpellDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SpellDatabase::class.java,
                        "beardblaster-db"
                    ).createFromAsset("bb-db-v4.db")
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
