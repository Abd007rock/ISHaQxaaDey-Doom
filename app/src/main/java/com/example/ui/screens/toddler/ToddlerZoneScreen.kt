package com.example.ui.screens.toddler

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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

data class AbcCard(val letter: String, val word: String, val emoji: String, val color: Color)
data class AnimalSound(val name: String, val emoji: String, val sound: String, val phrase: String)
data class StoryRhyme(val title: String, val preview: String, val fullText: String, val icon: String)

@Composable
fun ToddlerZoneScreen(
    onSpeak: (String) -> Unit
) {
    var activeTab by remember { mutableStateOf("ABC") }
    val tabs = listOf("ABC", "123 Numbers", "Animal Sounds", "Colors & Shapes", "Rhymes & Stories", "Tracing")

    val abcCards = remember {
        listOf(
            AbcCard("A", "Apple", "🍎", Color(0xFFFF7675)),
            AbcCard("B", "Bear", "🐻", Color(0xFFFECA57)),
            AbcCard("C", "Cat", "🐱", Color(0xFF54A0FF)),
            AbcCard("D", "Dolphin", "🐬", Color(0xFF00D2D3)),
            AbcCard("E", "Elephant", "🐘", Color(0xFF9B59B6)),
            AbcCard("F", "Fish", "🐠", Color(0xFFFF9FF3)),
            AbcCard("G", "Giraffe", "🦒", Color(0xFF10B981)),
            AbcCard("H", "Heart", "💖", Color(0xFFFF6B6B)),
            AbcCard("I", "Ice Cream", "🍦", Color(0xFFFECA57)),
            AbcCard("J", "Jellyfish", "🪼", Color(0xFF54A0FF)),
            AbcCard("K", "Kangaroo", "🦘", Color(0xFFE67E22)),
            AbcCard("L", "Lion", "🦁", Color(0xFFF39C12))
        )
    }

    val animalSounds = remember {
        listOf(
            AnimalSound("Lion", "🦁", "Roaaar!", "The brave lion roars loudly in the savanna!"),
            AnimalSound("Cat", "🐱", "Meow Meow!", "The soft kitten purrs and says meow!"),
            AnimalSound("Dog", "🐶", "Woof Woof!", "The happy puppy wags its tail and barks woof!"),
            AnimalSound("Cow", "🐮", "Moooo!", "The gentle cow grazes in the field and says moo!"),
            AnimalSound("Duck", "🦆", "Quack Quack!", "The yellow duck swims in the pond saying quack!"),
            AnimalSound("Sheep", "🐑", "Baaa Baaa!", "The fluffy sheep gives warm wool and says baa!"),
            AnimalSound("Elephant", "🐘", "Pawoo!", "The big elephant trumpets with its long trunk!"),
            AnimalSound("Frog", "🐸", "Ribbit Ribbit!", "The green frog leaps on lily pads saying ribbit!")
        )
    }

    val rhymes = remember {
        listOf(
            StoryRhyme(
                "Twinkle Twinkle Little Star",
                "How I wonder what you are...",
                "Twinkle, twinkle, little star,\nHow I wonder what you are!\nUp above the world so high,\nLike a diamond in the sky.\nTwinkle, twinkle, little star,\nHow I wonder what you are!",
                "⭐"
            ),
            StoryRhyme(
                "Old MacDonald Had a Farm",
                "E-I-E-I-O with a duck on his farm!",
                "Old MacDonald had a farm, E-I-E-I-O!\nAnd on that farm he had a cow, E-I-E-I-O!\nWith a moo-moo here, and a moo-moo there,\nHere a moo, there a moo, everywhere a moo-moo!\nOld MacDonald had a farm, E-I-E-I-O!",
                "🚜"
            ),
            StoryRhyme(
                "The Wise Little Ant",
                "Working together with love and patience.",
                "Once upon a sunny day,\nA little ant went out to play.\nHe found a seed so round and sweet,\nA joyful summer treat to eat!\nHe called his friends to share the prize,\nTrue happiness is kindness in our eyes.",
                "🐜"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Toddler Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFFFECA57), Color(0xFFFF9F43), Color(0xFFFF7675))
                    )
                )
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Toddler Learning Zone 🧸",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Ages 2-6 • Big Buttons • Audio Voice • Fun & Easy",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.95f)
                    )
                }
                Text("🎈", fontSize = 36.sp)
            }
        }

        // Subcategory Pills
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tabs) { tab ->
                val isSelected = activeTab == tab
                FilterChip(
                    selected = isSelected,
                    onClick = { activeTab = tab },
                    label = { Text(tab, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Content Area based on selected Tab
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            when (activeTab) {
                "ABC" -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(abcCards) { card ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(24.dp))
                                    .clickable {
                                        onSpeak("${card.letter} is for ${card.word}")
                                    }
                                    .testTag("toddler_abc_${card.letter}"),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = card.color.copy(alpha = 0.15f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(card.emoji, fontSize = 48.sp)
                                    Spacer(Modifier.height(4.dp))
                                    Text(card.letter, fontSize = 32.sp, fontWeight = FontWeight.Black, color = card.color)
                                    Text(card.word, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    Spacer(Modifier.height(4.dp))
                                    Icon(Icons.Filled.VolumeUp, contentDescription = "Listen", tint = card.color, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }

                "123 Numbers" -> {
                    val numbers = (1..10).toList()
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(numbers) { num ->
                            val stars = "⭐".repeat(num.coerceAtMost(5))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSpeak("Number $num")
                                    },
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("$num", fontSize = 40.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                    Text(stars, fontSize = 16.sp)
                                    Spacer(Modifier.height(4.dp))
                                    Text("Count $num", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                "Animal Sounds" -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(animalSounds) { animal ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSpeak("${animal.name} says ${animal.sound}. ${animal.phrase}")
                                    },
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(animal.emoji, fontSize = 48.sp)
                                    Spacer(Modifier.height(4.dp))
                                    Text(animal.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            animal.sound,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                "Colors & Shapes" -> {
                    val colorsList = listOf(
                        "Red" to Color(0xFFFF6B6B),
                        "Blue" to Color(0xFF54A0FF),
                        "Green" to Color(0xFF10B981),
                        "Yellow" to Color(0xFFFECA57),
                        "Purple" to Color(0xFF9B59B6),
                        "Orange" to Color(0xFFFF9F43)
                    )
                    Column {
                        Text("Tap any color to hear its name:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(Modifier.height(12.dp))
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(colorsList) { (name, color) ->
                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(color)
                                        .clickable { onSpeak("Color $name") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(name, fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }

                "Rhymes & Stories" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        rhymes.forEach { rhyme ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSpeak("${rhyme.title}. ${rhyme.fullText}") },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(rhyme.icon, fontSize = 36.sp)
                                    Spacer(Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(rhyme.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text(rhyme.preview, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    FilledIconButton(onClick = { onSpeak("${rhyme.title}. ${rhyme.fullText}") }) {
                                        Icon(Icons.Filled.PlayArrow, contentDescription = "Play")
                                    }
                                }
                            }
                        }
                    }
                }

                "Tracing" -> {
                    ToddlerTracingCanvas(onSpeak = onSpeak)
                }
            }
        }
    }
}

@Composable
fun ToddlerTracingCanvas(onSpeak: (String) -> Unit) {
    var traceLetter by remember { mutableStateOf("A") }
    val letters = listOf("A", "B", "C", "1", "2", "3")
    val currentPoints = remember { mutableStateListOf<Offset>() }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            letters.forEach { l ->
                FilterChip(
                    selected = traceLetter == l,
                    onClick = {
                        traceLetter = l
                        currentPoints.clear()
                        onSpeak("Trace letter $l")
                    },
                    label = { Text(l, fontWeight = FontWeight.Bold) }
                )
            }
            IconButton(onClick = { currentPoints.clear() }) {
                Icon(Icons.Filled.Delete, contentDescription = "Clear")
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset -> currentPoints.add(offset) },
                        onDrag = { change, _ ->
                            change.consume()
                            currentPoints.add(change.position)
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // Guide Letter Outline in Light Grey
            Text(
                text = traceLetter,
                fontSize = 200.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFE2E8F0)
            )

            // Toddler Finger Tracing Stroke
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (currentPoints.size > 1) {
                    val path = Path()
                    path.moveTo(currentPoints[0].x, currentPoints[0].y)
                    for (i in 1 until currentPoints.size) {
                        path.lineTo(currentPoints[i].x, currentPoints[i].y)
                    }
                    drawPath(
                        path = path,
                        color = Color(0xFF6C5CE7),
                        style = Stroke(width = 30f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }
            }
        }

        Text(
            "Use your finger to trace $traceLetter!",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}
