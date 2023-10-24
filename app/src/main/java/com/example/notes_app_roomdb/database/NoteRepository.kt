package com.example.notes_app_roomdb.database

import androidx.lifecycle.LiveData

class NoteRepository(private val noteDao: NoteDao) {

    val getAllNotes: LiveData<List<Note>> = noteDao.getAllNotes()

     fun insert(note: Note) {
        noteDao.insert(note)
    }

    fun delete(note: Note) {
        noteDao.delete(note)
    }
    fun deleteNotes(noteIds: List<Int>) {
        noteDao.deleteAll(noteIds)
    }

     fun update(note: Note) {
        noteDao.update(note.id, note.title, note.content)
    }

}
