package com.example.notes_app_roomdb.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * FROM notes order by id ASC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("UPDATE notes set title = :title, note = :content where id = :id")
    suspend fun update(id: Int?, title: String?, content: String?)

    @Delete
    suspend fun deleteNotes(notes: List<Note>)
}