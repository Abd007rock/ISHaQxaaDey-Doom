package com.example.ui.screens.games

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class MemoryCard(
    val id: Int,
    val icon: String,
    val isMatched: Boolean = false,
    val isFaceUp: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryMatchGame(
    onBack: () -> Unit,
    onScoreSaved: (score: Int, stars: Int) -> Unit
) {
    val animalIcons = remember { listOf("🦁", "🐼", "🦊", "🐰", "🐸", "🐧") }

    fun generateDeck(): List<MemoryCard> {
        val pairs = (animalIcons + animalIcons).shuffled()
        return pairs.mapIndexed { index, icon ->
            MemoryCard(id = index, icon = icon)
        }
    }

    var cards by remember { mutableStateOf(generateDeck()) }
    var selectedIndices by remember { mutableStateOf(listOf<Int>()) }
    var moves by remember { mutableIntStateOf(0) }
    var isChecking by remember { mutableStateOf(false) }

    val isWon = cards.all { it.isMatched }

    LaunchedEffect(isWon) {
        if (isWon && cards.isNotEmpty()) {
            val score = (100 - moves * 3).coerceAtLeast(30)
            val stars = if (moves <= 10) 3 else if (moves <= 16) 2 else 1
            onScoreSaved(score, stars)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Memory Match 🧩", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 16.dp)) {
                        Text("Moves: $moves", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(Color(0xFFEDE9FE), Color(0xFFE0E7FF), Color(0xFFF8FAFC))))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Find all animal pairs! Tap cards to flip them.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(cards) { index, card ->
                    val isRevealed = card.isFaceUp || card.isMatched
                    val rotation by animateFloatAsState(
                        targetValue = if (isRevealed) 180f else 0f,
                        label = "card_flip"
                    )

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .graphicsLayer {
                                rotationY = rotation
                                cameraDistance = 8 * density
                            }
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (card.isMatched) Color(0xFFD1FAE5)
                                else if (isRevealed) Color(0xFFFEF3C7)
                                else MaterialTheme.colorScheme.primary
                            )
                            .clickable(enabled = !isChecking && !card.isMatched && !card.isFaceUp) {
                                val newSelected = selectedIndices + index
                                cards = cards.mapIndexed { i, c ->
                                    if (i == index) c.copy(isFaceUp = true) else c
                                }
                                selectedIndices = newSelected

                                if (newSelected.size == 2) {
                                    moves++
                                    isChecking = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (rotation > 90f) {
                            Text(
                                text = card.icon,
                                fontSize = 38.sp,
                                modifier = Modifier.graphicsLayer { rotationY = 180f }
                            )
                        } else {
                            Text("❓", fontSize = 28.sp)
                        }
                    }
                }
            }

            // Handle match logic after short delay
            LaunchedEffect(selectedIndices) {
                if (selectedIndices.size == 2) {
                    delay(800L)
                    val first = selectedIndices[0]
                    val second = selectedIndices[1]
                    if (cards[first].icon == cards[second].icon) {
                        cards = cards.mapIndexed { i, c ->
                            if (i == first || i == second) c.copy(isMatched = true, isFaceUp = true) else c
                        }
                    } else {
                        cards = cards.mapIndexed { i, c ->
                            if (i == first || i == second) c.copy(isFaceUp = false) else c
                        }
                    }
                    selectedIndices = emptyList()
                    isChecking = false
                }
            }

            // Victory state
            if (isWon) {
                Card(
                    modifier = Modifier.padding(vertical = 12.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🌟 Memory Master! 🌟", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Completed in $moves moves!", color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = {
                                cards = generateDeck()
                                moves = 0
                                selectedIndices = emptyList()
                            },
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Play Another Round")
                        }
                    }
                }
            }
        }
    }
}
