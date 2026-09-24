package com.example.ui.screens.poems

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PoemEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoemsScreen(
    poems: List<PoemEntity>,
    onToggleFavorite: (poemId: Long) -> Unit,
    onRecordRead: (poemId: Long) -> Unit,
    onSpeak: (String) -> Unit,
    onStopSpeak: () -> Unit
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var readingPoem by remember { mutableStateOf<PoemEntity?>(null) }
    var isNarrating by remember { mutableStateOf(false) }

    val categories = listOf("All", "Favorites", "Friendship", "Nature", "Educational", "Bedtime", "Children's", "Islamic", "Inspirational")

    val filteredPoems = remember(selectedCategory, searchQuery, poems) {
        poems.filter { poem ->
            val matchesCategory = when (selectedCategory) {
                "All" -> true
                "Favorites" -> poem.isFavorite
                else -> poem.category.equals(selectedCategory, ignoreCase = true)
            }
            val matchesSearch = poem.title.contains(searchQuery, ignoreCase = true) ||
                    poem.content.contains(searchQuery, ignoreCase = true) ||
                    poem.author.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF8B5CF6), Color(0xFF6C5CE7), Color(0xFF3B82F6))
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Poem & Rhyme Library 📖",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Listen, Read, and Learn Heartfelt Verses • Offline Ready",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search poems, poets, themes...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear")
                    }
                }
            },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("poem_search_input")
        )

        // Category Pills
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat, fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal) },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Poems List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filteredPoems, key = { it.id }) { poem ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .clickable {
                            readingPoem = poem
                            onRecordRead(poem.id)
                        }
                        .testTag("poem_item_${poem.id}"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    poem.category,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            IconButton(onClick = { onToggleFavorite(poem.id) }) {
                                Icon(
                                    if (poem.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (poem.isFavorite) Color(0xFFFF6B6B) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = poem.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "By ${poem.author}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = poem.content,
                            fontSize = 13.sp,
                            maxLines = 3,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    readingPoem = poem
                                    onRecordRead(poem.id)
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Filled.Book, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Read & Listen", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Full Reader Mode
    readingPoem?.let { poem ->
        AlertDialog(
            onDismissRequest = {
                onStopSpeak()
                isNarrating = false
                readingPoem = null
            },
            modifier = Modifier.fillMaxWidth(0.95f),
            shape = RoundedCornerShape(28.dp),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(poem.title, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        Text("Category: ${poem.category} • ${poem.author}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = poem.content,
                            fontSize = 17.sp,
                            lineHeight = 26.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(20.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Narration Play / Stop Button
                        Button(
                            onClick = {
                                if (isNarrating) {
                                    onStopSpeak()
                                    isNarrating = false
                                } else {
                                    onSpeak("${poem.title}. A poem by ${poem.author}. ${poem.content}")
                                    isNarrating = true
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isNarrating) Color(0xFFFF6B6B) else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(if (isNarrating) Icons.Filled.Stop else Icons.Filled.VolumeUp, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text(if (isNarrating) "Stop Audio" else "Audio Narration")
                        }

                        // Share Poem Intent
                        OutlinedButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, poem.title)
                                    putExtra(Intent.EXTRA_TEXT, "📖 '${poem.title}' on IshaQxaaDey Doom:\n\n${poem.content}\n\nPlay, Learn, Create, and Grow Together!")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Poem"))
                            },
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Share")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onStopSpeak()
                        isNarrating = false
                        readingPoem = null
                    }
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
