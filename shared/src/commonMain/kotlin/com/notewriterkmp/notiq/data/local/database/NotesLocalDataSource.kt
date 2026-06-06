package com.notewriterkmp.notiq.data.local.database

import com.notewriterkmp.db.NotesDatabase


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
        isPinned: Boolean,
        createdAt: Long,
        updatedAt: Long
    ) {
        queries.insertNote(id, title, content, isPinned, createdAt,updatedAt)
    }

    fun updateNote(
        id: String,
        title: String?,
        content: String?,
        isPinned: Boolean,
        updatedAt: Long
    ) {
        queries.updateNote(
            id = id ,
            title = title,
            content = content,
            isPinned = isPinned,
            updatedAt = updatedAt,

        )
    }

    fun deleteNote(id: String) {
        queries.deleteNote(id)
    }
}