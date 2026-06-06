package com.notewriterkmp.notiq.data.mapper

import com.notewriterkmp.db.NoteEntity


fun NoteEntity.toDomain(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        isPinned = isPinned,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}