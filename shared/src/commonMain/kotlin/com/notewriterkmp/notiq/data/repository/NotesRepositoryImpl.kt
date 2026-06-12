package com.notewriterkmp.notiq.data.repository

import com.notewriterkmp.db.NoteEntity
import com.notewriterkmp.notiq.data.local.database.NotesLocalDataSource
import com.notewriterkmp.notiq.data.mapper.toDomain
import com.notewriterkmp.notiq.domain.repository.NotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class NotesRepositoryImpl(
    private val local: NotesLocalDataSource
) : NotesRepository {

    override suspend fun getNotes(): List<NoteEntity> = withContext(Dispatchers.Default) {
        local.getNotes().map { it.toDomain() }
    }

    override suspend fun updateNote(note: NoteEntity) = withContext(Dispatchers.Default) {
        local.updateNote(
            id = note.id,
            title = note.title,
            content = note.content,
            updatedAt = note.updatedAt,
            isPinned = note.isPinned
        )
    }

    override suspend fun insertNote(note: NoteEntity) = withContext(Dispatchers.Default) {
        local.insertNote(
            id = note.id,
            title = note.title,
            content = note.content,
            isPinned = note.isPinned,
            createdAt = note.createdAt,
            updatedAt = note.updatedAt
        )
    }

    override suspend fun deleteNote(id: String) = withContext(Dispatchers.Default) {
        local.deleteNote(id)
    }
}
