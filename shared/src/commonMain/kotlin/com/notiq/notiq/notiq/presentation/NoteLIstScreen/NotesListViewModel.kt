package com.notiq.notiq.notiq.presentation.NoteLIstScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notiq.db.NoteEntity
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

    private val _allNotes = MutableStateFlow<List<NoteEntity>>(emptyList())
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
    val notes: StateFlow<UiState<List<NoteEntity>>> = combine(
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
                        compareByDescending<NoteEntity> { it.isPinned == true }
                            .thenByDescending { it.createdAt }
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

    fun saveNote(
        existingNote: NoteEntity?,
        title: String,
        content: String,
        imagePath : String? = null,
        isPinned: Boolean? = null,
        onSuccess: (NoteEntity) -> Unit = {}
    ) {
        viewModelScope.launch {
            val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
            val note = NoteEntity(
                id = existingNote?.id ?: randomUUID(),
                title = title,
                content = content,
                imagePath = imagePath,
                isPinned = isPinned ?: existingNote?.isPinned ?: false,
                createdAt = existingNote?.createdAt ?: now,
                updatedAt = now
            )

            if (existingNote == null) {
                addNoteUseCase(note)
            } else {
                updateNoteUseCase(note)
            }

            loadNotes()
            onSuccess(note)
        }
    }

    fun getNoteById(id: String): NoteEntity? {
        return _allNotes.value.find { it.id == id }
    }

    fun deleteNote(id: String) {
        viewModelScope.launch {
            deleteNoteUseCase(id)
            loadNotes()
        }
    }
}
