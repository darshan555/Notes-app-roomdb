package com.example.notes_app_roomdb.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Note::class], version = 13, exportSchema = false)
abstract class NoteDatabase: RoomDatabase()
{
    abstract fun getNoteDAO(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_db",
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}