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
    fun insert(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("SELECT * FROM notes order by id ASC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("UPDATE notes set title = :title, note = :content where id = :id")
    fun update(id: Int?, title: String?, content: String?)
    @Query("DELETE FROM notes WHERE id IN (:noteIds)")
    fun deleteAll(noteIds: List<Int>)


}