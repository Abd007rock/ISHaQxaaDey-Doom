package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class IshaQxaaDeyRepository(private val db: AppDatabase) {

    // User operations
    fun getCurrentUser(userId: String = "user_current"): Flow<UserEntity?> = db.userDao().getUserById(userId)
    fun getAllUsers(): Flow<List<UserEntity>> = db.userDao().getAllUsers()
    suspend fun saveUser(user: UserEntity) = db.userDao().insertUser(user)
    suspend fun setUserBanned(userId: String, banned: Boolean) = db.userDao().setUserBanned(userId, banned)
    suspend fun addRewards(userId: String = "user_current", xp: Int, stars: Int, gems: Int) {
        db.userDao().addRewards(userId, xp, stars, gems)
    }

    // Posts & Feed operations
    fun getApprovedPosts(): Flow<List<PostEntity>> = db.postDao().getApprovedPosts()
    fun getAllPosts(): Flow<List<PostEntity>> = db.postDao().getAllPosts()
    suspend fun createPost(post: PostEntity): Long = db.postDao().insertPost(post)
    suspend fun toggleLike(postId: Long) = db.postDao().toggleLike(postId)
    suspend fun deletePost(postId: Long) = db.postDao().deletePost(postId)
    suspend fun setPostApproval(postId: Long, approved: Boolean) = db.postDao().updatePostApproval(postId, approved)

    // Comments
    fun getComments(postId: Long): Flow<List<CommentEntity>> = db.commentDao().getCommentsForPost(postId)
    suspend fun addComment(comment: CommentEntity) = db.commentDao().insertComment(comment)

    // Poems
    fun getAllPoems(): Flow<List<PoemEntity>> = db.poemDao().getAllPoems()
    fun getPoemsByCategory(category: String): Flow<List<PoemEntity>> = db.poemDao().getPoemsByCategory(category)
    fun getFavoritePoems(): Flow<List<PoemEntity>> = db.poemDao().getFavoritePoems()
    suspend fun toggleFavoritePoem(poemId: Long) = db.poemDao().toggleFavorite(poemId)
    suspend fun recordPoemRead(poemId: Long) {
        db.poemDao().incrementReads(poemId)
        addRewards("user_current", xp = 20, stars = 3, gems = 5)
    }

    // Artworks
    fun getAllArtworks(): Flow<List<ArtworkEntity>> = db.artworkDao().getAllArtworks()
    suspend fun saveArtwork(artwork: ArtworkEntity): Long {
        val id = db.artworkDao().insertArtwork(artwork)
        addRewards("user_current", xp = 30, stars = 5, gems = 8)
        return id
    }

    // Game Scores
    fun getAllScores(): Flow<List<GameScoreEntity>> = db.gameScoreDao().getAllScores()
    fun getHighScore(gameId: String): Flow<Int?> = db.gameScoreDao().getHighScoreForGame(gameId)
    suspend fun recordGameScore(gameId: String, title: String, score: Int, stars: Int) {
        db.gameScoreDao().insertScore(
            GameScoreEntity(
                gameId = gameId,
                gameTitle = title,
                score = score,
                starsEarned = stars
            )
        )
        addRewards("user_current", xp = score / 2 + 10, stars = stars, gems = stars * 2)
    }

    // Parental Controls & Safety
    fun getParentalSettings(): Flow<ParentalSettingsEntity?> = db.parentalDao().getParentalSettings()
    suspend fun updateParentalSettings(settings: ParentalSettingsEntity) = db.parentalDao().saveParentalSettings(settings)
    suspend fun addScreenTime(minutes: Int) = db.parentalDao().recordScreenTime(minutes)

    fun getFriendRequests(): Flow<List<FriendRequestEntity>> = db.parentalDao().getAllFriendRequests()
    suspend fun sendFriendRequest(senderName: String, senderAvatar: String) {
        db.parentalDao().insertFriendRequest(
            FriendRequestEntity(
                senderName = senderName,
                senderAvatar = senderAvatar,
                status = "pending"
            )
        )
    }
    suspend fun updateFriendRequestStatus(requestId: Long, status: String) {
        db.parentalDao().updateRequestStatus(requestId, status)
    }

    // Content Moderation & Reports
    fun getReports(): Flow<List<ReportEntity>> = db.parentalDao().getAllReports()
    suspend fun submitReport(postId: Long, content: String, reporter: String, reason: String) {
        db.parentalDao().insertReport(
            ReportEntity(
                reportedPostId = postId,
                reportedContent = content,
                reporterName = reporter,
                reason = reason,
                status = "open"
            )
        )
    }
    suspend fun updateReportStatus(reportId: Long, status: String) {
        db.parentalDao().updateReportStatus(reportId, status)
    }
}
