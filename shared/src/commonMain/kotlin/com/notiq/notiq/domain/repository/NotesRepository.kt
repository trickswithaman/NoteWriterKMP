package com.notiq.notiq.domain.repository

import com.notiq.db.NoteEntity


interface NotesRepository {

    suspend fun getNotes(): List<NoteEntity>

    suspend fun updateNote(note: NoteEntity)

    suspend fun insertNote(note: NoteEntity)

    suspend fun deleteNote(id: String)
}