package com.notiq.notiq.data.repository

import com.notiq.db.NoteEntity
import com.notiq.db.NoteImageEntity
import com.notiq.notiq.data.local.database.NotesLocalDataSource
import com.notiq.notiq.data.mapper.toDomain
import com.notiq.notiq.domain.model.NoteWithImages
import com.notiq.notiq.domain.repository.NotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class NotesRepositoryImpl(
    private val local: NotesLocalDataSource
) : NotesRepository {

    override suspend fun getNotesWithImages(): List<NoteWithImages> = withContext(Dispatchers.Default) {
        val notes = local.getNotes()
        notes.map { note ->
            val images = local.getImagesForNote(note.id)
            NoteWithImages(note = note.toDomain(), images = images)
        }
    }

    override suspend fun getNotes(): List<NoteEntity> = withContext(Dispatchers.Default) {
        local.getNotes().map { it.toDomain() }
    }

    override suspend fun updateNote(note: NoteEntity, images: List<NoteImageEntity>) = withContext(Dispatchers.Default) {
        // Update the note details
        local.updateNote(
            id = note.id,
            title = note.title,
            content = note.content,
            imagePath = note.imagePath,
            updatedAt = note.updatedAt,
            isPinned = note.isPinned
        )
        
        // Relational update: clear old images and insert new ones to maintain order and selection.
        // In a more complex app, we'd diff the lists to only update what's changed.
        local.deleteImagesByNoteId(note.id)
        images.forEach { image ->
            local.insertImage(image.id, image.noteId, image.uri, image.createdAt)
        }
    }

    override suspend fun insertNote(note: NoteEntity, images: List<NoteImageEntity>) = withContext(Dispatchers.Default) {
        local.insertNote(
            id = note.id,
            title = note.title,
            content = note.content,
            imagePath = note.imagePath,
            isPinned = note.isPinned,
            createdAt = note.createdAt,
            updatedAt = note.updatedAt
        )
        
        // Insert all associated images for the new note
        images.forEach { image ->
            local.insertImage(image.id, image.noteId, image.uri, image.createdAt)
        }
    }

    override suspend fun deleteNote(id: String) = withContext(Dispatchers.Default) {
        // If CASCADE DELETE is configured in SQL, this also removes images automatically.
        local.deleteNote(id)
    }
}
