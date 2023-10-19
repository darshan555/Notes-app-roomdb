package com.example.notes_app_roomdb.database

import androidx.lifecycle.LiveData

class NoteRepository(private val noteDao: NoteDao) {

    val getAllNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    suspend fun delete(note: Note) {
        noteDao.delete(note)
    }

    suspend fun update(note: Note) {
        noteDao.update(note.id, note.title, note.content)
    }
}
