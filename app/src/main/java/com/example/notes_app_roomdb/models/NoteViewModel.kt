package com.example.notes_app_roomdb.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.notes_app_roomdb.database.Note
import com.example.notes_app_roomdb.database.NoteDatabase
import com.example.notes_app_roomdb.database.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(application: Application):AndroidViewModel(application) {
    private val repository: NoteRepository
    val allNote : LiveData<List<Note>>
    val filterNote : LiveData<List<Note>>
    val deletedNote : LiveData<List<Note>>

    init{
        val dao = NoteDatabase.getDatabase(application).getNoteDAO()
        repository = NoteRepository(dao)
        allNote = repository.getAllNotes
        filterNote = repository.getAllFilterNotes
        deletedNote = repository.getAllDeletedNotes

    }

    fun insertNote(note: Note) = viewModelScope.launch(Dispatchers.IO){
        repository.insert(note)
    }
    fun updateNote(note: Note) = viewModelScope.launch(Dispatchers.IO){
        repository.update(note)
    }
    fun deleteNote(note: Note) = viewModelScope.launch(Dispatchers.IO){
        repository.delete(note)
    }
    fun deleteSelectedNotes(notesToDelete: List<Int>) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteNotes(notesToDelete)
    }
    fun temporaryDelete(notesToDelete: List<Int>) = viewModelScope.launch(Dispatchers.IO) {
        repository.tempDelete(notesToDelete)
    }

}

