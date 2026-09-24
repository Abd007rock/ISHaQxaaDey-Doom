package com.example.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

data class FallingItem(
    val id: Int,
    val emoji: String,
    val points: Int,
    val isHazard: Boolean,
    val xRatio: Float,
    var yRatio: Float = 0f
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FruitCatcherGame(
    onBack: () -> Unit,
    onScoreSaved: (score: Int, stars: Int) -> Unit
) {
    var basketXRatio by remember { mutableFloatStateOf(0.5f) }
    var score by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var isGameOver by remember { mutableStateOf(false) }
    var items by remember { mutableStateOf(listOf<FallingItem>()) }

    // Game tick
    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            var itemCounter = 0
            while (lives > 0) {
                delay(70L)
                // Spawn new item
                if (Random.nextInt(18) == 0 && items.size < 6) {
                    val isHazard = Random.nextInt(4) == 0
                    val (emoji, points) = if (isHazard) {
                        "🪨" to -1
                    } else {
                        when (Random.nextInt(4)) {
                            0 -> "🍎" to 10
                            1 -> "🍌" to 15
                            2 -> "🍓" to 20
                            else -> "🍉" to 25
                        }
                    }
                    items = items + FallingItem(
                        id = itemCounter++,
                        emoji = emoji,
                        points = points,
                        isHazard = isHazard,
                        xRatio = Random.nextFloat() * 0.8f + 0.1f,
                        yRatio = 0f
                    )
                }

                // Move items down and check catch
                val updated = mutableListOf<FallingItem>()
                for (item in items) {
                    val newY = item.yRatio + 0.025f
                    if (newY >= 0.85f && newY <= 0.95f) {
                        // Check collision with basket
                        val dist = kotlin.math.abs(item.xRatio - basketXRatio)
                        if (dist < 0.14f) {
                            if (item.isHazard) {
                                lives--
                            } else {
                                score += item.points
                            }
                            continue // caught!
                        }
                    }

                    if (newY < 1.0f) {
                        updated.add(item.copy(yRatio = newY))
                    } else {
                        // Missed fruit doesn't penalize, just leaves screen
                    }
                }
                items = updated

                if (lives <= 0) {
                    isGameOver = true
                    val stars = if (score >= 120) 3 else if (score >= 60) 2 else 1
                    onScoreSaved(score, stars)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fruit Catcher 🍎🧺", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 12.dp)) {
                        Row {
                            repeat(lives) {
                                Icon(Icons.Filled.Favorite, contentDescription = "Life", tint = Color(0xFFFF6B6B), modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFECA57))
                        Spacer(Modifier.width(4.dp))
                        Text("$score", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(listOf(Color(0xFFFEF3C7), Color(0xFFE0F2FE), Color(0xFFDCFCE7)))
                )
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val newRatio = (basketXRatio + dragAmount.x / 1000f).coerceIn(0.1f, 0.9f)
                        basketXRatio = newRatio
                    }
                }
        ) {
            // Instructions banner
            Text(
                text = "👈 Drag or tap buttons below to catch fruit! Avoid rocks! 👉",
                fontSize = 12.sp,
                color = Color(0xFF475569),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
            )

            // Falling Items
            items.forEach { item ->
                Text(
                    text = item.emoji,
                    fontSize = 34.sp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(
                            x = (item.xRatio * 320).dp,
                            y = (item.yRatio * 520).dp
                        )
                )
            }

            // Basket at Bottom
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (basketXRatio * 320 - 30).dp, y = (-70).dp)
            ) {
                Text("🧺", fontSize = 48.sp)
                Text("Catcher", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }

            // Left / Right touch controls for ease of play
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { basketXRatio = (basketXRatio - 0.12f).coerceAtLeast(0.1f) },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("⬅️ Move Left", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { basketXRatio = (basketXRatio + 0.12f).coerceAtMost(0.9f) },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Move Right ➡️", fontWeight = FontWeight.Bold)
                }
            }

            // Game Over Dialog
            if (isGameOver) {
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🍎 Great Catch! 🧺", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(Modifier.height(8.dp))
                        Text("You gathered fresh juicy fruits!")
                        Spacer(Modifier.height(12.dp))
                        Text("Score: $score", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(onClick = onBack, shape = RoundedCornerShape(16.dp)) {
                                Text("Back")
                            }
                            Button(
                                onClick = {
                                    score = 0
                                    lives = 3
                                    items = emptyList()
                                    isGameOver = false
                                },
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Play Again")
                            }
                        }
                    }
                }
            }
        }
    }
}
