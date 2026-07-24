package com.notiq.notiq.data.local.database

import com.notiq.db.NotesDatabase


class NotesLocalDataSource(
    private val db: NotesDatabase
) {

    private val queries = db.notesDatabaseQueries

    fun getNotes() =
        queries.selectAll().executeAsList()

    fun insertNote(
        id: String,
        title: String?,
        content: String?,
        imagePath: String?,
        isPinned: Boolean,
        createdAt: Long,
        updatedAt: Long
    ) {
        queries.insertNote(id, title, content,imagePath, isPinned, createdAt,updatedAt)
    }

    fun updateNote(
        id: String,
        title: String?,
        imagePath: String?,
        content: String?,
        isPinned: Boolean,
        updatedAt: Long
    ) {
        queries.updateNote(
            id = id,
            title = title,
            content = content,
            imagePath = imagePath,
            isPinned = isPinned,
            updatedAt = updatedAt
        )
    }

    fun deleteNote(id: String) {
        queries.deleteNote(id)
    }

    // --- Image Related Methods ---

    /**
     * Retrieves all images associated with a specific note ID.
     * This supports the one-to-many relational structure.
     */
    fun getImagesForNote(noteId: String) =
        queries.getImagesForNote(noteId).executeAsList()

    /**
     * Inserts a new image entry into the database.
     */
    fun insertImage(id: String, noteId: String, uri: String, createdAt: Long) {
        queries.insertImage(id, noteId, uri, createdAt)
    }

    /**
     * Deletes a specific image by its ID.
     */
    fun deleteImageById(id: String) {
        queries.deleteImageById(id)
    }

    /**
     * Deletes all images associated with a note. 
     * Note: If 'ON DELETE CASCADE' is active in SQL, this might be redundant on note deletion,
     * but it's useful for clearing a note's images without deleting the note itself.
     */
    fun deleteImagesByNoteId(noteId: String) {
        queries.deleteImagesByNoteId(noteId)
    }
}
