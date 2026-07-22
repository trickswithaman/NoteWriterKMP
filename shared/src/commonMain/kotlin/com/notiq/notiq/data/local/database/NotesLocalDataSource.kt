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
}