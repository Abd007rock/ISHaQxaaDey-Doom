package com.example.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
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
import com.example.data.model.GameScoreEntity

data class GameInfo(
    val id: String,
    val title: String,
    val icon: String,
    val category: String,
    val description: String,
    val isPlayableNow: Boolean = true,
    val badgeColor: Color = Color(0xFF6C5CE7)
)

@Composable
fun GamesHubScreen(
    scores: List<GameScoreEntity>,
    onLaunchGame: (gameId: String) -> Unit
) {
    val gamesList = remember {
        listOf(
            GameInfo("bubble_pop", "Bubble Pop", "🎈", "Action", "Pop floating bubbles with letters and stars!", true, Color(0xFFFF7675)),
            GameInfo("fruit_catcher", "Fruit Catcher", "🍎", "Arcade", "Catch juicy apples and strawberries in your basket!", true, Color(0xFFFF9F43)),
            GameInfo("memory_match", "Memory Match", "🧩", "Puzzle", "Flip cute animal cards and match identical pairs!", true, Color(0xFF10B981)),
            GameInfo("math_challenge", "Math Challenge", "🔢", "Math", "Speed arithmetic addition and multiplication fun!", true, Color(0xFF54A0FF)),
            GameInfo("shape_matching", "Shape Matching", "⭐", "Shapes", "Match geometric shapes with target outlines!", true, Color(0xFF9B59B6)),
            GameInfo("word_builder", "Word Builder", "🔤", "Language", "Spell words from scrambled letter tiles with audio clues!", true, Color(0xFF00D2D3)),
            GameInfo("dino_runner", "Dino Runner", "🦖", "Adventure", "Jump over friendly obstacles with baby dinosaur!", false, Color(0xFF2ECC71)),
            GameInfo("space_explorer", "Space Explorer", "🚀", "Action", "Steer your rocket past shimmering stars and galaxies!", false, Color(0xFF34495E)),
            GameInfo("alphabet_adventure", "Alphabet Adventure", "📚", "Learning", "Journey through phonics and vocabulary lands!", false, Color(0xFFE67E22)),
            GameInfo("coloring_games", "Coloring Games", "🎨", "Art", "Fill vibrant colors into storybook illustrations!", false, Color(0xFFE84393)),
            GameInfo("animal_matching", "Animal Matching", "🐾", "Nature", "Match animal footprints, habitats and sounds!", false, Color(0xFF00B894)),
            GameInfo("puzzle_adventure", "Puzzle Adventure", "🗺️", "Quest", "Solve friendly riddles across magical islands!", false, Color(0xFF0984E3)),
            GameInfo("number_learning", "Number Learning", "🔟", "Math", "Count colorful objects and unlock treasure chests!", false, Color(0xFF6C5CE7))
        )
    }

    var selectedFilter by remember { mutableStateOf("All") }
    val categories = listOf("All", "Action", "Puzzle", "Math", "Language", "Shapes")

    val filteredGames = remember(selectedFilter) {
        if (selectedFilter == "All") gamesList else gamesList.filter { it.category == selectedFilter }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Hero Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF6C5CE7), Color(0xFF8075E8), Color(0xFF54A0FF))
                    )
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column {
                Text(
                    text = "Miniclip Fun Zone 🎮",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "100% Free • Safe • No In-App Purchases • Educational",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // Filter chips
        ScrollableTabRow(
            selectedTabIndex = categories.indexOf(selectedFilter),
            edgePadding = 16.dp,
            divider = {}
        ) {
            categories.forEach { cat ->
                Tab(
                    selected = selectedFilter == cat,
                    onClick = { selectedFilter = cat },
                    text = { Text(cat, fontWeight = if (selectedFilter == cat) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        // Games list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredGames, key = { it.id }) { game ->
                val highestScore = scores.filter { it.gameId == game.id }.maxOfOrNull { it.score }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .clickable(enabled = game.isPlayableNow) {
                            onLaunchGame(game.id)
                        }
                        .testTag("game_card_${game.id}"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Game Icon
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(game.badgeColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(game.icon, fontSize = 36.sp)
                        }

                        Spacer(Modifier.width(16.dp))

                        // Game info
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = game.title,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = game.badgeColor.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = game.category,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = game.badgeColor,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = game.description,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2
                            )
                            if (highestScore != null) {
                                Spacer(Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFECA57), modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("High Score: $highestScore", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }

                        Spacer(Modifier.width(10.dp))

                        // Play Button
                        if (game.isPlayableNow) {
                            FilledIconButton(
                                onClick = { onLaunchGame(game.id) },
                                shape = RoundedCornerShape(14.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = "Play")
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    "Zone+",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}
