package com.example.ui.navigation

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.R
import com.example.data.mock.MockChannelData
import com.example.data.model.Announcement
import com.example.data.model.CommunityPost
import com.example.data.model.StorySeries
import com.example.data.model.UserProfile
import com.example.data.model.VideoNote
import com.example.data.model.VideoRequest
import com.example.data.repository.FirestoreRepository
import com.example.data.repository.PreferencesRepository
import com.example.data.repository.YouTubeRepository
import com.example.i18n.stringsFor
import com.example.ui.auth.GoogleAuthHelper
import com.example.ui.components.ConfettiCelebrationOverlay
import com.example.ui.components.GlassCard
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.QuickActionFabMenu
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.AnimatedSplashScreen
import com.example.ui.screens.CommunityScreen
import com.example.ui.screens.CreatorToolsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.OnboardingAndAuthScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.QuizLearningScreen
import com.example.ui.screens.ResourcesHubScreen
import com.example.ui.screens.StoriesScreen
import com.example.ui.screens.VideosScreen
import com.example.ui.theme.KabirTechLabTheme
import com.example.ui.viewmodel.CloudSyncViewModel
import com.example.ui.viewmodel.MainAppViewModel
import com.example.ui.viewmodel.UiState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

object Routes {
    const val HOME = "home"
    const val VIDEOS = "videos"
    const val STORIES = "stories"
    const val COMMUNITY = "community"
    const val PROFILE = "profile"
    const val AI = "ai"
    const val TOOLS = "tools"
    const val QUIZ = "quiz"
    const val RESOURCES = "resources"
    const val LIBRARY = "library"
    const val ADMIN = "admin"
    const val SEARCH = "search"
    const val NOTIFICATIONS = "notifications"
}

private fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth -> trySend(auth.currentUser) }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

@Composable
fun KabirTechLabRoot(auth: FirebaseAuth = Firebase.auth) {
    val context = LocalContext.current
    val mainViewModel: MainAppViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as Application
                MainAppViewModel(PreferencesRepository(app))
            }
        }
    )

    val prefs by mainViewModel.preferences.collectAsStateWithLifecycle()
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    var showSplash by remember { mutableStateOf(true) }

    KabirTechLabTheme(
        themeMode = prefs.themeMode,
        accentKey = prefs.accentKey,
        seasonalKey = prefs.seasonalKey,
        fontScale = prefs.fontScale,
        highContrast = prefs.highContrast,
    ) {
        if (showSplash) {
            AnimatedSplashScreen(
                language = prefs.language,
                onFinished = { showSplash = false }
            )
        } else if (currentUser == null && !prefs.offlinePreviewMode) {
            OnboardingAndAuthScreen(
                initialLanguage = prefs.language,
                onboardingAlreadyCompleted = prefs.onboardingCompleted,
                onLanguageChange = { mainViewModel.setLanguage(it) },
                onAuthSuccess = { lang, cats ->
                    mainViewModel.completeOnboarding(lang, cats, offlinePreview = false)
                },
                onContinueOfflinePreview = { lang, cats ->
                    mainViewModel.completeOnboarding(lang, cats, offlinePreview = true)
                }
            )
        } else if (currentUser != null) {
            val user = currentUser!!
            key(user.uid) {
                val cloudVm: CloudSyncViewModel = viewModel(
                    key = user.uid,
                    factory = viewModelFactory {
                        initializer {
                            val app = this[APPLICATION_KEY] as Application
                            val dbId = app.getString(R.string.firestore_database_id)
                            val db = FirebaseFirestore.getInstance(dbId)
                            CloudSyncViewModel(
                                repository = FirestoreRepository(db),
                                currentUserId = user.uid,
                                initialDisplayName = user.displayName ?: "Kabir Tech Explorer",
                                initialLanguage = prefs.language,
                                initialThemeMode = prefs.themeMode,
                            )
                        }
                    }
                )
                AuthenticatedOrPreviewShell(
                    mainVm = mainViewModel,
                    cloudVm = cloudVm,
                    currentUser = user,
                    onRequestSignIn = {
                        mainViewModel.setOfflinePreviewMode(false)
                    }
                )
            }
        } else {
            // Pure offline / guest preview mode: zero Firestore queries
            AuthenticatedOrPreviewShell(
                mainVm = mainViewModel,
                cloudVm = null,
                currentUser = null,
                onRequestSignIn = {
                    mainViewModel.setOfflinePreviewMode(false)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthenticatedOrPreviewShell(
    mainVm: MainAppViewModel,
    cloudVm: CloudSyncViewModel?,
    currentUser: FirebaseUser?,
    onRequestSignIn: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val prefs by mainVm.preferences.collectAsStateWithLifecycle()
    val videos by mainVm.videos.collectAsStateWithLifecycle()
    val stories by mainVm.stories.collectAsStateWithLifecycle()
    val isRefreshing by mainVm.isRefreshingVideos.collectAsStateWithLifecycle()
    val activePlayer by mainVm.activePlayer.collectAsStateWithLifecycle()
    val chatHistory by mainVm.chatHistory.collectAsStateWithLifecycle()
    val aiState by mainVm.aiState.collectAsStateWithLifecycle()
    val summaryMap by mainVm.videoSummaryMap.collectAsStateWithLifecycle()
    val summaryLoadingId by mainVm.summaryLoadingId.collectAsStateWithLifecycle()
    val showConfetti by mainVm.showConfetti.collectAsStateWithLifecycle()
    val adsEnabled by mainVm.adsEnabled.collectAsStateWithLifecycle()
    val maintenanceMode by mainVm.maintenanceMode.collectAsStateWithLifecycle()
    val pollVotes by mainVm.pollVotes.collectAsStateWithLifecycle()

    val localNotes by mainVm.localNotes.collectAsStateWithLifecycle()
    val localPosts by mainVm.localPosts.collectAsStateWithLifecycle()
    val localRequests by mainVm.localRequests.collectAsStateWithLifecycle()
    val localAnnouncements by mainVm.localAnnouncements.collectAsStateWithLifecycle()

    // Cloud-backed states when authenticated
    val cloudProfileState = cloudVm?.userProfile?.collectAsStateWithLifecycle()?.value
    val cloudNotesState = cloudVm?.notes?.collectAsStateWithLifecycle()?.value
    val cloudPlaylistsState = cloudVm?.playlists?.collectAsStateWithLifecycle()?.value
    val cloudPostsState = cloudVm?.communityPosts?.collectAsStateWithLifecycle()?.value
    val cloudRequestsState = cloudVm?.videoRequests?.collectAsStateWithLifecycle()?.value
    val cloudAnnouncementsState = cloudVm?.announcements?.collectAsStateWithLifecycle()?.value
    val cloudMessage = cloudVm?.actionMessage?.collectAsStateWithLifecycle()?.value

    LaunchedEffect(cloudMessage) {
        if (cloudMessage != null) {
            snackbarHostState.showSnackbar(cloudMessage)
            cloudVm.clearMessage()
        }
    }

    val userProfile: UserProfile? = (cloudProfileState as? UiState.Success)?.data
    val notes: List<VideoNote> = ((cloudNotesState as? UiState.Success)?.data ?: emptyList()) + localNotes
    val playlists = (cloudPlaylistsState as? UiState.Success)?.data ?: emptyList()
    val communityPosts: List<CommunityPost> =
        ((cloudPostsState as? UiState.Success)?.data.orEmpty() + localPosts).distinctBy { it.id }
    val videoRequests: List<VideoRequest> =
        ((cloudRequestsState as? UiState.Success)?.data.orEmpty() + localRequests).distinctBy { it.id }
    val announcements: List<Announcement> =
        ((cloudAnnouncementsState as? UiState.Success)?.data.orEmpty() + localAnnouncements).distinctBy { it.id }

    var fabExpanded by remember { mutableStateOf(false) }
    var videoInitialCategory by remember { mutableStateOf("All") }
    var selectedStoryForDetail by remember { mutableStateOf<StorySeries?>(null) }
    var aiInitialPrompt by remember { mutableStateOf("") }
    var aiVideoContext by remember { mutableStateOf<String?>(null) }

    val strings = stringsFor(prefs.language)
    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStack?.destination?.route ?: Routes.HOME

    val topLevelRoutes = setOf(Routes.HOME, Routes.VIDEOS, Routes.STORIES, Routes.COMMUNITY, Routes.PROFILE)

    fun showToast(msg: String) {
        scope.launch { snackbarHostState.showSnackbar(msg) }
    }

    fun navigateTopTab(route: String) {
        fabExpanded = false
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    if (maintenanceMode && currentUser?.email?.lowercase() !in com.example.config.AppConfig.ADMIN_EMAILS) {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            GlassCard(highlightBorder = true) {
                Text("Scheduled Maintenance", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Kabir Tech Lab is upgrading servers. Back shortly!")
            }
        }
        return
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 600.dp

        Row(modifier = Modifier.fillMaxSize()) {
            if (useRail && currentRoute in topLevelRoutes) {
                NavigationRail {
                    listOf(
                        Triple(Routes.HOME, strings.navHome, Icons.Filled.Home to Icons.Outlined.Home),
                        Triple(Routes.VIDEOS, strings.navVideos, Icons.Filled.PlayCircle to Icons.Outlined.PlayCircle),
                        Triple(Routes.STORIES, strings.navStories, Icons.Filled.MenuBook to Icons.Outlined.MenuBook),
                        Triple(Routes.COMMUNITY, strings.navCommunity, Icons.Filled.Groups to Icons.Outlined.Groups),
                        Triple(Routes.PROFILE, strings.navProfile, Icons.Filled.Person to Icons.Outlined.Person),
                    ).forEach { (route, label, icons) ->
                        val selected = currentRoute == route
                        NavigationRailItem(
                            selected = selected,
                            onClick = { navigateTopTab(route) },
                            icon = { Icon(if (selected) icons.first else icons.second, label) },
                            label = { Text(label) },
                        )
                    }
                }
            }

            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                contentWindowInsets = WindowInsets.navigationBars,
                floatingActionButton = {
                    if (currentRoute in topLevelRoutes) {
                        QuickActionFabMenu(
                            expanded = fabExpanded,
                            language = prefs.language,
                            onToggle = { fabExpanded = !fabExpanded },
                            onOpenAi = {
                                fabExpanded = false
                                aiInitialPrompt = ""
                                aiVideoContext = null
                                navController.navigate(Routes.AI)
                            },
                            onOpenSearch = {
                                fabExpanded = false
                                navController.navigate(Routes.SEARCH)
                            },
                            onOpenTools = {
                                fabExpanded = false
                                navController.navigate(Routes.TOOLS)
                            },
                            onOpenQuiz = {
                                fabExpanded = false
                                navController.navigate(Routes.QUIZ)
                            },
                        )
                    }
                },
                bottomBar = {
                    Column {
                        activePlayer?.let { player ->
                            MiniPlayerBar(
                                player = player,
                                language = prefs.language,
                                onExpand = {
                                    if (player.storySeriesId != null) navigateTopTab(Routes.STORIES)
                                    else navigateTopTab(Routes.VIDEOS)
                                },
                                onTogglePlay = { mainVm.updatePlayer { it.copy(playing = !it.playing) } },
                                onClose = { mainVm.closePlayer() },
                            )
                        }
                        if (!useRail && currentRoute in topLevelRoutes) {
                            NavigationBar {
                                listOf(
                                    Triple(Routes.HOME, strings.navHome, Icons.Filled.Home to Icons.Outlined.Home),
                                    Triple(Routes.VIDEOS, strings.navVideos, Icons.Filled.PlayCircle to Icons.Outlined.PlayCircle),
                                    Triple(Routes.STORIES, strings.navStories, Icons.Filled.MenuBook to Icons.Outlined.MenuBook),
                                    Triple(Routes.COMMUNITY, strings.navCommunity, Icons.Filled.Groups to Icons.Outlined.Groups),
                                    Triple(Routes.PROFILE, strings.navProfile, Icons.Filled.Person to Icons.Outlined.Person),
                                ).forEach { (route, label, icons) ->
                                    val selected = currentRoute == route
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = { navigateTopTab(route) },
                                        icon = { Icon(if (selected) icons.first else icons.second, label) },
                                        label = { Text(label, maxLines = 1) },
                                        modifier = Modifier.testTag("nav_$route"),
                                    )
                                }
                            }
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Routes.HOME,
                    modifier = Modifier.padding(innerPadding),
                ) {
                    composable(Routes.HOME) {
                        HomeScreen(
                            prefs = prefs,
                            videos = videos,
                            stories = stories,
                            announcements = announcements,
                            isRefreshing = isRefreshing,
                            isOfflineMode = currentUser == null,
                            onRefresh = { mainVm.refreshVideos(force = true) },
                            onToggleLanguage = { mainVm.toggleLanguage() },
                            onOpenSearch = { navController.navigate(Routes.SEARCH) },
                            onOpenNotifications = { navController.navigate(Routes.NOTIFICATIONS) },
                            onOpenVideo = { vid ->
                                mainVm.playVideo(vid)
                                navigateTopTab(Routes.VIDEOS)
                            },
                            onOpenStory = { ser ->
                                selectedStoryForDetail = ser
                                navigateTopTab(Routes.STORIES)
                            },
                            onOpenAiWithPrompt = { prompt ->
                                aiInitialPrompt = prompt
                                aiVideoContext = null
                                navController.navigate(Routes.AI)
                            },
                            onOpenTools = { navController.navigate(Routes.RESOURCES) },
                            onClaimCheckIn = {
                                mainVm.claimDailyCheckIn { claimed, surprise ->
                                    if (claimed) {
                                        val bonus = if (surprise) 75L else 25L
                                        cloudVm?.syncXpAndQuiz(bonus)
                                        showToast(if (surprise) "Surprise 7-Day Bonus +75 XP!" else "+25 XP Claimed!")
                                    }
                                }
                            },
                            onToggleSection = { mainVm.toggleHomeSection(it) },
                            onMoveSection = { k, d -> mainVm.moveHomeSection(k, d) },
                            onSelectCategoryNavigate = { cat ->
                                videoInitialCategory = cat
                                navigateTopTab(Routes.VIDEOS)
                            },
                            onShowMessage = ::showToast,
                        )
                    }

                    composable(Routes.VIDEOS) {
                        VideosScreen(
                            prefs = prefs,
                            videos = videos,
                            initialCategory = videoInitialCategory,
                            activePlayer = activePlayer,
                            userPlaylists = playlists,
                            videoSummaryMap = summaryMap,
                            summaryLoadingId = summaryLoadingId,
                            onPlayVideo = { v, pos -> mainVm.playVideo(v, pos) },
                            onUpdatePlayer = { mainVm.updatePlayer(it) },
                            onToggleSave = { id ->
                                mainVm.toggleSaved(id)
                                cloudVm?.syncMediaProgress(id, "VIDEO", 0L, 600L, id !in prefs.savedIds, id in prefs.likedIds, false)
                            },
                            onToggleLike = { id ->
                                mainVm.toggleLiked(id)
                                cloudVm?.syncMediaProgress(id, "VIDEO", 0L, 600L, id in prefs.savedIds, id !in prefs.likedIds, false)
                            },
                            onDownloadResource = { mainVm.markDownloaded(it) },
                            onAddNote = { vidId, title, ts, content ->
                                if (cloudVm != null) cloudVm.addNote(vidId, title, ts, content)
                                else mainVm.addLocalNote(vidId, title, ts, content)
                            },
                            onCreatePlaylist = { name, vidId ->
                                if (cloudVm != null) cloudVm.createPlaylist(name, vidId)
                                else showToast("Playlist '$name' saved locally. Sign in to sync!")
                            },
                            onGenerateAiSummary = { mainVm.generateVideoSummary(it) },
                            onAskAiAboutVideo = { vid ->
                                aiInitialPrompt = ""
                                aiVideoContext = "${vid.titleEn}\n${vid.descriptionEn}"
                                navController.navigate(Routes.AI)
                            },
                            onShowMessage = ::showToast,
                        )
                    }

                    composable(Routes.STORIES) {
                        StoriesScreen(
                            prefs = prefs,
                            stories = stories,
                            initialSelectedSeries = selectedStoryForDetail,
                            activePlayer = activePlayer,
                            onPlayStoryPart = { ser, part, audio -> mainVm.playStoryPart(ser, part, audio) },
                            onUpdatePlayer = { mainVm.updatePlayer(it) },
                            onToggleSave = { mainVm.toggleSaved(it) },
                            onShowMessage = ::showToast,
                        )
                    }

                    composable(Routes.COMMUNITY) {
                        CommunityScreen(
                            prefs = prefs,
                            isAuthenticated = currentUser != null,
                            posts = communityPosts,
                            videoRequests = videoRequests,
                            pollVotes = pollVotes,
                            onVotePoll = { pId, oId -> mainVm.votePoll(pId, oId) },
                            onCreatePost = { type, title, body ->
                                val author = userProfile?.displayName ?: currentUser?.displayName ?: "Guest"
                                if (cloudVm != null) cloudVm.createPost(author, type, title, body)
                                else mainVm.addLocalPost(author, type, title, body)
                            },
                            onLikePost = { id ->
                                if (cloudVm != null && !id.startsWith("post_demo") && !id.startsWith("local_")) {
                                    cloudVm.likePost(id)
                                } else {
                                    showToast("Liked!")
                                }
                            },
                            onCreateVideoRequest = { title, cat, desc ->
                                val author = userProfile?.displayName ?: currentUser?.displayName ?: "Guest"
                                if (cloudVm != null) cloudVm.createVideoRequest(author, title, cat, desc)
                                else mainVm.addLocalRequest(author, title, cat, desc)
                            },
                            onUpvoteRequest = { id ->
                                if (cloudVm != null && !id.startsWith("req_demo") && !id.startsWith("local_")) {
                                    cloudVm.upvoteRequest(id)
                                } else {
                                    showToast("Upvoted!")
                                }
                            },
                            onSubmitJoinForm = { name, contact, device, interests ->
                                cloudVm?.submitJoinForm(name, contact, device, interests)
                            },
                            onRequestSignIn = onRequestSignIn,
                            onShowMessage = ::showToast,
                        )
                    }

                    composable(Routes.PROFILE) {
                        ProfileScreen(
                            prefs = prefs,
                            userProfile = userProfile,
                            userEmail = currentUser?.email,
                            isAuthenticated = currentUser != null,
                            onOpenLibrary = { navController.navigate(Routes.LIBRARY) },
                            onOpenResources = { navController.navigate(Routes.RESOURCES) },
                            onOpenAi = {
                                aiInitialPrompt = ""
                                aiVideoContext = null
                                navController.navigate(Routes.AI)
                            },
                            onOpenCreatorTools = { navController.navigate(Routes.TOOLS) },
                            onOpenQuiz = { navController.navigate(Routes.QUIZ) },
                            onOpenAdmin = { navController.navigate(Routes.ADMIN) },
                            onSignInClick = onRequestSignIn,
                            onSignOutClick = {
                                GoogleAuthHelper.signOut(credentialManager, scope) {
                                    mainVm.setOfflinePreviewMode(false)
                                }
                            },
                            onSetLanguage = { mainVm.setLanguage(it) },
                            onSetBanglaNumerals = { mainVm.setBanglaNumerals(it) },
                            onUpdateTheme = { tm, ac, st, fs, hc, rm -> mainVm.updateTheme(tm, ac, st, fs, hc, rm) },
                            onUpdatePlayback = { ds, ap, wf, pin -> mainVm.updatePlaybackSettings(ds, ap, wf, pin) },
                            onUpdateNotifications = { en, qh, dt, tp -> mainVm.updateNotificationPrefs(en, qh, dt, tp) },
                            onShowMessage = ::showToast,
                        )
                    }

                    composable(Routes.AI) {
                        AiAssistantScreen(
                            prefs = prefs,
                            chatHistory = chatHistory,
                            aiState = aiState,
                            initialPrompt = aiInitialPrompt,
                            videoContext = aiVideoContext,
                            onSendMessage = { p, ctx -> mainVm.sendAiMessage(p, ctx) },
                            onBack = { navController.popBackStack() },
                            onShowMessage = ::showToast,
                        )
                    }

                    composable(Routes.TOOLS) {
                        CreatorToolsScreen(
                            prefs = prefs,
                            onGenerateAi = { mainVm.generateCreatorToolAi(it) },
                            onBack = { navController.popBackStack() },
                            onShowMessage = ::showToast,
                        )
                    }

                    composable(Routes.QUIZ) {
                        QuizLearningScreen(
                            prefs = prefs,
                            onQuizCompleted = { bonus ->
                                mainVm.addXp(bonus)
                                if (bonus > 0) mainVm.triggerConfetti()
                                cloudVm?.syncXpAndQuiz(bonus.toLong(), 1L)
                            },
                            onToggleFlashcardMastered = { mainVm.toggleFlashcardMastered(it) },
                            onBack = { navController.popBackStack() },
                        )
                    }

                    composable(Routes.RESOURCES) {
                        ResourcesHubScreen(
                            prefs = prefs,
                            onToggleLesson = { mainVm.toggleLessonComplete(it) },
                            onBack = { navController.popBackStack() },
                            onShowMessage = ::showToast,
                        )
                    }

                    composable(Routes.LIBRARY) {
                        LibraryScreen(
                            prefs = prefs,
                            videos = videos,
                            notes = notes,
                            onDeleteNote = { cloudVm?.deleteNote(it) },
                            onOpenVideo = {
                                mainVm.playVideo(it)
                                navigateTopTab(Routes.VIDEOS)
                            },
                            onBack = { navController.popBackStack() },
                        )
                    }

                    composable(Routes.ADMIN) {
                        AdminPanelScreen(
                            prefs = prefs,
                            adsEnabled = adsEnabled,
                            maintenanceMode = maintenanceMode,
                            onToggleAds = { mainVm.toggleAdsEnabled() },
                            onToggleMaintenance = { mainVm.toggleMaintenanceMode() },
                            onPublishAnnouncement = { tb, te, bb, be ->
                                cloudVm?.publishAnnouncement(tb, te, bb, be)
                            },
                            onBack = { navController.popBackStack() },
                        )
                    }

                    composable(Routes.SEARCH) {
                        SearchDiscoveryScreen(
                            prefs = prefs,
                            videos = videos,
                            stories = stories,
                            onSearchSubmit = { mainVm.addRecentSearch(it) },
                            onOpenVideo = {
                                mainVm.playVideo(it)
                                navigateTopTab(Routes.VIDEOS)
                            },
                            onOpenStory = {
                                selectedStoryForDetail = it
                                navigateTopTab(Routes.STORIES)
                            },
                            onBack = { navController.popBackStack() },
                        )
                    }

                    composable(Routes.NOTIFICATIONS) {
                        NotificationsCenterScreen(
                            language = prefs.language,
                            onSelectRoute = { target -> navController.navigate(target) },
                            onBack = { navController.popBackStack() },
                        )
                    }
                }
            }
        }

        ConfettiCelebrationOverlay(visible = showConfetti)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchDiscoveryScreen(
    prefs: com.example.data.repository.UserPreferences,
    videos: List<com.example.data.model.VideoItem>,
    stories: List<StorySeries>,
    onSearchSubmit: (String) -> Unit,
    onOpenVideo: (com.example.data.model.VideoItem) -> Unit,
    onOpenStory: (StorySeries) -> Unit,
    onBack: () -> Unit,
) {
    val lang = prefs.language
    val strings = stringsFor(lang)
    var query by remember { mutableStateOf("") }
    val trendingQueries = listOf("premer golpo", "Gemini AI", "Android Compose", "Bangla story")

    val matchedVideos = remember(query, videos) {
        videos.filter {
            YouTubeRepository.matchesSmartQuery(query, it.titleBn, it.titleEn, it.descriptionBn, it.tags.joinToString(" "))
        }
    }
    val matchedStories = remember(query, stories) {
        stories.filter {
            YouTubeRepository.matchesSmartQuery(query, it.titleBn, it.titleEn, it.synopsisBn, it.genre)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search & Discovery") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                },
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text(strings.searchHint) },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onSearchSubmit(query) }) { Text("Go") }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    trendingQueries.take(3).forEach { tq ->
                        FilterChip(selected = query == tq, onClick = { query = tq }, label = { Text(tq) })
                    }
                }
            }

            if (matchedStories.isNotEmpty()) {
                item { Text(strings.storiesHeader, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                items(matchedStories, key = { it.id }) { ser ->
                    GlassCard(onClick = { onOpenStory(ser) }) {
                        Text(if (lang == "en") ser.titleEn else ser.titleBn, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            item { Text(strings.videosTabAll, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
            items(matchedVideos, key = { it.id }) { vid ->
                GlassCard(onClick = { onOpenVideo(vid) }) {
                    Text(if (lang == "en") vid.titleEn else vid.titleBn, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationsCenterScreen(
    language: String,
    onSelectRoute: (String) -> Unit,
    onBack: () -> Unit,
) {
    val strings = stringsFor(language)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.notificationsTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                },
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(MockChannelData.sampleNotifications, key = { it.id }) { notif ->
                GlassCard(onClick = { onSelectRoute(notif.targetRoute) }, highlightBorder = notif.unread) {
                    Text(
                        text = if (language == "en") notif.titleEn else notif.titleBn,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = if (language == "en") notif.bodyEn else notif.bodyBn,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(notif.timeLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
