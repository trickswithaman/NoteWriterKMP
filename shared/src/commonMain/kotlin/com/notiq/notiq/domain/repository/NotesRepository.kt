package com.notiq.notiq.domain.repository

import com.notiq.db.NoteEntity
import com.notiq.db.NoteImageEntity
import com.notiq.notiq.domain.model.NoteWithImages


interface NotesRepository {

    /**
     * Retrieves all notes along with their associated images.
     */
    suspend fun getNotesWithImages(): List<NoteWithImages>

    /**
     * Legacy method for retrieving simple note entities.
     */
    suspend fun getNotes(): List<NoteEntity>

    suspend fun updateNote(note: NoteEntity, images: List<NoteImageEntity>)

    suspend fun insertNote(note: NoteEntity, images: List<NoteImageEntity>)

    suspend fun deleteNote(id: String)
}
