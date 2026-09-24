package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.IshaQxaaDeyRepository
import com.example.service.*
import com.example.util.AppLanguage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository = IshaQxaaDeyRepository(database)

    val currentUser: StateFlow<UserEntity?> = repository.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val posts: StateFlow<List<PostEntity>> = repository.getAllPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val poems: StateFlow<List<PoemEntity>> = repository.getAllPoems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val artworks: StateFlow<List<ArtworkEntity>> = repository.getAllArtworks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gameScores: StateFlow<List<GameScoreEntity>> = repository.getAllScores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val parentalSettings: StateFlow<ParentalSettingsEntity?> = repository.getParentalSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val friendRequests: StateFlow<List<FriendRequestEntity>> = repository.getFriendRequests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reports: StateFlow<List<ReportEntity>> = repository.getReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val selectedLanguage: StateFlow<AppLanguage> = _selectedLanguage.asStateFlow()

    private val _postComments = MutableStateFlow<Map<Long, List<CommentEntity>>>(emptyMap())

    val authState: StateFlow<AuthState> = AuthManager.authState

    private val screenTimeManager = ScreenTimeManager(viewModelScope) { minutesUsed ->
        viewModelScope.launch {
            repository.addScreenTime(1)
        }
    }
    val screenTimeStatus: StateFlow<ScreenTimeStatus> = screenTimeManager.status

    init {
        // Observe comments for all posts
        viewModelScope.launch {
            posts.collect { currentPosts ->
                currentPosts.forEach { post ->
                    launch {
                        repository.getComments(post.id).collect { comments: List<CommentEntity> ->
                            _postComments.update { it + (post.id to comments) }
                        }
                    }
                }
            }
        }

        // Initialize AuthManager with current user
        viewModelScope.launch {
            currentUser.collect { user ->
                if (user != null && AuthManager.authState.value is AuthState.Idle) {
                    AuthManager.initializeSession(user)
                }
            }
        }

        // Initialize ScreenTimeManager with parental settings
        viewModelScope.launch {
            parentalSettings.collect { settings ->
                if (settings != null) {
                    screenTimeManager.initialize(
                        dailyLimitMinutes = settings.dailyScreenTimeMinutes,
                        currentMinutesUsed = settings.minutesUsedToday
                    )
                }
            }
        }
    }

    fun getCommentsForPost(postId: Long): List<CommentEntity> {
        return _postComments.value[postId] ?: emptyList()
    }

    fun setLanguage(lang: AppLanguage) {
        _selectedLanguage.value = lang
    }

    fun saveGameScore(gameId: String, score: Int, stars: Int) {
        viewModelScope.launch {
            val title = gameId.split('_').joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
            repository.recordGameScore(gameId = gameId, title = title, score = score, stars = stars)
        }
    }

    fun toggleLike(postId: Long) {
        viewModelScope.launch {
            repository.toggleLike(postId)
        }
    }

    fun createPost(content: String, type: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            // Verify with Defense-in-depth Moderation
            val check = ModerationService.screenText(content)
            if (!check.isSafe) return@launch

            repository.createPost(
                PostEntity(
                    authorId = user.id,
                    authorName = user.name,
                    authorAvatar = user.avatar,
                    content = check.sanitizedText,
                    type = type
                )
            )
            repository.addRewards(user.id, xp = 15, stars = 1, gems = 2)
        }
    }

    fun addComment(postId: Long, text: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val check = ModerationService.screenText(text)
            if (!check.isSafe) return@launch

            repository.addComment(
                CommentEntity(
                    postId = postId,
                    authorName = user.name,
                    authorAvatar = user.avatar,
                    content = check.sanitizedText
                )
            )
            repository.addRewards(user.id, xp = 5, stars = 0, gems = 1)
        }
    }

    fun toggleFavoritePoem(poemId: Long) {
        viewModelScope.launch {
            repository.toggleFavoritePoem(poemId)
        }
    }

    fun recordPoemRead(poemId: Long) {
        viewModelScope.launch {
            repository.recordPoemRead(poemId)
        }
    }

    fun saveArtwork(artwork: ArtworkEntity) {
        viewModelScope.launch {
            repository.saveArtwork(artwork)
        }
    }

    fun updateParentalSettings(settings: ParentalSettingsEntity) {
        viewModelScope.launch {
            repository.updateParentalSettings(settings)
            screenTimeManager.initialize(
                dailyLimitMinutes = settings.dailyScreenTimeMinutes,
                currentMinutesUsed = settings.minutesUsedToday
            )
        }
    }

    fun addBonusScreenTime(minutes: Int) {
        screenTimeManager.addBonusMinutes(minutes)
        parentalSettings.value?.let { current ->
            updateParentalSettings(current.copy(dailyScreenTimeMinutes = current.dailyScreenTimeMinutes + minutes))
        }
    }

    fun approveFriend(requestId: Long) {
        viewModelScope.launch {
            repository.updateFriendRequestStatus(requestId, "approved")
        }
    }

    fun rejectFriend(requestId: Long) {
        viewModelScope.launch {
            repository.updateFriendRequestStatus(requestId, "rejected")
        }
    }

    fun reportPost(postId: Long, content: String, reason: String) {
        viewModelScope.launch {
            val user = currentUser.value
            repository.submitReport(
                postId = postId,
                content = content,
                reporter = user?.name ?: "Concerned Child",
                reason = reason
            )
        }
    }

    fun resolveReport(reportId: Long, deletePost: Boolean) {
        viewModelScope.launch {
            val rep = reports.value.find { it.id == reportId }
            if (deletePost && rep?.reportedPostId != null) {
                repository.deletePost(rep.reportedPostId)
            }
            repository.updateReportStatus(reportId, "resolved")
        }
    }

    fun toggleUserBan(userId: String, isBanned: Boolean) {
        viewModelScope.launch {
            repository.setUserBanned(userId, isBanned)
        }
    }

    fun updateUser(user: UserEntity) {
        viewModelScope.launch {
            repository.saveUser(user)
            AuthManager.initializeSession(user)
        }
    }

    fun loginUser(user: UserEntity) {
        viewModelScope.launch {
            repository.saveUser(user)
            AuthManager.initializeSession(user)
        }
    }

    fun attemptRoleElevation(targetRole: UserRole, pin: String): Boolean {
        val parentPin = parentalSettings.value?.parentPin ?: "1234"
        val success = AuthManager.attemptRoleElevation(targetRole, pin, parentPin)
        if (success) {
            currentUser.value?.let { user ->
                val newRoleStr = targetRole.name.lowercase()
                updateUser(user.copy(role = newRoleStr))
            }
        }
        return success
    }

    fun signOut() {
        AuthManager.signOut()
    }

    override fun onCleared() {
        super.onCleared()
        screenTimeManager.stop()
    }
}
