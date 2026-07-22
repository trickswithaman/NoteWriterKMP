package com.notiq.notiq.data.mapper

import com.notiq.db.NoteEntity


fun NoteEntity.toDomain(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        imagePath = imagePath,
        isPinned = isPinned,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}