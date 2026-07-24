package com.notiq.notiq.notiq.presentation.NoteLIstScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.notiq.db.NoteEntity
import com.notiq.notiq.domain.model.NoteWithImages
import com.notiq.notiq.notiq.components.formatDate
import com.notiq.notiq.notiq.navigation.PhotoItem
import com.notiq.notiq.notiq.util.renderMarkdown
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult


@Composable
fun NoteItem(
    noteWithImages: NoteWithImages, onEditNote: () -> Unit, onDeleteNote: () -> Unit, isGridView: Boolean
) {
    val note = noteWithImages.note
    val images = noteWithImages.images

    val renderedTitle = remember(note.title) {
        renderMarkdown(note.title ?: "")
    }
    val renderedContent = remember(note.content) {
        renderMarkdown(note.content ?: "")
    }
    val formattedDate = remember(note.createdAt) {
        formatDate(note.createdAt)
    }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onEditNote),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (note.isPinned == true) MaterialTheme.colorScheme.primaryContainer.copy(
                alpha = 0.7f
            )
            else MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (note.isPinned == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(
                    alpha = 0.5f
                )
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            // Using the new relational NoteWithImages model to display a preview list of images.
            if (images.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))


                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isGridView) 100.dp else 150.dp),
                    verticalItemSpacing = 4.dp,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(images) { imageEntity ->
                        PhotoItem(
                            photo = PhotoResult(uri = imageEntity.uri),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = renderedTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (note.isPinned == true) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (note.isPinned == true) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }



            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = renderedContent,
                style = MaterialTheme.typography.bodyMedium,
                color = if (note.isPinned == true) MaterialTheme.colorScheme.onPrimaryContainer.copy(
                    alpha = 0.8f
                ) else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = if (isGridView) 4 else 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                IconButton(
                    onClick = onDeleteNote, modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
