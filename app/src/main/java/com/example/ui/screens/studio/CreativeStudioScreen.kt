package com.example.ui.screens.studio

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ArtworkEntity

data class DrawnStroke(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float
)

data class PlacedSticker(
    val emoji: String,
    val offset: Offset
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreativeStudioScreen(
    onSaveArtwork: (ArtworkEntity) -> Unit,
    onShareToFeed: (title: String, author: String) -> Unit
) {
    val strokes = remember { mutableStateListOf<DrawnStroke>() }
    val stickers = remember { mutableStateListOf<PlacedSticker>() }
    var currentPoints = remember { mutableStateListOf<Offset>() }

    var selectedColor by remember { mutableStateOf(Color(0xFF6C5CE7)) }
    var brushWidth by remember { mutableFloatStateOf(16f) }
    var selectedSticker by remember { mutableStateOf<String?>(null) }
    var artworkTitle by remember { mutableStateOf("My Colorful Masterpiece") }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showChallengeDialog by remember { mutableStateOf(false) }

    val palette = listOf(
        Color(0xFF6C5CE7), // Indigo
        Color(0xFF54A0FF), // Sky Blue
        Color(0xFF10B981), // Emerald
        Color(0xFFFECA57), // Golden
        Color(0xFFFF9F43), // Orange
        Color(0xFFFF6B6B), // Coral
        Color(0xFFFF9FF3), // Pink
        Color(0xFF34495E), // Slate
        Color(0xFFFFFFFF)  // Eraser
    )

    val availableStickers = listOf("⭐", "💖", "🌈", "👑", "🎈", "🚀", "☀️", "🦋", "🦁", "🌸")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Studio Top Bar
        TopAppBar(
            title = {
                Column {
                    Text("Creative Studio 🎨", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Draw, Color, and Place Stickers", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            actions = {
                // Creative Challenge button
                IconButton(onClick = { showChallengeDialog = true }) {
                    Icon(Icons.Filled.EmojiEvents, contentDescription = "Daily Art Challenge", tint = Color(0xFFFECA57))
                }
                // Clear Canvas button
                IconButton(onClick = {
                    strokes.clear()
                    stickers.clear()
                    currentPoints.clear()
                }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Clear")
                }
                // Save & Share button
                FilledTonalButton(
                    onClick = { showSaveDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.padding(end = 8.dp).testTag("save_art_btn")
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Save", fontSize = 12.sp)
                }
            }
        )

        // Color Palette Selector
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(palette) { color ->
                val isSelected = selectedColor == color && selectedSticker == null
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFCBD5E1),
                            shape = CircleShape
                        )
                        .clickable {
                            selectedColor = color
                            selectedSticker = null
                        }
                )
            }

            item {
                Spacer(Modifier.width(8.dp))
                // Brush sizes
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(8f to "S", 18f to "M", 32f to "L").forEach { (width, label) ->
                        FilterChip(
                            selected = brushWidth == width,
                            onClick = { brushWidth = width },
                            label = { Text(label, fontSize = 11.sp) },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        // Stickers Bar
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Box(
                    modifier = Modifier.padding(end = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        "Stickers:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            items(availableStickers) { sticker ->
                val isSelected = selectedSticker == sticker
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable {
                        selectedSticker = if (isSelected) null else sticker
                    }
                ) {
                    Text(sticker, fontSize = 22.sp, modifier = Modifier.padding(6.dp))
                }
            }
        }

        // Main Drawing Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .border(2.dp, Color(0xFFE2E8F0), RoundedCornerShape(24.dp))
                .pointerInput(selectedSticker, selectedColor, brushWidth) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            if (selectedSticker != null) {
                                stickers.add(PlacedSticker(selectedSticker!!, offset))
                            } else {
                                currentPoints.add(offset)
                            }
                        },
                        onDrag = { change, _ ->
                            if (selectedSticker == null) {
                                change.consume()
                                currentPoints.add(change.position)
                            }
                        },
                        onDragEnd = {
                            if (selectedSticker == null && currentPoints.isNotEmpty()) {
                                strokes.add(DrawnStroke(currentPoints.toList(), selectedColor, brushWidth))
                                currentPoints.clear()
                            }
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Completed strokes
                strokes.forEach { stroke ->
                    if (stroke.points.size > 1) {
                        val path = Path()
                        path.moveTo(stroke.points[0].x, stroke.points[0].y)
                        for (i in 1 until stroke.points.size) {
                            path.lineTo(stroke.points[i].x, stroke.points[i].y)
                        }
                        drawPath(
                            path = path,
                            color = stroke.color,
                            style = Stroke(width = stroke.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                }

                // Current actively drawn stroke
                if (currentPoints.size > 1) {
                    val path = Path()
                    path.moveTo(currentPoints[0].x, currentPoints[0].y)
                    for (i in 1 until currentPoints.size) {
                        path.lineTo(currentPoints[i].x, currentPoints[i].y)
                    }
                    drawPath(
                        path = path,
                        color = selectedColor,
                        style = Stroke(width = brushWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }
            }

            // Placed Stickers
            stickers.forEach { sticker ->
                Text(
                    text = sticker.emoji,
                    fontSize = 36.sp,
                    modifier = Modifier.offset(
                        x = (sticker.offset.x / 2.5f).dp,
                        y = (sticker.offset.y / 2.5f).dp
                    )
                )
            }
        }
    }

    // Save & Share Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save & Share Your Art 🎨", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Give your artwork a name:")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = artworkTitle,
                        onValueChange = { artworkTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val art = ArtworkEntity(
                            title = artworkTitle.ifBlank { "Untitled Art" },
                            authorName = "Ayaan & Safiya",
                            pathPointsJson = "strokes:${strokes.size},stickers:${stickers.size}",
                            stickerNames = stickers.joinToString(",") { it.emoji }
                        )
                        onSaveArtwork(art)
                        onShareToFeed(art.title, "Ayaan & Safiya")
                        showSaveDialog = false
                    },
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Save & Post to Feed")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Daily Art Challenge Dialog
    if (showChallengeDialog) {
        AlertDialog(
            onDismissRequest = { showChallengeDialog = false },
            title = { Text("Today's Art Challenge 🏆", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("🚀 Draw a Magical Spaceship visiting a Rainbow Planet!", fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    Text("Use the Rocket & Rainbow stickers to earn 50 bonus XP and the 'Galaxy Creator' badge!", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                }
            },
            confirmButton = {
                Button(onClick = { showChallengeDialog = false }) {
                    Text("Accept Challenge!")
                }
            }
        )
    }
}
