package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val avatar: String, // e.g. "🦁", "🦊", "⭐", "🚀", "🐧", "🦉"
    val bio: String,
    val isChild: Boolean = true,
    val isParentVerified: Boolean = true,
    val parentPin: String = "1234",
    val xp: Int = 350,
    val stars: Int = 42,
    val gems: Int = 85,
    val learningScore: Int = 92,
    val streakDays: Int = 5,
    val badges: String = "First Art,Math Genius,Bookworm,Bubble Master",
    val role: String = "child", // "child", "parent", "admin"
    val isBanned: Boolean = false
)

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorId: String,
    val authorName: String,
    val authorAvatar: String,
    val content: String,
    val imageUrl: String? = null,
    val type: String = "status", // "status", "artwork", "achievement"
    val likesCount: Int = 0,
    val likedByUser: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val isApproved: Boolean = true,
    val reactions: String = "❤️:4,🌟:9,👏:2,🎉:5,🚀:3"
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val postId: Long,
    val authorName: String,
    val authorAvatar: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "poems")
data class PoemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val author: String,
    val category: String, // Friendship, Nature, Educational, Bedtime, Children's, Islamic, Inspirational
    val content: String,
    val isFavorite: Boolean = false,
    val readsCount: Int = 0,
    val language: String = "en"
)

@Entity(tableName = "artworks")
data class ArtworkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val authorName: String,
    val pathPointsJson: String,
    val stickerNames: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "game_scores")
data class GameScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gameId: String,
    val gameTitle: String,
    val score: Int,
    val starsEarned: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "parental_settings")
data class ParentalSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val dailyScreenTimeMinutes: Int = 60,
    val minutesUsedToday: Int = 18,
    val contentFilterStrictness: String = "Strict", // "Strict", "Moderate"
    val requireFriendApproval: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val parentPin: String = "1234"
)

@Entity(tableName = "friend_requests")
data class FriendRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderName: String,
    val senderAvatar: String,
    val targetUserId: String = "current_user",
    val status: String = "pending", // "pending", "approved", "rejected"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reportedPostId: Long,
    val reportedContent: String,
    val reporterName: String,
    val reason: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "open" // "open", "resolved", "dismissed"
)
