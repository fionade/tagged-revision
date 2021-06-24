package de.lmu.nfcrevision.datahandling

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Question::class, Location::class], version = 1, exportSchema = true)
abstract class QuestionDatabase: RoomDatabase() {

    abstract fun questionDao(): QuestionDao
    abstract fun locationDao(): LocationDao

    companion object {
        @Volatile
        private var INSTANCE: QuestionDatabase? = null

        fun getDatabase(context: Context): QuestionDatabase {
            val tempInstance =
                INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuestionDatabase::class.java,
                    "quiz_database"
                )
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}