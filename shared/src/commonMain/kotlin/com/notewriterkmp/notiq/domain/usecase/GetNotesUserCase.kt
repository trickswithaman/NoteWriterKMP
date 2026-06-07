package com.notewriterkmp.notiq.domain.usecase

import com.notewriterkmp.db.NoteEntity
import com.notewriterkmp.notiq.domain.repository.NotesRepository

class GetNotesUseCase(private val repo: NotesRepository) {
    suspend operator fun invoke() = repo.getNotes()
    fun search(
        notes: List<NoteEntity>,
        query: String
    ): List<NoteEntity> {

        if (query.isBlank()) return notes

        return notes.filter {
            it.title?.contains(query, ignoreCase = true) == true ||
                    it.content?.contains(query, ignoreCase = true) == true
        }
    }

}
class SearchNotesUseCase {

    operator fun invoke(
        notes: List<NoteEntity>,
        query: String
    ): List<NoteEntity> {

        if (query.isBlank()) return notes

        return notes.filter {
            it.title?.contains(query, ignoreCase = true) == true ||
                    it.content?.contains(query, ignoreCase = true) == true
        }
    }
}

class AddNoteUseCase(private val repo: NotesRepository) {
    suspend operator fun invoke(note: NoteEntity) = repo.insertNote(note)
}

class UpdateNoteUseCase(private val repo: NotesRepository){
    suspend operator fun invoke(note: NoteEntity) = repo.updateNote(note)
}

class DeleteNoteUseCase(private val repo: NotesRepository) {
    suspend operator fun invoke(id: String) = repo.deleteNote(id)
}