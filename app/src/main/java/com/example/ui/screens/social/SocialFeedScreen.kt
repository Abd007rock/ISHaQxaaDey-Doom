package com.example.ui.screens.social

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CommentEntity
import com.example.data.model.PostEntity
import com.example.service.ModerationService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialFeedScreen(
    posts: List<PostEntity>,
    onToggleLike: (postId: Long) -> Unit,
    onCreatePost: (content: String, type: String) -> Unit,
    onAddComment: (postId: Long, text: String) -> Unit,
    commentsForPost: (postId: Long) -> List<CommentEntity>,
    onReportPost: (postId: Long, content: String, reason: String) -> Unit
) {
    var showCreatePostDialog by remember { mutableStateOf(false) }
    var viewingCommentsPostId by remember { mutableStateOf<Long?>(null) }
    var reportingPost by remember { mutableStateOf<PostEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreatePostDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("create_post_fab")
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Edit, contentDescription = "Create Safe Post")
                    Spacer(Modifier.width(6.dp))
                    Text("Share", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Child Protection Safety Notice Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🛡️", fontSize = 28.sp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "Protected Community Zone",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "All posts & comments are screened by our Child Protection AI. Be kind, safe, and supportive!",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            items(posts, key = { it.id }) { post ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("post_card_${post.id}"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Author header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(post.authorAvatar, fontSize = 24.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = post.authorName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = when (post.type) {
                                        "artwork" -> "🎨 Shared an Artwork"
                                        "achievement" -> "⭐ Unlocked Achievement"
                                        else -> "💬 Safe Thought"
                                    },
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            // Report button
                            IconButton(onClick = { reportingPost = post }) {
                                Icon(Icons.Filled.Flag, contentDescription = "Report Post", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Post Content
                        Text(
                            text = post.content,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(Modifier.height(14.dp))

                        // Reaction Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Reactions summary
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("❤️", "🌟", "👏", "🎉", "🚀").forEach { emoji ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.clickable { onToggleLike(post.id) }
                                    ) {
                                        Text(emoji, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                                    }
                                }
                            }

                            // Like & Comments count
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { onToggleLike(post.id) }) {
                                    Icon(
                                        if (post.likedByUser) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                        contentDescription = "Like",
                                        tint = if (post.likedByUser) Color(0xFFFF6B6B) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text("${post.likesCount}", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                                Spacer(Modifier.width(8.dp))

                                IconButton(onClick = { viewingCommentsPostId = post.id }) {
                                    Icon(Icons.Filled.ChatBubbleOutline, contentDescription = "Comments", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(64.dp))
            }
        }
    }

    // Create Safe Post Dialog
    if (showCreatePostDialog) {
        var postText by remember { mutableStateOf("") }
        var moderationWarning by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showCreatePostDialog = false },
            title = { Text("Share Something Kind ✨", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Express yourself! Share an achievement, learning tip, or smile.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = postText,
                        onValueChange = {
                            postText = it
                            moderationWarning = null
                        },
                        placeholder = { Text("What did you learn today?") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .testTag("post_text_field"),
                        shape = RoundedCornerShape(16.dp)
                    )
                    moderationWarning?.let { warning ->
                        Spacer(Modifier.height(8.dp))
                        Text(warning, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val check = ModerationService.screenText(postText)
                        if (!check.isSafe) {
                            moderationWarning = check.violationReason
                        } else {
                            onCreatePost(check.sanitizedText, "status")
                            showCreatePostDialog = false
                        }
                    },
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Post Safely")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreatePostDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Comments Sheet
    viewingCommentsPostId?.let { postId ->
        var commentInput by remember { mutableStateOf("") }
        var commentWarning by remember { mutableStateOf<String?>(null) }
        val comments = commentsForPost(postId)

        AlertDialog(
            onDismissRequest = { viewingCommentsPostId = null },
            modifier = Modifier.fillMaxWidth(0.95f),
            title = { Text("Friendly Comments 💬", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (comments.isEmpty()) {
                        Text("No comments yet. Say something uplifting!", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            comments.forEach { c ->
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier.padding(10.dp)) {
                                        Text(c.authorAvatar, fontSize = 18.sp)
                                        Spacer(Modifier.width(8.dp))
                                        Column {
                                            Text(c.authorName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text(c.content, fontSize = 13.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Quick safe compliments
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Great job! 👏", "So colorful! 🎨", "Awesome! ⭐").forEach { quickText ->
                            SuggestionChip(
                                onClick = { commentInput = quickText },
                                label = { Text(quickText, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = commentInput,
                        onValueChange = {
                            commentInput = it
                            commentWarning = null
                        },
                        placeholder = { Text("Write kind comment...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    commentWarning?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val check = ModerationService.screenText(commentInput)
                        if (!check.isSafe) {
                            commentWarning = check.violationReason
                        } else {
                            onAddComment(postId, check.sanitizedText)
                            commentInput = ""
                        }
                    },
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Send")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewingCommentsPostId = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Report Post Dialog
    reportingPost?.let { post ->
        var selectedReason by remember { mutableStateOf("Not appropriate for children") }
        val reasons = listOf("Not appropriate for children", "Looks like personal information", "Unkind language", "Spam")

        AlertDialog(
            onDismissRequest = { reportingPost = null },
            title = { Text("Report Post to Moderation 🚨", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Help keep IshaQxaaDey safe for everyone. Why are you reporting this?", fontSize = 13.sp)
                    Spacer(Modifier.height(12.dp))
                    reasons.forEach { r ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedReason = r }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(selected = selectedReason == r, onClick = { selectedReason = r })
                            Spacer(Modifier.width(8.dp))
                            Text(r, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onReportPost(post.id, post.content, selectedReason)
                        reportingPost = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Submit Report")
                }
            },
            dismissButton = {
                TextButton(onClick = { reportingPost = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
