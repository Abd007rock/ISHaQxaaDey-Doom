package com.example.ui.screens.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

data class Bubble(
    val id: Int,
    val text: String,
    val color: Color,
    val xOffset: Float, // 0.1 to 0.85
    val speed: Float,
    var isPopped: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BubblePopGame(
    onBack: () -> Unit,
    onScoreSaved: (score: Int, stars: Int) -> Unit
) {
    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(35) }
    var isGameOver by remember { mutableStateOf(false) }
    var bubbles by remember { mutableStateOf(listOf<Bubble>()) }

    val bubbleColors = listOf(
        Color(0xFFFF7675),
        Color(0xFF54A0FF),
        Color(0xFFFECA57),
        Color(0xFF10B981),
        Color(0xFF9B59B6),
        Color(0xFFFF9FF3)
    )
    val symbols = listOf("A", "B", "C", "1", "2", "3", "⭐", "🎈", "💎", "🌟")

    // Game loop timer
    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            }
            isGameOver = true
            val starsEarned = when {
                score >= 80 -> 3
                score >= 40 -> 2
                else -> 1
            }
            onScoreSaved(score, starsEarned)
        }
    }

    // Spawn bubbles
    LaunchedEffect(isGameOver) {
        var bubbleCounter = 0
        while (!isGameOver) {
            delay(700L)
            if (bubbles.size < 12) {
                val newBubble = Bubble(
                    id = bubbleCounter++,
                    text = symbols.random(),
                    color = bubbleColors.random(),
                    xOffset = Random.nextFloat() * 0.75f + 0.05f,
                    speed = Random.nextFloat() * 0.005f + 0.008f
                )
                bubbles = bubbles + newBubble
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bubble Pop Adventure 🎈", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Icon(Icons.Filled.Star, contentDescription = "Score", tint = Color(0xFFFECA57))
                        Spacer(Modifier.width(4.dp))
                        Text("$score", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        Spacer(Modifier.width(16.dp))
                        Text("⏳ ${timeLeft}s", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                    Brush.verticalGradient(
                        listOf(Color(0xFFE0F2FE), Color(0xFFF0FDF4), Color(0xFFFFF7ED))
                    )
                )
        ) {
            // Active Bubbles
            bubbles.filter { !it.isPopped }.forEach { bubble ->
                KeyframeBubble(
                    bubble = bubble,
                    onPop = {
                        score += 10
                        bubble.isPopped = true
                        bubbles = bubbles.filter { it.id != bubble.id }
                    }
                )
            }

            // Game Over Dialog / Overlay
            if (isGameOver) {
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                        .testTag("game_over_card"),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎉 Fantastic Job! 🎉", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(Modifier.height(12.dp))
                        Text("You popped so many bubbles!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Text("Final Score: $score", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        Row {
                            repeat(if (score >= 80) 3 else if (score >= 40) 2 else 1) {
                                Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFECA57), modifier = Modifier.size(36.dp))
                            }
                        }
                        Spacer(Modifier.height(20.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = onBack,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.testTag("return_games_btn")
                            ) {
                                Text("Back to Games")
                            }
                            Button(
                                onClick = {
                                    score = 0
                                    timeLeft = 35
                                    bubbles = emptyList()
                                    isGameOver = false
                                },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.testTag("play_again_btn")
                            ) {
                                Icon(Icons.Filled.Refresh, contentDescription = null)
                                Spacer(Modifier.width(6.dp))
                                Text("Play Again")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BoxScope.KeyframeBubble(
    bubble: Bubble,
    onPop: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bubble_float")
    val yProgress by infiniteTransition.animateFloat(
        initialValue = 1.05f,
        targetValue = -0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "y_pos"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth(0.24f)
            .aspectRatio(1f)
            .align(Alignment.TopStart)
            .offset(
                x = (bubble.xOffset * 320).dp,
                y = (yProgress * 650).dp
            )
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(bubble.color.copy(alpha = 0.85f), bubble.color.copy(alpha = 0.5f))
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onPop()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = bubble.text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
    }
}
