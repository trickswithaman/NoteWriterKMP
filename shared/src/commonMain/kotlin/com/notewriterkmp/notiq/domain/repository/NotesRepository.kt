package com.notewriterkmp.notiq.domain.repository

import com.notewriterkmp.db.NoteEntity


interface NotesRepository {

    suspend fun getNotes(): List<NoteEntity>

    suspend fun updateNote(note: NoteEntity)

    suspend fun insertNote(note: NoteEntity)

    suspend fun deleteNote(id: String)
}