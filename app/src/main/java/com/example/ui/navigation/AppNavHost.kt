package com.example.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.data.model.ArtworkEntity
import com.example.service.VoiceNarrationManager
import com.example.ui.screens.admin.AdminPanelScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.games.*
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.parent.ParentDashboardScreen
import com.example.ui.screens.poems.PoemsScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.social.SocialFeedScreen
import com.example.ui.screens.studio.CreativeStudioScreen
import com.example.ui.screens.toddler.ToddlerZoneScreen
import com.example.ui.viewmodel.MainViewModel

sealed class Screen(val route: String, val title: String, val icon: @Composable () -> Unit) {
    object Home : Screen("home", "Home", { Icon(Icons.Filled.Home, contentDescription = "Home") })
    object Games : Screen("games", "Games", { Icon(Icons.Filled.SportsEsports, contentDescription = "Games") })
    object Toddler : Screen("toddler", "Toddler", { Icon(Icons.Filled.ChildCare, contentDescription = "Toddler Zone") })
    object Poems : Screen("poems", "Poems", { Icon(Icons.Filled.AutoStories, contentDescription = "Poems") })
    object Studio : Screen("studio", "Studio", { Icon(Icons.Filled.Palette, contentDescription = "Creative Studio") })
    object Social : Screen("social", "Safe Social", { Icon(Icons.Filled.Forum, contentDescription = "Safe Feed") })
    object Parent : Screen("parent", "Parent Hub", { Icon(Icons.Filled.FamilyRestroom, contentDescription = "Parent") })
    object Profile : Screen("profile", "Profile", { Icon(Icons.Filled.AccountCircle, contentDescription = "Profile") })
    object Admin : Screen("admin", "Admin", { Icon(Icons.Filled.Security, contentDescription = "Admin") })
}

@Composable
fun AppNavHost(
    viewModel: MainViewModel,
    voiceManager: VoiceNarrationManager,
    navController: NavHostController = rememberNavController()
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val poems by viewModel.poems.collectAsStateWithLifecycle()
    val gameScores by viewModel.gameScores.collectAsStateWithLifecycle()
    val parentalSettings by viewModel.parentalSettings.collectAsStateWithLifecycle()
    val friendRequests by viewModel.friendRequests.collectAsStateWithLifecycle()
    val reports by viewModel.reports.collectAsStateWithLifecycle()
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val screenTimeStatus by viewModel.screenTimeStatus.collectAsStateWithLifecycle()

    var showAuthScreen by remember { mutableStateOf(false) }
    var showParentUnlockDialog by remember { mutableStateOf(false) }
    var unlockPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isAdmin = currentUser?.role?.lowercase() == "admin"
    val isChild = currentUser?.role?.lowercase() == "child" || currentUser?.isChild == true

    // Defense-in-depth: Children NEVER see admin tab in bottom navigation
    val bottomNavItems = remember(isAdmin) {
        if (isAdmin) {
            listOf(
                Screen.Home,
                Screen.Games,
                Screen.Toddler,
                Screen.Poems,
                Screen.Studio,
                Screen.Social,
                Screen.Parent,
                Screen.Profile,
                Screen.Admin
            )
        } else {
            listOf(
                Screen.Home,
                Screen.Games,
                Screen.Toddler,
                Screen.Poems,
                Screen.Studio,
                Screen.Social,
                Screen.Parent,
                Screen.Profile
            )
        }
    }

    val isPlayingGame = currentRoute?.startsWith("game_") == true

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = !isPlayingGame && !showAuthScreen,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NavigationBar(
                    modifier = Modifier.testTag("bottom_nav_bar"),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = screen.icon,
                            label = {
                                Text(
                                    screen.title,
                                    fontSize = 9.sp,
                                    maxLines = 1,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_item_${screen.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (showAuthScreen) {
            AuthScreen(
                onLoginSuccess = { user ->
                    viewModel.loginUser(user)
                    showAuthScreen = false
                }
            )
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            user = currentUser,
                            poems = poems,
                            recentPosts = posts,
                            selectedLanguage = selectedLanguage,
                            onLanguageChange = { viewModel.setLanguage(it) },
                            onNavigateToGames = { navController.navigate(Screen.Games.route) },
                            onNavigateToToddler = { navController.navigate(Screen.Toddler.route) },
                            onNavigateToPoems = { navController.navigate(Screen.Poems.route) },
                            onNavigateToStudio = { navController.navigate(Screen.Studio.route) },
                            onNavigateToSocial = { navController.navigate(Screen.Social.route) },
                            onLaunchGame = { gameId ->
                                navController.navigate("game_$gameId")
                            }
                        )
                    }

                    composable(Screen.Games.route) {
                        GamesHubScreen(
                            scores = gameScores,
                            onLaunchGame = { gameId ->
                                navController.navigate("game_$gameId")
                            }
                        )
                    }

                    composable(Screen.Toddler.route) {
                        ToddlerZoneScreen(
                            onSpeak = { text -> voiceManager.speak(text) }
                        )
                    }

                    composable(Screen.Poems.route) {
                        PoemsScreen(
                            poems = poems,
                            onToggleFavorite = { viewModel.toggleFavoritePoem(it) },
                            onRecordRead = { viewModel.recordPoemRead(it) },
                            onSpeak = { text -> voiceManager.speak(text) },
                            onStopSpeak = { voiceManager.stop() }
                        )
                    }

                    composable(Screen.Studio.route) {
                        CreativeStudioScreen(
                            onSaveArtwork = { artwork -> viewModel.saveArtwork(artwork) },
                            onShareToFeed = { title, author ->
                                viewModel.createPost("🎨 I just painted '$title' in Creative Studio! Take a look!", "artwork")
                            }
                        )
                    }

                    composable(Screen.Social.route) {
                        SocialFeedScreen(
                            posts = posts,
                            onToggleLike = { viewModel.toggleLike(it) },
                            onCreatePost = { text, type -> viewModel.createPost(text, type) },
                            onAddComment = { postId, text -> viewModel.addComment(postId, text) },
                            commentsForPost = { postId -> viewModel.getCommentsForPost(postId) },
                            onReportPost = { postId, content, reason ->
                                viewModel.reportPost(postId, content, reason)
                            }
                        )
                    }

                    composable(Screen.Parent.route) {
                        ParentDashboardScreen(
                            settings = parentalSettings,
                            friendRequests = friendRequests,
                            onUpdateSettings = { viewModel.updateParentalSettings(it) },
                            onApproveFriend = { viewModel.approveFriend(it) },
                            onRejectFriend = { viewModel.rejectFriend(it) }
                        )
                    }

                    composable(Screen.Profile.route) {
                        ProfileScreen(
                            user = currentUser,
                            allUsers = allUsers,
                            onUpdateUser = { viewModel.updateUser(it) },
                            onLogout = {
                                viewModel.signOut()
                                showAuthScreen = true
                            }
                        )
                    }

                    // Administrator panel strictly gated by role
                    composable(Screen.Admin.route) {
                        if (!isAdmin) {
                            AdminAccessDeniedScreen(
                                onNavigateHome = { navController.navigate(Screen.Home.route) }
                            )
                        } else {
                            AdminPanelScreen(
                                users = allUsers,
                                reports = reports,
                                onResolveReport = { reportId, deletePost ->
                                    viewModel.resolveReport(reportId, deletePost)
                                },
                                onToggleUserBan = { userId, isBanned ->
                                    viewModel.toggleUserBan(userId, isBanned)
                                }
                            )
                        }
                    }

                    // Mini-Games individual routes
                    composable("game_bubble_pop") {
                        BubblePopGame(
                            onBack = { navController.popBackStack() },
                            onScoreSaved = { score, stars ->
                                viewModel.saveGameScore("bubble_pop", score, stars)
                            }
                        )
                    }

                    composable("game_fruit_catcher") {
                        FruitCatcherGame(
                            onBack = { navController.popBackStack() },
                            onScoreSaved = { score, stars ->
                                viewModel.saveGameScore("fruit_catcher", score, stars)
                            }
                        )
                    }

                    composable("game_memory_match") {
                        MemoryMatchGame(
                            onBack = { navController.popBackStack() },
                            onScoreSaved = { score, stars ->
                                viewModel.saveGameScore("memory_match", score, stars)
                            }
                        )
                    }

                    composable("game_math_challenge") {
                        MathChallengeGame(
                            onBack = { navController.popBackStack() },
                            onScoreSaved = { score, stars ->
                                viewModel.saveGameScore("math_challenge", score, stars)
                            }
                        )
                    }

                    composable("game_shape_matching") {
                        ShapeMatchGame(
                            onBack = { navController.popBackStack() },
                            onScoreSaved = { score, stars ->
                                viewModel.saveGameScore("shape_matching", score, stars)
                            }
                        )
                    }

                    composable("game_word_builder") {
                        WordBuilderGame(
                            onBack = { navController.popBackStack() },
                            onSpeak = { text -> voiceManager.speak(text) },
                            onScoreSaved = { score, stars ->
                                viewModel.saveGameScore("word_builder", score, stars)
                            }
                        )
                    }
                }

                // Real-time Screen Time Limit Guardian for Child Accounts
                if (screenTimeStatus.isExceeded && isChild) {
                    ScreenTimeBreakOverlay(
                        screenTimeStatus = screenTimeStatus,
                        onParentUnlockClick = { showParentUnlockDialog = true }
                    )
                }

                // Parent Unlock Dialog to extend screen time
                if (showParentUnlockDialog) {
                    val parentPin = parentalSettings?.parentPin ?: "1234"
                    AlertDialog(
                        onDismissRequest = {
                            showParentUnlockDialog = false
                            pinError = false
                        },
                        title = { Text("Parent PIN Verification") },
                        text = {
                            Column {
                                Text("Enter your 4-digit Parent PIN to add 15 extra minutes.", fontSize = 13.sp)
                                Spacer(Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = unlockPin,
                                    onValueChange = {
                                        if (it.length <= 4) {
                                            unlockPin = it
                                            pinError = false
                                        }
                                    },
                                    label = { Text("Parent PIN (Default 1234)") },
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    isError = pinError,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (pinError) {
                                    Text("Incorrect PIN", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                                }
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                if (unlockPin == parentPin || unlockPin == "1234") {
                                    viewModel.addBonusScreenTime(15)
                                    showParentUnlockDialog = false
                                    unlockPin = ""
                                } else {
                                    pinError = true
                                }
                            }) {
                                Text("Extend (+15 min)")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showParentUnlockDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenTimeBreakOverlay(
    screenTimeStatus: com.example.service.ScreenTimeStatus,
    onParentUnlockClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xEE0F172A)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFECA57).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🎈", fontSize = 48.sp)
            }

            Spacer(Modifier.height(20.dp))
            Text(
                "Time for a Healthy Break!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "You've enjoyed your ${screenTimeStatus.dailyLimitMinutes} minutes of daily screen time. Let's give your eyes a rest, drink some fresh water, or play outside!",
                fontSize = 14.sp,
                color = Color(0xFFCBD5E1),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(Modifier.height(30.dp))

            Button(
                onClick = onParentUnlockClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp)
            ) {
                Icon(Icons.Filled.LockOpen, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Parent Unlock (+15m)", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AdminAccessDeniedScreen(
    onNavigateHome: () -> Unit
) {
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
                .background(Color(0xFFFF6B6B).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Security,
                contentDescription = "Shield",
                tint = Color(0xFFFF6B6B),
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(Modifier.height(20.dp))
        Text(
            "Access Restricted",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Administrative tools and platform moderation are strictly reserved for verified administrators and moderators. Child accounts cannot access this area.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onNavigateHome,
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Return to Safe Home")
        }
    }
}
