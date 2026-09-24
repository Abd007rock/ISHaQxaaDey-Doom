package com.example.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

data class MathQuestion(
    val prompt: String,
    val icon: String,
    val correctAnswer: Int,
    val choices: List<Int>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MathChallengeGame(
    onBack: () -> Unit,
    onScoreSaved: (score: Int, stars: Int) -> Unit
) {
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    fun generateQuestion(): MathQuestion {
        val op = Random.nextInt(3)
        return when (op) {
            0 -> {
                val a = Random.nextInt(2, 9)
                val b = Random.nextInt(1, 8)
                val ans = a + b
                val wrongs = listOf(ans - 1, ans + 2, ans + 1, ans - 2).filter { it > 0 && it != ans }.shuffled().take(2)
                MathQuestion("$a + $b = ?", "⭐", ans, (wrongs + ans).shuffled())
            }
            1 -> {
                val a = Random.nextInt(6, 15)
                val b = Random.nextInt(1, a)
                val ans = a - b
                val wrongs = listOf(ans + 1, ans - 1, ans + 2).filter { it >= 0 && it != ans }.shuffled().take(2)
                MathQuestion("$a - $b = ?", "🍎", ans, (wrongs + ans).shuffled())
            }
            else -> {
                val a = Random.nextInt(2, 6)
                val b = Random.nextInt(2, 5)
                val ans = a * b
                val wrongs = listOf(ans + 2, ans - 2, ans + 4).filter { it > 0 && it != ans }.shuffled().take(2)
                MathQuestion("$a × $b = ?", "🚀", ans, (wrongs + ans).shuffled())
            }
        }
    }

    var currentQuestion by remember { mutableStateOf(generateQuestion()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Math Challenge 🔢", fontWeight = FontWeight.Bold) },
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
                .background(Brush.verticalGradient(listOf(Color(0xFFFEF3C7), Color(0xFFFFFBEB), Color(0xFFF1F5F9))))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Round $round / 10", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFECA57).copy(alpha = 0.3f)
                ) {
                    Text("🔥 Streak: $streak", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontWeight = FontWeight.Bold)
                }
            }

            if (!isFinished) {
                // Question Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(currentQuestion.icon, fontSize = 48.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = currentQuestion.prompt,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        feedbackMessage?.let {
                            Spacer(Modifier.height(8.dp))
                            Text(it, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (it.contains("Awesome")) Color(0xFF10B981) else Color(0xFFFF6B6B))
                        }
                    }
                }

                // Choices
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    currentQuestion.choices.forEach { choice ->
                        Button(
                            onClick = {
                                if (choice == currentQuestion.correctAnswer) {
                                    score += 15 + streak * 2
                                    streak++
                                    feedbackMessage = "🌟 Awesome! Correct! 🌟"
                                } else {
                                    streak = 0
                                    feedbackMessage = "Oops! Try next one! 👍"
                                }

                                if (round >= 10) {
                                    isFinished = true
                                    val stars = if (score >= 120) 3 else if (score >= 60) 2 else 1
                                    onScoreSaved(score, stars)
                                } else {
                                    round++
                                    currentQuestion = generateQuestion()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            shape = RoundedCornerShape(22.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("$choice", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Victory Summary
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🏆 Math Superstar! 🏆", fontSize = 26.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(12.dp))
                        Text("Total Score: $score", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(onClick = onBack, shape = RoundedCornerShape(16.dp)) {
                                Text("Back to Games")
                            }
                            Button(
                                onClick = {
                                    round = 1
                                    score = 0
                                    streak = 0
                                    isFinished = false
                                    feedbackMessage = null
                                    currentQuestion = generateQuestion()
                                },
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Play Again")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}
