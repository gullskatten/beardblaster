package no.ntnu.beardblaster.dbclasses

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [
    Spell::class,
    Element::class,
    WizardDB::class,
    Composition::class,
    KnowsSpell::class,
    Proficiency::class],
        version = 1)
abstract class SpellDatabase: RoomDatabase() {
    abstract fun elementDao() : ElementDao
    abstract fun spellDao() : SpellDao
    companion object {
        @Volatile
        private var INSTANCE: SpellDatabase? = null
        fun getInstance(context: Context): SpellDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            SpellDatabase::class.java,
                            "beardblaster-db"
                    )
                     //       .createFromAsset()
                            .fallbackToDestructiveMigration()
                            .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}