package com.notiq.notiq.data.repository

import com.notiq.db.NoteEntity
import com.notiq.notiq.data.local.database.NotesLocalDataSource
import com.notiq.notiq.data.mapper.toDomain
import com.notiq.notiq.domain.repository.NotesRepository
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
            imagePath = note.imagePath,
            updatedAt = note.updatedAt,
            isPinned = note.isPinned
        )
    }

    override suspend fun insertNote(note: NoteEntity) = withContext(Dispatchers.Default) {
        local.insertNote(
            id = note.id,
            title = note.title,
            content = note.content,
            imagePath = note.imagePath,
            isPinned = note.isPinned,
            createdAt = note.createdAt,
            updatedAt = note.updatedAt
        )
    }

    override suspend fun deleteNote(id: String) = withContext(Dispatchers.Default) {
        local.deleteNote(id)
    }
}
