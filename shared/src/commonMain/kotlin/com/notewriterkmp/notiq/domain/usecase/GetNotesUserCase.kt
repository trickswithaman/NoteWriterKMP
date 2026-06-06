package com.notewriterkmp.notiq.domain.usecase

import com.notewriterkmp.db.NoteEntity
import com.notewriterkmp.notiq.domain.repository.NotesRepository

class GetNotesUseCase(private val repo: NotesRepository) {
    suspend operator fun invoke() = repo.getNotes()
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