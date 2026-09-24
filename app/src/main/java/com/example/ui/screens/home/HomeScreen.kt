package com.example.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.PoemEntity
import com.example.data.model.PostEntity
import com.example.data.model.UserEntity
import com.example.util.AppLanguage
import com.example.util.MultilingualStrings

@Composable
fun HomeScreen(
    user: UserEntity?,
    poems: List<PoemEntity>,
    recentPosts: List<PostEntity>,
    selectedLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onNavigateToGames: () -> Unit,
    onNavigateToToddler: () -> Unit,
    onNavigateToPoems: () -> Unit,
    onNavigateToStudio: () -> Unit,
    onNavigateToSocial: () -> Unit,
    onLaunchGame: (String) -> Unit
) {
    val currentUser = user ?: UserEntity(
        id = "user_current",
        name = "Ayaan & Safiya",
        email = "family@kids.com",
        avatar = "🦁",
        bio = "Learning every day!"
    )

    var showLanguageMenu by remember { mutableStateOf(false) }

    fun t(key: String): String = MultilingualStrings.getString(key, selectedLanguage)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Hero Banner with Illustrated Asset
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column {
                    Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                        // Generated hero banner image
                        Image(
                            painter = painterResource(id = R.drawable.hero_banner_1790245518617),
                            contentDescription = "IshaQxaaDey Hero Illustration",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Top gradient overlay for contrast
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent, Color.Black.copy(alpha = 0.6f))
                                    )
                                )
                        )

                        // Top Row: Avatar greeting & Language Switcher
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.9f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(currentUser.avatar, fontSize = 22.sp)
                                }
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("Hi, ${currentUser.name}! 👋", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("🔥 ${currentUser.streakDays} ${t("daily_streak")}", color = Color(0xFFFECA57), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            // Language Button
                            Box {
                                FilledTonalButton(
                                    onClick = { showLanguageMenu = true },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color.White.copy(alpha = 0.85f)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("language_selector_btn")
                                ) {
                                    Text("${selectedLanguage.flag} ${selectedLanguage.displayName}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }

                                DropdownMenu(
                                    expanded = showLanguageMenu,
                                    onDismissRequest = { showLanguageMenu = false }
                                ) {
                                    AppLanguage.values().forEach { lang ->
                                        DropdownMenuItem(
                                            text = { Text("${lang.flag} ${lang.displayName}") },
                                            onClick = {
                                                onLanguageChange(lang)
                                                showLanguageMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Bottom Hero Tagline
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = t("app_title"),
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp
                            )
                            Text(
                                text = t("tagline"),
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Stats strip at bottom of card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        HeroStatChip("⭐", "${currentUser.stars}", t("stars"))
                        HeroStatChip("💎", "${currentUser.gems}", t("gems"))
                        HeroStatChip("⚡", "${currentUser.xp}", t("xp_points"))
                        HeroStatChip("🎯", "${currentUser.learningScore}%", t("learning_score"))
                    }
                }
            }
        }

        // Quick Navigation Grid
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Explore & Learn 🚀", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = t("nav_games"),
                        subtitle = "13 Free Mini-Games",
                        emoji = "🎮",
                        bg = Color(0xFFFF7675),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToGames
                    )
                    QuickActionCard(
                        title = t("nav_toddler"),
                        subtitle = "Ages 2-6 ABC & Rhymes",
                        emoji = "🧸",
                        bg = Color(0xFFFECA57),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToToddler
                    )
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = t("nav_poems"),
                        subtitle = "Audio & 7 Themes",
                        emoji = "📖",
                        bg = Color(0xFF54A0FF),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToPoems
                    )
                    QuickActionCard(
                        title = t("nav_studio"),
                        subtitle = "Draw & Color Art",
                        emoji = "🎨",
                        bg = Color(0xFF10B981),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToStudio
                    )
                }
            }
        }

        // Today's Missions & Daily Achievements
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(t("daily_challenges"), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFECA57).copy(alpha = 0.2f)
                        ) {
                            Text("3/4 Done 🎉", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE67E22), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    MissionItem("🎈 Pop 20 bubbles in Bubble Pop", "+25 XP", true)
                    MissionItem("📖 Listen to a bedtime or Islamic poem", "+20 XP", true)
                    MissionItem("🎨 Paint a picture in Creative Studio", "+30 XP", true)
                    MissionItem("🔢 Answer 5 Math Challenge questions", "+20 XP", false)
                }
            }
        }

        // Featured Mini-Games Carousel
        item {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(t("featured_games"), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    TextButton(onClick = onNavigateToGames) {
                        Text("See All (13)")
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        FeaturedGameCard("Bubble Pop", "🎈", "Pop letters & symbols!", Color(0xFFFF7675)) {
                            onLaunchGame("bubble_pop")
                        }
                    }
                    item {
                        FeaturedGameCard("Fruit Catcher", "🍎", "Catch apples & bananas!", Color(0xFFFF9F43)) {
                            onLaunchGame("fruit_catcher")
                        }
                    }
                    item {
                        FeaturedGameCard("Memory Match", "🧩", "Find matching cute animals!", Color(0xFF10B981)) {
                            onLaunchGame("memory_match")
                        }
                    }
                    item {
                        FeaturedGameCard("Math Challenge", "🔢", "Fast additions & multipliers!", Color(0xFF54A0FF)) {
                            onLaunchGame("math_challenge")
                        }
                    }
                }
            }
        }

        // Popular Poems Section
        item {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(t("popular_poems"), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    TextButton(onClick = onNavigateToPoems) {
                        Text("View Library")
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(poems.take(4)) { poem ->
                        Card(
                            modifier = Modifier
                                .width(220.dp)
                                .clickable(onClick = onNavigateToPoems),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(poem.category, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(poem.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
                                Text("By ${poem.author}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(6.dp))
                                Text(poem.content, fontSize = 12.sp, maxLines = 2, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Recent Community Safe Feed Snippet
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(t("safe_feed"), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    TextButton(onClick = onNavigateToSocial) {
                        Text("View Feed")
                    }
                }

                recentPosts.firstOrNull()?.let { post ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onNavigateToSocial),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(post.authorAvatar, fontSize = 28.sp)
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(post.authorName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(post.content, fontSize = 12.sp, maxLines = 2, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text("❤️ ${post.likesCount}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFFF6B6B))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeroStatChip(icon: String, value: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(icon, fontSize = 16.sp)
        Spacer(Modifier.width(4.dp))
        Column {
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    emoji: String,
    bg: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = bg.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(emoji, fontSize = 32.sp)
            Spacer(Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun FeaturedGameCard(title: String, emoji: String, desc: String, bg: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bg.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(emoji, fontSize = 36.sp)
            Spacer(Modifier.height(6.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            Spacer(Modifier.height(8.dp))
            FilledTonalButton(
                onClick = onClick,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier.height(30.dp)
            ) {
                Text("Play", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MissionItem(title: String, reward: String, isDone: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
                if (isDone) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isDone) Color(0xFF10B981) else Color(0xFFCBD5E1),
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                title,
                fontSize = 12.sp,
                fontWeight = if (isDone) FontWeight.Medium else FontWeight.Normal,
                color = if (isDone) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(reward, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}
