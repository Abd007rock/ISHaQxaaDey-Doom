package com.example.ui.screens.parent

import androidx.compose.foundation.background
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
import com.example.data.model.FriendRequestEntity
import com.example.data.model.ParentalSettingsEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboardScreen(
    settings: ParentalSettingsEntity?,
    friendRequests: List<FriendRequestEntity>,
    onUpdateSettings: (ParentalSettingsEntity) -> Unit,
    onApproveFriend: (requestId: Long) -> Unit,
    onRejectFriend: (requestId: Long) -> Unit
) {
    var isUnlocked by remember { mutableStateOf(false) }
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    val currentSettings = settings ?: ParentalSettingsEntity()
    var screenTimeLimit by remember(settings) { mutableFloatStateOf(currentSettings.dailyScreenTimeMinutes.toFloat()) }
    var strictness by remember(settings) { mutableStateOf(currentSettings.contentFilterStrictness) }
    var requireFriendApproval by remember(settings) { mutableStateOf(currentSettings.requireFriendApproval) }
    var notificationsEnabled by remember(settings) { mutableStateOf(currentSettings.notificationsEnabled) }

    if (!isUnlocked) {
        // PIN Lock Screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Lock, contentDescription = "Locked", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(44.dp))
            }

            Spacer(Modifier.height(20.dp))
            Text("Parent Verification", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                "Please enter your 4-digit Parent PIN to access parental controls and child safety settings.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = enteredPin,
                onValueChange = {
                    if (it.length <= 4) {
                        enteredPin = it
                        pinError = false
                    }
                },
                placeholder = { Text("Default PIN is 1234") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                isError = pinError,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(0.7f).testTag("parent_pin_field")
            )

            if (pinError) {
                Text("Incorrect PIN. Please try again.", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    if (enteredPin == currentSettings.parentPin) {
                        isUnlocked = true
                    } else {
                        pinError = true
                    }
                },
                modifier = Modifier.fillMaxWidth(0.7f).height(50.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = enteredPin.length >= 4
            ) {
                Text("Unlock Dashboard", fontWeight = FontWeight.Bold)
            }
        }
    } else {
        // Unlocked Dashboard Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("👨‍👩‍👧‍👦", fontSize = 32.sp)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Parental Guardian Hub", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                Text("Managing Child Account: Ayaan & Safiya", fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
                            }
                        }
                    }
                }
            }

            // Screen Time Control Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text("Screen Time Control", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        }

                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Daily Allowance: ${screenTimeLimit.toInt()} minutes",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Slider(
                            value = screenTimeLimit,
                            onValueChange = {
                                screenTimeLimit = it
                                onUpdateSettings(currentSettings.copy(dailyScreenTimeMinutes = it.toInt()))
                            },
                            valueRange = 15f..180f,
                            steps = 10,
                            modifier = Modifier.testTag("screen_time_slider")
                        )

                        // Usage Progress
                        val usedPercent = (currentSettings.minutesUsedToday.toFloat() / screenTimeLimit).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { usedPercent },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = if (usedPercent > 0.8f) Color(0xFFFF6B6B) else MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "${currentSettings.minutesUsedToday} mins used today (${(screenTimeLimit.toInt() - currentSettings.minutesUsedToday).coerceAtLeast(0)} mins remaining)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Friend Requests Approval System
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.GroupAdd, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(Modifier.width(8.dp))
                            Text("Friend Approval System", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        }

                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Every new friend request must be approved by a parent before your child can message or see them.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(14.dp))
                        val pending = friendRequests.filter { it.status == "pending" }
                        if (pending.isEmpty()) {
                            Text("✅ No pending friend requests at this time.", fontSize = 13.sp, color = Color(0xFF10B981))
                        } else {
                            pending.forEach { req ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(req.senderAvatar, fontSize = 24.sp)
                                        Spacer(Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(req.senderName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("Wants to be friends", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        FilledTonalIconButton(onClick = { onApproveFriend(req.id) }) {
                                            Icon(Icons.Filled.Check, contentDescription = "Approve", tint = Color(0xFF10B981))
                                        }
                                        IconButton(onClick = { onRejectFriend(req.id) }) {
                                            Icon(Icons.Filled.Close, contentDescription = "Reject", tint = Color(0xFFFF6B6B))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Safety Filters & Toggles
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Safety & Content Moderation", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Require Friend Approval", fontWeight = FontWeight.Medium)
                                Text("Friends must be approved by parents", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = requireFriendApproval,
                                onCheckedChange = {
                                    requireFriendApproval = it
                                    onUpdateSettings(currentSettings.copy(requireFriendApproval = it))
                                }
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Learning Notifications", fontWeight = FontWeight.Medium)
                                Text("Daily reminders and streak rewards", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = {
                                    notificationsEnabled = it
                                    onUpdateSettings(currentSettings.copy(notificationsEnabled = it))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
