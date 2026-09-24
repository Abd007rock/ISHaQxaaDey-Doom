package com.example.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
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

data class GameShape(
    val name: String,
    val emoji: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShapeMatchGame(
    onBack: () -> Unit,
    onScoreSaved: (score: Int, stars: Int) -> Unit
) {
    val allShapes = remember {
        listOf(
            GameShape("Star", "⭐", Color(0xFFFECA57)),
            GameShape("Heart", "❤️", Color(0xFFFF6B6B)),
            GameShape("Circle", "🔵", Color(0xFF54A0FF)),
            GameShape("Square", "🟩", Color(0xFF10B981)),
            GameShape("Diamond", "💎", Color(0xFF00D2D3)),
            GameShape("Sun", "☀️", Color(0xFFFF9F43))
        )
    }

    var targetShape by remember { mutableStateOf(allShapes.random()) }
    var currentChoices by remember { mutableStateOf(allShapes.shuffled().take(4)) }
    var score by remember { mutableIntStateOf(0) }
    var matchesCount by remember { mutableIntStateOf(0) }
    var isWon by remember { mutableStateOf(false) }

    fun nextRound() {
        if (matchesCount >= 8) {
            isWon = true
            onScoreSaved(score, 3)
        } else {
            val newTarget = allShapes.random()
            targetShape = newTarget
            val others = allShapes.filter { it.name != newTarget.name }.shuffled().take(3)
            currentChoices = (others + newTarget).shuffled()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shape Matching 🔶⭐", fontWeight = FontWeight.Bold) },
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
                .background(Brush.verticalGradient(listOf(Color(0xFFE0F2FE), Color(0xFFF0FDF4))))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Match the target shape! Tap the matching one below.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!isWon) {
                // Target Display
                Card(
                    modifier = Modifier.padding(vertical = 16.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Find the:", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = targetShape.name,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .border(4.dp, targetShape.color, CircleShape)
                                .background(targetShape.color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(targetShape.emoji, fontSize = 54.sp)
                        }
                    }
                }

                // Choices Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    currentChoices.take(2).forEach { shape ->
                        ShapeButton(shape = shape) {
                            if (shape.name == targetShape.name) {
                                score += 15
                                matchesCount++
                                nextRound()
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    currentChoices.drop(2).take(2).forEach { shape ->
                        ShapeButton(shape = shape) {
                            if (shape.name == targetShape.name) {
                                score += 15
                                matchesCount++
                                nextRound()
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
                        Text("🎉 Shapes Champion! 🎉", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(Modifier.height(8.dp))
                        Text("You recognized all the shapes perfectly!")
                        Spacer(Modifier.height(12.dp))
                        Text("Score: $score", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onBack, shape = RoundedCornerShape(16.dp)) {
                            Text("Back to Games")
                        }
                    }
                }
            }

            Text("Matches: $matchesCount / 8", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun ShapeButton(shape: GameShape, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(shape.emoji, fontSize = 48.sp)
        }
    }
}
