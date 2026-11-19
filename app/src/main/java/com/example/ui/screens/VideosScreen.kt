package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.mock.MockChannelData
import com.example.data.model.UserPlaylist
import com.example.data.model.VideoAiSummary
import com.example.data.model.VideoItem
import com.example.data.repository.UserPreferences
import com.example.data.repository.YouTubeRepository
import com.example.i18n.formatNumber
import com.example.i18n.stringsFor
import com.example.ui.components.GlassCard
import com.example.ui.components.VideoThumbnailImage
import com.example.ui.components.formatSeconds
import com.example.ui.viewmodel.ActivePlayerState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VideosScreen(
    prefs: UserPreferences,
    videos: List<VideoItem>,
    initialCategory: String,
    activePlayer: ActivePlayerState?,
    userPlaylists: List<UserPlaylist>,
    videoSummaryMap: Map<String, Result<VideoAiSummary>>,
    summaryLoadingId: String?,
    onPlayVideo: (VideoItem, Int) -> Unit,
    onUpdatePlayer: ((ActivePlayerState) -> ActivePlayerState) -> Unit,
    onToggleSave: (String) -> Unit,
    onToggleLike: (String) -> Unit,
    onDownloadResource: (String) -> Unit,
    onAddNote: (String, String, Long, String) -> Unit,
    onCreatePlaylist: (String, String?) -> Unit,
    onGenerateAiSummary: (VideoItem) -> Unit,
    onAskAiAboutVideo: (VideoItem) -> Unit,
    onShowMessage: (String) -> Unit,
) {
    val lang = prefs.language
    val strings = stringsFor(lang)
    var selectedTab by remember { mutableIntStateOf(0) } // 0=All, 1=Shorts, 2=Playlists
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember(initialCategory) { mutableStateOf(initialCategory) }
    var sortMode by remember { mutableStateOf("Newest") } // Newest, Oldest, Most Viewed, Most Liked
    var durationFilter by remember { mutableStateOf("All") } // All, Short, Medium, Long
    var isGridView by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var openedVideoDetail by remember { mutableStateOf<VideoItem?>(null) }

    val filteredVideos = remember(videos, searchQuery, selectedCategory, sortMode, durationFilter) {
        videos.filter { vid ->
            val catMatch = selectedCategory == "All" || vid.category == selectedCategory
            val durMatch = when (durationFilter) {
                "Short" -> vid.durationSeconds < 240
                "Medium" -> vid.durationSeconds in 240..1200
                "Long" -> vid.durationSeconds > 1200
                else -> true
            }
            val queryMatch = YouTubeRepository.matchesSmartQuery(
                searchQuery,
                vid.titleBn,
                vid.titleEn,
                vid.descriptionBn,
                vid.descriptionEn,
                vid.category,
                vid.tags.joinToString(" "),
            )
            catMatch && durMatch && queryMatch
        }.let { list ->
            when (sortMode) {
                "Oldest" -> list.sortedByDescending { it.uploadDateDaysAgo }
                "Most Viewed" -> list.sortedByDescending { it.viewsCount }
                "Most Liked" -> list.sortedByDescending { it.likesCount }
                else -> list.sortedBy { it.uploadDateDaysAgo }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text(strings.videosTabAll) })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text(strings.videosTabShorts) })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text(strings.videosTabPlaylists) })
        }

        when (selectedTab) {
            0 -> {
                // Search + View Toggle + Filter Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text(strings.searchPlaceholder, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("videos_search_input"),
                    )
                    IconButton(onClick = { isGridView = !isGridView }, modifier = Modifier.testTag("videos_grid_toggle")) {
                        Icon(if (isGridView) Icons.Default.ViewList else Icons.Default.GridView, "Toggle layout")
                    }
                    IconButton(onClick = { showFilterSheet = true }, modifier = Modifier.testTag("videos_filter_btn")) {
                        Icon(Icons.Default.FilterList, strings.filterAndSort)
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(MockChannelData.categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))

                if (isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(filteredVideos, key = { it.id }) { vid ->
                            GlassCard(onClick = {
                                onPlayVideo(vid, 0)
                                openedVideoDetail = vid
                            }) {
                                VideoThumbnailImage(
                                    vid,
                                    Modifier.fillMaxWidth().height(90.dp).clip(RoundedCornerShape(12.dp)),
                                    vid.titleEn,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (lang == "en") vid.titleEn else vid.titleBn,
                                    style = MaterialTheme.typography.labelLarge,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = vid.durationLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(filteredVideos, key = { it.id }) { vid ->
                            GlassCard(
                                onClick = {
                                    onPlayVideo(vid, 0)
                                    openedVideoDetail = vid
                                },
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(120.dp, 76.dp)
                                            .clip(RoundedCornerShape(14.dp)),
                                    ) {
                                        VideoThumbnailImage(vid, Modifier.fillMaxSize(), vid.titleEn)
                                        Surface(
                                            color = Color.Black.copy(alpha = 0.72f),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp),
                                        ) {
                                            Text(
                                                text = vid.durationLabel,
                                                color = Color.White,
                                                style = MaterialTheme.typography.labelSmall,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (lang == "en") vid.titleEn else vid.titleBn,
                                            style = MaterialTheme.typography.titleMedium,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${vid.category} • ${formatNumber(vid.viewsCount, prefs.useBanglaNumerals)} views",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                val shorts = videos.filter { it.shortVideo || it.category == "Shorts" }
                val pagerState = rememberPagerState(pageCount = { shorts.size.coerceAtLeast(1) })
                VerticalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                ) { idx ->
                    val vid = shorts.getOrNull(idx)
                    if (vid != null) {
                        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            VideoThumbnailImage(
                                video = vid,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(28.dp)),
                                contentDescription = vid.titleEn,
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(28.dp))
                                    .background(Color.Black.copy(alpha = 0.42f))
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(20.dp),
                            ) {
                                Text(
                                    text = if (lang == "en") vid.titleEn else vid.titleBn,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Button(onClick = {
                                        onPlayVideo(vid, 0)
                                        openedVideoDetail = vid
                                    }) {
                                        Icon(Icons.Default.PlayArrow, null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (lang == "en") "Watch Short" else "শর্ট দেখুন")
                                    }
                                    OutlinedButton(onClick = { onToggleLike(vid.id) }) {
                                        Icon(Icons.Default.ThumbUp, null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(formatNumber(vid.likesCount, prefs.useBanglaNumerals), color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                var newPlaylistName by remember { mutableStateOf("") }
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        GlassCard {
                            Text(
                                text = if (lang == "en") "Create Custom Playlist" else "নতুন প্লেলিস্ট তৈরি করুন",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = newPlaylistName,
                                    onValueChange = { newPlaylistName = it },
                                    placeholder = { Text(if (lang == "en") "Playlist name..." else "প্লেলিস্টের নাম...") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(onClick = {
                                    if (newPlaylistName.isNotBlank()) {
                                        onCreatePlaylist(newPlaylistName, null)
                                        newPlaylistName = ""
                                    }
                                }) {
                                    Icon(Icons.Default.Add, null)
                                }
                            }
                        }
                    }
                    if (userPlaylists.isNotEmpty()) {
                        items(userPlaylists, key = { it.id }) { pl ->
                            GlassCard {
                                Text(pl.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${pl.videoIds.size} videos",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                    items(MockChannelData.curatedPlaylists, key = { it.id }) { pl ->
                        GlassCard(onClick = {
                            val firstVid = videos.firstOrNull { it.id in pl.videoIds } ?: videos.first()
                            onPlayVideo(firstVid, 0)
                            openedVideoDetail = firstVid
                        }) {
                            Text(
                                text = if (lang == "en") pl.titleEn else pl.titleBn,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = if (lang == "en") pl.descriptionEn else pl.descriptionBn,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(progress = { 0.35f }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${pl.videoIds.size} videos • 35% complete",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(onDismissRequest = { showFilterSheet = false }) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(strings.filterAndSort, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Sort", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Newest", "Oldest", "Most Viewed", "Most Liked").forEach { mode ->
                        FilterChip(selected = sortMode == mode, onClick = { sortMode = mode }, label = { Text(mode) })
                    }
                }
                Text("Duration", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All", "Short", "Medium", "Long").forEach { dur ->
                        FilterChip(selected = durationFilter == dur, onClick = { durationFilter = dur }, label = { Text(dur) })
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    openedVideoDetail?.let { video ->
        VideoDetailPlayerSheet(
            video = video,
            prefs = prefs,
            player = activePlayer,
            summaryResult = videoSummaryMap[video.id],
            isLoadingSummary = summaryLoadingId == video.id,
            onDismiss = { openedVideoDetail = null },
            onUpdatePlayer = onUpdatePlayer,
            onToggleSave = { onToggleSave(video.id) },
            onToggleLike = { onToggleLike(video.id) },
            onDownloadResource = { resId ->
                onDownloadResource(resId)
                onShowMessage(if (lang == "en") "Saved for offline reading!" else "অফলাইন ব্যবহারের জন্য ডাউনলোড হয়েছে!")
            },
            onAddNote = { ts, note ->
                onAddNote(video.id, if (lang == "en") video.titleEn else video.titleBn, ts, note)
                onShowMessage(if (lang == "en") "Timestamp note saved!" else "টাইমস্ট্যাম্প নোট সেভ হয়েছে!")
            },
            onCreatePlaylist = { name -> onCreatePlaylist(name, video.id) },
            onGenerateAiSummary = { onGenerateAiSummary(video) },
            onAskAi = { onAskAiAboutVideo(video) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VideoDetailPlayerSheet(
    video: VideoItem,
    prefs: UserPreferences,
    player: ActivePlayerState?,
    summaryResult: Result<VideoAiSummary>?,
    isLoadingSummary: Boolean,
    onDismiss: () -> Unit,
    onUpdatePlayer: ((ActivePlayerState) -> ActivePlayerState) -> Unit,
    onToggleSave: () -> Unit,
    onToggleLike: () -> Unit,
    onDownloadResource: (String) -> Unit,
    onAddNote: (Long, String) -> Unit,
    onCreatePlaylist: (String) -> Unit,
    onGenerateAiSummary: () -> Unit,
    onAskAi: () -> Unit,
) {
    val context = LocalContext.current
    val lang = prefs.language
    val strings = stringsFor(lang)
    val isSaved = video.id in prefs.savedIds
    val isLiked = video.id in prefs.likedIds
    var noteInput by remember { mutableStateOf("") }
    var showNoteBox by remember { mutableStateOf(false) }
    var selectedQuizOpt by remember { mutableStateOf<Int?>(null) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = video.category,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, strings.close)
                }
            }

            // In-App Player Canvas + Controls
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp)),
            ) {
                VideoThumbnailImage(video, Modifier.fillMaxSize(), video.titleEn)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = if (player?.audioOnly == true) 0.78f else 0.35f))
                )
                IconButton(
                    onClick = { onUpdatePlayer { it.copy(playing = !it.playing) } },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)),
                ) {
                    Icon(
                        imageVector = if (player?.playing == true) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play pause",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(36.dp),
                    )
                }
                if (player?.audioOnly == true) {
                    Text(
                        text = if (lang == "en") "Audio-Only Mode Active" else "অডিও মোড চালু আছে",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp),
                    )
                }
            }

            if (player != null) {
                val pos = player.positionSeconds.toFloat()
                val total = player.durationSeconds.coerceAtLeast(1).toFloat()
                Slider(
                    value = pos,
                    onValueChange = { newVal -> onUpdatePlayer { it.copy(positionSeconds = newVal.toInt()) } },
                    valueRange = 0f..total,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(formatSeconds(player.positionSeconds), style = MaterialTheme.typography.labelSmall)
                    Text(formatSeconds(player.durationSeconds), style = MaterialTheme.typography.labelSmall)
                }

                // Player quick toggles: Speed, Quality, Audio-Only, Loop
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = player.audioOnly,
                        onClick = { onUpdatePlayer { it.copy(audioOnly = !it.audioOnly) } },
                        leadingIcon = { Icon(Icons.Default.Headphones, null, modifier = Modifier.size(16.dp)) },
                        label = { Text(strings.audioMode) },
                    )
                    FilterChip(
                        selected = player.loopEnabled,
                        onClick = { onUpdatePlayer { it.copy(loopEnabled = !it.loopEnabled) } },
                        leadingIcon = { Icon(Icons.Default.Loop, null, modifier = Modifier.size(16.dp)) },
                        label = { Text("Loop") },
                    )
                    FilterChip(
                        selected = false,
                        onClick = {
                            val nextSpeed = when (player.speed) {
                                1.0f -> 1.25f
                                1.25f -> 1.5f
                                1.5f -> 2.0f
                                else -> 1.0f
                            }
                            onUpdatePlayer { it.copy(speed = nextSpeed) }
                        },
                        label = { Text("${player.speed}x") },
                    )
                    FilterChip(
                        selected = false,
                        onClick = {
                            val nextQ = when (player.quality) {
                                "1080p" -> "720p"
                                "720p" -> "480p"
                                else -> "1080p"
                            }
                            onUpdatePlayer { it.copy(quality = nextQ) }
                        },
                        label = { Text(player.quality) },
                    )
                }
            }

            Text(
                text = if (lang == "en") video.titleEn else video.titleBn,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            // Action Row: Like, Save, Note, Playlist, Share, YouTube
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(onClick = onToggleLike) {
                    Icon(Icons.Default.ThumbUp, null, tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(formatNumber(video.likesCount + if (isLiked) 1 else 0, prefs.useBanglaNumerals))
                }
                OutlinedButton(onClick = onToggleSave) {
                    Icon(if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isSaved) strings.saved else strings.saveVideo)
                }
                OutlinedButton(onClick = { showNoteBox = !showNoteBox }) {
                    Icon(Icons.Default.EditNote, null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(strings.addNote)
                }
                OutlinedButton(onClick = { onCreatePlaylist("Watch Later") }) {
                    Icon(Icons.Default.PlaylistAdd, null)
                }
                OutlinedButton(onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "Watch ${video.titleEn} on Kabir Tech Lab: https://youtube.com/watch?v=${video.youtubeId}")
                    }
                    context.startActivity(Intent.createChooser(shareIntent, strings.share))
                }) {
                    Icon(Icons.Default.Share, null)
                }
                Button(onClick = {
                    val ytIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=${video.youtubeId}"))
                    context.startActivity(ytIntent)
                }) {
                    Icon(Icons.Default.OpenInNew, null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(strings.openInYoutube)
                }
            }

            if (showNoteBox) {
                GlassCard {
                    val ts = (player?.positionSeconds ?: 0).toLong()
                    Text("${strings.addNote} @ ${formatSeconds(ts.toInt())}", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        placeholder = { Text(if (lang == "en") "Write your timestamp note..." else "এই মুহূর্তের নোট লিখুন...") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        if (noteInput.isNotBlank()) {
                            onAddNote(ts, noteInput)
                            noteInput = ""
                            showNoteBox = false
                        }
                    }) {
                        Text(strings.submit)
                    }
                }
            }

            // Gemini AI Video Summary & Quiz
            GlassCard(highlightBorder = true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.aiVideoSummary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = onAskAi) {
                        Text(if (lang == "en") "Ask AI" else "প্রশ্ন করুন")
                    }
                }

                if (isLoadingSummary) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(strings.loading)
                    }
                } else if (summaryResult == null) {
                    Button(onClick = onGenerateAiSummary, modifier = Modifier.fillMaxWidth()) {
                        Text(strings.generateAiSummary)
                    }
                } else {
                    summaryResult.fold(
                        onSuccess = { summary ->
                            Text(summary.tldr, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            summary.keyPoints.forEach { pt ->
                                Text("• $pt", style = MaterialTheme.typography.bodyMedium)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(summary.quizQuestion, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            summary.quizOptions.forEachIndexed { idx, opt ->
                                val picked = selectedQuizOpt == idx
                                val isCorrect = idx == summary.correctIndex
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp)
                                        .clickable { selectedQuizOpt = idx },
                                    shape = RoundedCornerShape(10.dp),
                                    color = when {
                                        picked && isCorrect -> Color(0xFF10B981).copy(alpha = 0.25f)
                                        picked && !isCorrect -> MaterialTheme.colorScheme.error.copy(alpha = 0.22f)
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    },
                                ) {
                                    Text(opt, modifier = Modifier.padding(10.dp), style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        },
                        onFailure = { err ->
                            Text(
                                text = err.localizedMessage ?: "AI summary unavailable.",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            TextButton(onClick = onGenerateAiSummary) { Text(strings.retry) }
                        }
                    )
                }
            }

            // Chapters
            if (video.chapters.isNotEmpty()) {
                Text(strings.chapters, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                video.chapters.forEach { ch ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onUpdatePlayer { it.copy(positionSeconds = ch.timeSeconds, playing = true) } }
                            .padding(vertical = 6.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(ch.timeLabel, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(if (lang == "en") ch.titleEn else ch.titleBn, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // Resources
            if (video.resources.isNotEmpty()) {
                Text(strings.downloadResources, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                video.resources.forEach { res ->
                    val downloaded = res.id in prefs.downloadedIds
                    GlassCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(if (lang == "en") res.titleEn else res.titleBn, style = MaterialTheme.typography.labelLarge)
                                Text("${res.type} • ${res.sizeLabel}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            OutlinedButton(onClick = { onDownloadResource(res.id) }) {
                                Icon(Icons.Default.Download, null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (downloaded) "✓" else "Get")
                            }
                        }
                    }
                }
            }

            // Sample Comments
            if (video.sampleComments.isNotEmpty()) {
                Text(strings.comments, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                video.sampleComments.forEach { (author, comment) ->
                    GlassCard {
                        Text(author, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Text(comment, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
