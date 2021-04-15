package no.ntnu.beardblaster.dbclasses

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Spell::class, Element::class, Wizard::class, Composition::class, KnowsSpell::class], version = 2)
abstract class SpellDatabase: RoomDatabase() {

    abstract fun elementDao() : ElementDao
    abstract fun spellDao() : SpellDao


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
                    ).createFromAsset("bb-db-v2.db")
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
