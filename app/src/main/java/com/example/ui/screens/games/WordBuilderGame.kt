package com.example.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class WordPuzzle(
    val word: String,
    val emoji: String,
    val clue: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordBuilderGame(
    onBack: () -> Unit,
    onSpeak: (String) -> Unit,
    onScoreSaved: (score: Int, stars: Int) -> Unit
) {
    val puzzles = remember {
        listOf(
            WordPuzzle("STAR", "⭐", "Shines bright in the night sky"),
            WordPuzzle("LION", "🦁", "The brave king of the jungle"),
            WordPuzzle("BIRD", "🐦", "Sings sweet songs on trees"),
            WordPuzzle("FISH", "🐠", "Swims happily in the ocean"),
            WordPuzzle("MOON", "🌙", "Glows gently while we sleep"),
            WordPuzzle("TREE", "🌳", "Has green leaves and provides shade")
        )
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var currentPuzzle by remember { mutableStateOf(puzzles[0]) }
    var assembledLetters by remember { mutableStateOf(listOf<Char>()) }
    var availableTiles by remember { mutableStateOf(currentPuzzle.word.toList().shuffled()) }
    var score by remember { mutableIntStateOf(0) }
    var isCompleted by remember { mutableStateOf(false) }

    fun loadPuzzle(index: Int) {
        val p = puzzles[index]
        currentPuzzle = p
        assembledLetters = emptyList()
        availableTiles = p.word.toList().shuffled()
        onSpeak(p.word)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Word Builder 🔤", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 16.dp)) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFECA57))
                        Spacer(Modifier.width(4.dp))
                        Text("$score", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(Color(0xFFFEF3C7), Color(0xFFEDE9FE))))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (!isCompleted) {
                // Word Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(currentPuzzle.emoji, fontSize = 64.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(currentPuzzle.clue, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        IconButton(onClick = { onSpeak(currentPuzzle.word) }) {
                            Icon(Icons.Filled.VolumeUp, contentDescription = "Listen to word", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                // Assembled Slots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(currentPuzzle.word.length) { index ->
                        val char = assembledLetters.getOrNull(index)
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (char != null) MaterialTheme.colorScheme.primaryContainer else Color.White)
                                .clickable(enabled = char != null) {
                                    if (char != null) {
                                        assembledLetters = assembledLetters.toMutableList().also { it.removeAt(index) }
                                        availableTiles = availableTiles + char
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = char?.toString() ?: "_",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Available Scrambled Letter Tiles
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Tap letters to build word:", fontSize = 13.sp, color = Color(0xFF64748B))
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        availableTiles.forEachIndexed { tileIdx, char ->
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                                    .clickable {
                                        val newAssembled = assembledLetters + char
                                        assembledLetters = newAssembled
                                        availableTiles = availableTiles.toMutableList().also { it.removeAt(tileIdx) }

                                        // Check if completed word
                                        val built = newAssembled.joinToString("")
                                        if (built == currentPuzzle.word) {
                                            score += 25
                                            onSpeak("Correct! ${currentPuzzle.word}")
                                            if (currentIndex + 1 < puzzles.size) {
                                                currentIndex++
                                                loadPuzzle(currentIndex)
                                            } else {
                                                isCompleted = true
                                                onScoreSaved(score, 3)
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = char.toString(),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.padding(24.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🌟 Spelling Superstar! 🌟", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(Modifier.height(8.dp))
                        Text("You spelled all words correctly!")
                        Spacer(Modifier.height(12.dp))
                        Text("Score: $score", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onBack, shape = RoundedCornerShape(16.dp)) {
                            Text("Back to Games")
                        }
                    }
                }
            }

            Text("Word ${currentIndex + 1} of ${puzzles.size}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}
