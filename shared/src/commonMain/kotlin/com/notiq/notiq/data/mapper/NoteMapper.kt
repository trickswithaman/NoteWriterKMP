package com.notiq.notiq.data.mapper

import com.notiq.db.NoteEntity


/**
 * Maps a NoteEntity from the database layer to the domain layer.
 * Note: For full multi-image support, use NoteWithImages to include relational NoteImageEntity data.
 */
fun NoteEntity.toDomain(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        // We keep imagePath as a preview URI (usually the first image) for quick rendering in lists.
        imagePath = imagePath,
        isPinned = isPinned,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
