package com.notewriterkmp.notiq.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notewriterkmp.db.NoteEntity
import com.notewriterkmp.notiq.domain.usecase.AddNoteUseCase
import com.notewriterkmp.notiq.domain.usecase.DeleteNoteUseCase
import com.notewriterkmp.notiq.domain.usecase.GetNotesUseCase
import com.notewriterkmp.notiq.util.randomUUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock

class NotesListViewModel(
    private val getNotes: GetNotesUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : ViewModel() {

    private val _notes = MutableStateFlow<List<NoteEntity>>(emptyList())
    val notes: StateFlow<List<NoteEntity>> = _notes

    fun loadNotes() {
        viewModelScope.launch {
            _notes.value = getNotes()
        }
    }

    fun addNote(title: String) {
        viewModelScope.launch {
            addNoteUseCase(
                NoteEntity(
                    id = randomUUID(),
                    title = title,
                    content = "",
                    isPinned = false,
                    createdAt = 0L
                )
            )
            loadNotes()
        }
    }
    fun deleteNote(id: String) {
        viewModelScope.launch {
            deleteNoteUseCase(id)
            loadNotes()
        }
    }
}