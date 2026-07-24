package com.notiq.notiq.domain.model

import com.notiq.db.NoteEntity
import com.notiq.db.NoteImageEntity

/**
 * A wrapper class that represents a Note along with all its associated images.
 * This follows the relational structure defined in our database.
 */
data class NoteWithImages(
    val note: NoteEntity,
    val images: List<NoteImageEntity>
)
