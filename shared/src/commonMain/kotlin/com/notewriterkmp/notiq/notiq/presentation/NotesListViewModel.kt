package com.notewriterkmp.notiq.notiq.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notewriterkmp.db.NoteEntity
import com.notewriterkmp.notiq.domain.usecase.AddNoteUseCase
import com.notewriterkmp.notiq.domain.usecase.DeleteNoteUseCase
import com.notewriterkmp.notiq.domain.usecase.GetNotesUseCase
import com.notewriterkmp.notiq.domain.usecase.UpdateNoteUseCase
import com.notewriterkmp.notiq.notiq.util.randomUUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock

class NotesListViewModel(
    private val getNotes: GetNotesUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase
) : ViewModel() {


    private val _notes = MutableStateFlow<List<NoteEntity>>(emptyList())
    val notes: StateFlow<List<NoteEntity>> = _notes

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var allNotes: List<NoteEntity> = emptyList()

    fun loadNotes() {
        viewModelScope.launch {
            allNotes = getNotes()
            applyFilter()
        }
    }

    fun onSearch(query: String) {
        _searchQuery.value = query
        applyFilter()
    }

    private fun applyFilter() {
        _notes.value = getNotes.search(allNotes, _searchQuery.value)
    }

    val currentTime = Clock.System.now().toEpochMilliseconds()

    fun saveNote(
        existingNote: NoteEntity?,
        title: String,
        content: String
    ) {
        viewModelScope.launch {



            val note = NoteEntity(
                id = existingNote?.id ?: randomUUID(),
                title = title,
                content = content,
                isPinned = existingNote?.isPinned ?: false,
                createdAt = existingNote?.createdAt ?: currentTime,
                updatedAt = currentTime
            )

            if (existingNote == null) {
                addNoteUseCase(note)
            } else {
                updateNoteUseCase(note)
            }

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