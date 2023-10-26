package com.example.notes_app_roomdb.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("SELECT * FROM notes order by id ASC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE isInRecycleBin = 0")
    fun getNonRecycledItems(): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE isInRecycleBin = 1")
    fun getDeletedNote(): LiveData<List<Note>>

    @Query("UPDATE notes set title = :title, note = :content,updated_date = :updatedDate where id = :id")
    fun update(id: Int?, title: String?, content: String?, updatedDate: String?)

    @Query("DELETE FROM notes WHERE id IN (:noteIds)")
    fun deleteAll(noteIds: List<Int>)

    @Query("UPDATE notes SET isInRecycleBin = 0 WHERE id = :noteId")
    fun restoreAll(noteId: Int)
    @Transaction
    fun restoreAll(noteIds: List<Int>) {
        for (noteId in noteIds) {
            restoreAll(noteId)
        }
    }

    @Query("UPDATE notes SET isInRecycleBin = 1 WHERE id = :noteId")
    fun tempDelete(noteId: Int)
    @Transaction
    fun tempDelete(noteIds: List<Int>) {
        for (noteId in noteIds) {
            tempDelete(noteId)
        }
    }





}