package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity

data class BadgeItem(
    val name: String,
    val icon: String,
    val description: String,
    val isUnlocked: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: UserEntity?,
    allUsers: List<UserEntity>,
    onUpdateUser: (UserEntity) -> Unit,
    onLogout: () -> Unit
) {
    val currentUser = user ?: UserEntity(
        id = "user_current",
        name = "Ayaan & Safiya",
        email = "family@kids.com",
        avatar = "🦁",
        bio = "Learning and creating every day!"
    )

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAvatarPicker by remember { mutableStateOf(false) }
    var showRoleSwitchDialog by remember { mutableStateOf(false) }

    val avatarOptions = listOf("🦁", "🦊", "⭐", "🚀", "🐧", "🦉", "🐰", "🐬", "🎨", "🦄")

    val badgesList = remember(currentUser.badges) {
        listOf(
            BadgeItem("First Art", "🎨", "Created first canvas artwork", currentUser.badges.contains("First Art")),
            BadgeItem("Math Genius", "🔢", "Scored 100+ in Math Challenge", currentUser.badges.contains("Math Genius")),
            BadgeItem("Bookworm", "📖", "Read 5 lovely poems", currentUser.badges.contains("Bookworm")),
            BadgeItem("Bubble Master", "🎈", "Popped 20 bubbles in one round", currentUser.badges.contains("Bubble Master")),
            BadgeItem("Star Explorer", "🌟", "Explored all learning zones", currentUser.badges.contains("Star Explorer")),
            BadgeItem("Streak Hero", "🔥", "Maintained a 5-day streak", currentUser.streakDays >= 5)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Profile Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar with edit button
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(Color(0xFFFECA57), Color(0xFFFF9F43))
                                    )
                                )
                                .clickable { showAvatarPicker = true }
                                .testTag("profile_avatar"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(currentUser.avatar, fontSize = 48.sp)
                        }
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { showAvatarPicker = true }
                        ) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Change avatar",
                                tint = Color.White,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Text(currentUser.name, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(4.dp))

                    // Role Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = when (currentUser.role.lowercase()) {
                            "admin" -> Color(0xFFFECA57).copy(alpha = 0.25f)
                            "parent" -> Color(0xFF10B981).copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.primaryContainer
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            val roleLabel = when (currentUser.role.lowercase()) {
                                "admin" -> "🛡️ Administrator"
                                "parent" -> "👨‍👩‍👧 Parent / Guardian"
                                else -> "👶 Child Account"
                            }
                            Text(
                                roleLabel,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (currentUser.role.lowercase()) {
                                    "admin" -> Color(0xFFD97706)
                                    "parent" -> Color(0xFF059669)
                                    else -> MaterialTheme.colorScheme.primary
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        currentUser.bio,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(Modifier.height(18.dp))

                    // Quick Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStatItem("🔥", "${currentUser.streakDays}", "Days Streak")
                        ProfileStatItem("⭐", "${currentUser.stars}", "Stars Earned")
                        ProfileStatItem("💎", "${currentUser.gems}", "Gems")
                        ProfileStatItem("🎯", "${currentUser.learningScore}%", "Learning")
                    }

                    Spacer(Modifier.height(18.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = { showEditProfileDialog = true },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Edit Profile", fontSize = 13.sp)
                        }

                        Button(
                            onClick = { showRoleSwitchDialog = true },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Switch Role", fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Achievements & Badges
        item {
            Text("Achievements & Badges 🏆", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    badgesList.chunked(2).forEach { rowBadges ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowBadges.forEach { badge ->
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (badge.isUnlocked) Color(0xFFFEF3C7) else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(badge.icon, fontSize = 28.sp)
                                        Spacer(Modifier.width(10.dp))
                                        Column {
                                            Text(badge.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(
                                                if (badge.isUnlocked) "Unlocked ⭐" else "Locked 🔒",
                                                fontSize = 11.sp,
                                                color = if (badge.isUnlocked) Color(0xFFE67E22) else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Friends Leaderboard Section
        item {
            Text("Friends Leaderboard 🌟", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    allUsers.sortedByDescending { it.xp }.forEachIndexed { idx, u ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (idx) {
                                    0 -> "🥇"
                                    1 -> "🥈"
                                    2 -> "🥉"
                                    else -> "#${idx + 1}"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(36.dp)
                            )
                            Text(u.avatar, fontSize = 24.sp)
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(u.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Score: ${u.learningScore}%", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text("${u.xp} XP", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                        }
                        if (idx < allUsers.size - 1) {
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                        }
                    }
                }
            }
        }

        // Account actions
        item {
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("logout_button")
            ) {
                Icon(Icons.Filled.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Sign Out")
            }
        }
    }

    // Avatar Picker Dialog
    if (showAvatarPicker) {
        AlertDialog(
            onDismissRequest = { showAvatarPicker = false },
            title = { Text("Choose Your Friendly Avatar", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    avatarOptions.chunked(5).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            row.forEach { av ->
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(if (currentUser.avatar == av) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                                        .border(
                                            width = if (currentUser.avatar == av) 2.dp else 0.dp,
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            onUpdateUser(currentUser.copy(avatar = av))
                                            showAvatarPicker = false
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(av, fontSize = 28.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAvatarPicker = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Role Switch Dialog with Security Barrier
    if (showRoleSwitchDialog) {
        var targetRole by remember { mutableStateOf("child") }
        var enteredPin by remember { mutableStateOf("") }
        var pinError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showRoleSwitchDialog = false },
            title = { Text("Security: Role Authorization", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Children cannot access parent or administrative tools without guardian authorization.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilterChip(
                            selected = targetRole == "child",
                            onClick = { targetRole = "child" },
                            label = { Text("Child") }
                        )
                        FilterChip(
                            selected = targetRole == "parent",
                            onClick = { targetRole = "parent" },
                            label = { Text("Parent") }
                        )
                        FilterChip(
                            selected = targetRole == "admin",
                            onClick = { targetRole = "admin" },
                            label = { Text("Admin") }
                        )
                    }

                    if (targetRole != "child") {
                        OutlinedTextField(
                            value = enteredPin,
                            onValueChange = {
                                enteredPin = it
                                pinError = false
                            },
                            label = { Text(if (targetRole == "admin") "Admin Passphrase (admin99 or 1234)" else "Parent PIN (1234)") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            isError = pinError,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        )
                        if (pinError) {
                            Text("Incorrect authorization PIN. Access denied.", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (targetRole == "child") {
                            onUpdateUser(currentUser.copy(role = "child", isChild = true))
                            showRoleSwitchDialog = false
                        } else if (targetRole == "parent") {
                            if (enteredPin == currentUser.parentPin || enteredPin == "1234") {
                                onUpdateUser(currentUser.copy(role = "parent", isChild = false))
                                showRoleSwitchDialog = false
                            } else {
                                pinError = true
                            }
                        } else if (targetRole == "admin") {
                            if (enteredPin == "admin99" || enteredPin == "1234") {
                                onUpdateUser(currentUser.copy(role = "admin", isChild = false))
                                showRoleSwitchDialog = false
                            } else {
                                pinError = true
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Apply Role")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRoleSwitchDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        var nameInput by remember { mutableStateOf(currentUser.name) }
        var bioInput by remember { mutableStateOf(currentUser.bio) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )
                    OutlinedTextField(
                        value = bioInput,
                        onValueChange = { bioInput = it },
                        label = { Text("Bio") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateUser(currentUser.copy(name = nameInput, bio = bioInput))
                        showEditProfileDialog = false
                    },
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileStatItem(icon: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 24.sp)
        Spacer(Modifier.height(2.dp))
        Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
