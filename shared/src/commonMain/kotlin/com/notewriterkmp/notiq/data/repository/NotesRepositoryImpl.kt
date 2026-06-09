package com.notewriterkmp.notiq.data.repository

import com.notewriterkmp.db.NoteEntity
import com.notewriterkmp.notiq.data.local.database.NotesLocalDataSource
import com.notewriterkmp.notiq.data.mapper.toDomain
import com.notewriterkmp.notiq.domain.repository.NotesRepository


class NotesRepositoryImpl(
    private val local: NotesLocalDataSource
) : NotesRepository {

    override suspend fun getNotes(): List<NoteEntity> {
        return local.getNotes().map { it.toDomain() }
    }

    override suspend fun updateNote(note: NoteEntity) {
        local.updateNote(
            id = note.id,
            title = note.title,
            content = note.content,
            updatedAt = note.updatedAt,
            isPinned = note.isPinned
        )
    }



    override suspend fun insertNote(note: NoteEntity) {
        local.insertNote(
            id = note.id,
            title = note.title,
            content = note.content,
            isPinned = note.isPinned,
            createdAt = note.createdAt,
            updatedAt = note.updatedAt
        )
    }

    override suspend fun deleteNote(id: String) {
        local.deleteNote(id)
    }
}