package com.notiq.notiq.notiq.presentation.NoteLIstScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notiq.db.NoteEntity
import com.notiq.db.NoteImageEntity
import com.notiq.notiq.domain.model.NoteWithImages
import com.notiq.notiq.domain.usecase.AddNoteUseCase
import com.notiq.notiq.domain.usecase.DeleteNoteUseCase
import com.notiq.notiq.domain.usecase.GetNotesUseCase
import com.notiq.notiq.domain.usecase.UpdateNoteUseCase
import com.notiq.notiq.notiq.util.UiState
import com.notiq.notiq.notiq.util.randomUUID
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesListViewModel(
    private val getNotes: GetNotesUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase
) : ViewModel() {

    private val settings: Settings = Settings()
    private val VIEW_MODE_KEY = "is_grid_view"

    // _allNotes now holds the full NoteWithImages objects for relational data management.
    private val _allNotes = MutableStateFlow<List<NoteWithImages>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)

    private val _isGridView = MutableStateFlow(settings.getBoolean(VIEW_MODE_KEY, false))
    val isGridView = _isGridView.asStateFlow()

    init {
        loadNotes()
    }

    @OptIn(FlowPreview::class)
    val notes: StateFlow<UiState<List<NoteWithImages>>> = combine(
        _allNotes,
        _searchQuery.debounce(300L),
        _isLoading,
        _error
    ) { notes, query, isLoading, error ->
        when {
            isLoading -> UiState.Loading
            error != null -> UiState.Error(error)
            else -> {
                val filteredNotes = getNotes.search(notes, query)
                    .sortedWith(
                        compareByDescending<NoteWithImages> { it.note.isPinned == true }
                            .thenByDescending { it.note.createdAt }
                    )
                if (filteredNotes.isEmpty()) UiState.Empty else UiState.Success(filteredNotes)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)


    fun loadNotes() {
        viewModelScope.launch {
            if (_allNotes.value.isEmpty()) {
                _isLoading.value = true
            }
            try {
                _allNotes.value = getNotes()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onSearch(query: String) {
        _searchQuery.value = query
    }

    fun toggleViewMode() {
        val newValue = !_isGridView.value
        _isGridView.value = newValue
        settings[VIEW_MODE_KEY] = newValue
    }

    /**
     * Saves or updates a note with its associated images.
     * 
     * @param existingNoteId The ID of the note if updating.
     * @param imageUris A list of URIs for the images to be linked with this note.
     */
    fun saveNote(
        existingNoteId: String? = null,
        title: String,
        content: String,
        imageUris: List<String> = emptyList(),
        isPinned: Boolean = false,
        onSuccess: (NoteWithImages) -> Unit = {}
    ) {
        val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
        val noteId = existingNoteId ?: randomUUID()
        
        val note = NoteEntity(
            id = noteId,
            title = title,
            content = content,
            // imagePath is now a simplified preview (the first image)
            imagePath = imageUris.firstOrNull(),
            isPinned = isPinned,
            createdAt = now, // This might need to be refined for updates
            updatedAt = now
        )
        
        val images = imageUris.map { uri ->
            NoteImageEntity(
                id = randomUUID(),
                noteId = noteId,
                uri = uri,
                createdAt = now
            )
        }
        
        val noteWithImages = NoteWithImages(note, images)

        // Optimistic update
        val currentNotes = _allNotes.value.toMutableList()
        val index = currentNotes.indexOfFirst { it.note.id == noteId }
        if (index != -1) {
            currentNotes[index] = noteWithImages
        } else {
            currentNotes.add(0, noteWithImages)
        }
        _allNotes.value = currentNotes

        viewModelScope.launch {
            try {
                if (existingNoteId == null) {
                    addNoteUseCase(note, images)
                } else {
                    updateNoteUseCase(note, images)
                }
                onSuccess(noteWithImages)
            } catch (_: Exception) {
                loadNotes()
            }
        }
    }

    fun getNoteById(id: String): NoteWithImages? {
        return _allNotes.value.find { it.note.id == id }
    }

    fun deleteNote(id: String) {
        viewModelScope.launch {
            deleteNoteUseCase(id)
            loadNotes()
        }
    }
}
