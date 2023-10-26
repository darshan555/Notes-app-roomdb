package com.example.notes_app_roomdb.database

import androidx.lifecycle.LiveData

class NoteRepository(private val noteDao: NoteDao) {

    val getAllNotes: LiveData<List<Note>> = noteDao.getAllNotes()
    val getAllFilterNotes: LiveData<List<Note>> = noteDao.getNonRecycledItems()
    val getAllDeletedNotes: LiveData<List<Note>> = noteDao.getDeletedNote()

     fun insert(note: Note) {
        noteDao.insert(note)
    }

    fun delete(note: Note) {
        noteDao.delete(note)
    }
    fun deleteNotes(noteIds: List<Int>) {
        noteDao.deleteAll(noteIds)
    }
    fun restoreNotes(noteIds: List<Int>) {
        noteDao.restoreAll(noteIds)
    }
    fun tempDelete(noteIds: List<Int>) {
        noteDao.tempDelete(noteIds)
    }

     fun update(note: Note) {
        noteDao.update(note.id, note.title, note.content,note.updatedDate)
    }



}
