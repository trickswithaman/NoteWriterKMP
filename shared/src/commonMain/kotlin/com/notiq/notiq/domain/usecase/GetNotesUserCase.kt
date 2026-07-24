package com.notiq.notiq.domain.usecase

import com.notiq.db.NoteEntity
import com.notiq.db.NoteImageEntity
import com.notiq.notiq.domain.model.NoteWithImages
import com.notiq.notiq.domain.repository.NotesRepository

class GetNotesUseCase(private val repo: NotesRepository) {
    suspend operator fun invoke() = repo.getNotesWithImages()
    
    fun search(
        notes: List<NoteWithImages>,
        query: String
    ): List<NoteWithImages> {

        if (query.isBlank()) return notes

        return notes.filter {
            it.note.title?.contains(query, ignoreCase = true) == true ||
                    it.note.content?.contains(query, ignoreCase = true) == true
        }
    }
}

class SearchNotesUseCase {

    operator fun invoke(
        notes: List<NoteWithImages>,
        query: String
    ): List<NoteWithImages> {

        if (query.isBlank()) return notes

        return notes.filter {
            it.note.title?.contains(query, ignoreCase = true) == true ||
                    it.note.content?.contains(query, ignoreCase = true) == true
        }
    }
}

class AddNoteUseCase(private val repo: NotesRepository) {
    suspend operator fun invoke(note: NoteEntity, images: List<NoteImageEntity>) = 
        repo.insertNote(note, images)
}

class UpdateNoteUseCase(private val repo: NotesRepository){
    suspend operator fun invoke(note: NoteEntity, images: List<NoteImageEntity>) = 
        repo.updateNote(note, images)
}

class DeleteNoteUseCase(private val repo: NotesRepository) {
    suspend operator fun invoke(id: String) = repo.deleteNote(id)
}
