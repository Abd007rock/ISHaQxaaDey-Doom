package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserById(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users ORDER BY xp DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isBanned = :banned WHERE id = :userId")
    suspend fun setUserBanned(userId: String, banned: Boolean)

    @Query("UPDATE users SET xp = xp + :points, stars = stars + :starsBonus, gems = gems + :gemsBonus WHERE id = :userId")
    suspend fun addRewards(userId: String, points: Int, starsBonus: Int, gemsBonus: Int)
}

@Dao
interface PostDao {
    @Query("SELECT * FROM posts WHERE isApproved = 1 ORDER BY timestamp DESC")
    fun getApprovedPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity): Long

    @Query("UPDATE posts SET likesCount = likesCount + (CASE WHEN likedByUser = 1 THEN -1 ELSE 1 END), likedByUser = NOT likedByUser WHERE id = :postId")
    suspend fun toggleLike(postId: Long)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: Long)

    @Query("UPDATE posts SET isApproved = :approved WHERE id = :postId")
    suspend fun updatePostApproval(postId: Long, approved: Boolean)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPost(postId: Long): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)
}

@Dao
interface PoemDao {
    @Query("SELECT * FROM poems ORDER BY id ASC")
    fun getAllPoems(): Flow<List<PoemEntity>>

    @Query("SELECT * FROM poems WHERE category = :category ORDER BY id ASC")
    fun getPoemsByCategory(category: String): Flow<List<PoemEntity>>

    @Query("SELECT * FROM poems WHERE isFavorite = 1")
    fun getFavoritePoems(): Flow<List<PoemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoems(poems: List<PoemEntity>)

    @Query("UPDATE poems SET isFavorite = NOT isFavorite WHERE id = :poemId")
    suspend fun toggleFavorite(poemId: Long)

    @Query("UPDATE poems SET readsCount = readsCount + 1 WHERE id = :poemId")
    suspend fun incrementReads(poemId: Long)
}

@Dao
interface ArtworkDao {
    @Query("SELECT * FROM artworks ORDER BY timestamp DESC")
    fun getAllArtworks(): Flow<List<ArtworkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtwork(artwork: ArtworkEntity): Long

    @Query("DELETE FROM artworks WHERE id = :artworkId")
    suspend fun deleteArtwork(artworkId: Long)
}

@Dao
interface GameScoreDao {
    @Query("SELECT * FROM game_scores ORDER BY timestamp DESC")
    fun getAllScores(): Flow<List<GameScoreEntity>>

    @Query("SELECT MAX(score) FROM game_scores WHERE gameId = :gameId")
    fun getHighScoreForGame(gameId: String): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: GameScoreEntity)
}

@Dao
interface ParentalDao {
    @Query("SELECT * FROM parental_settings WHERE id = 1 LIMIT 1")
    fun getParentalSettings(): Flow<ParentalSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveParentalSettings(settings: ParentalSettingsEntity)

    @Query("UPDATE parental_settings SET minutesUsedToday = minutesUsedToday + :mins WHERE id = 1")
    suspend fun recordScreenTime(mins: Int)

    @Query("SELECT * FROM friend_requests ORDER BY timestamp DESC")
    fun getAllFriendRequests(): Flow<List<FriendRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendRequest(req: FriendRequestEntity)

    @Query("UPDATE friend_requests SET status = :status WHERE id = :requestId")
    suspend fun updateRequestStatus(requestId: Long, status: String)

    @Query("SELECT * FROM reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Query("UPDATE reports SET status = :status WHERE id = :reportId")
    suspend fun updateReportStatus(reportId: Long, status: String)
}
