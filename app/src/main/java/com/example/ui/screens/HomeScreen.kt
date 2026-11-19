package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.mock.MockChannelData
import com.example.data.model.Announcement
import com.example.data.model.StorySeries
import com.example.data.model.VideoItem
import com.example.data.repository.UserPreferences
import com.example.i18n.formatNumber
import com.example.i18n.stringsFor
import com.example.ui.components.GlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.SkeletonLoaderCard
import com.example.ui.components.VideoThumbnailImage
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.RoseCrimson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    prefs: UserPreferences,
    videos: List<VideoItem>,
    stories: List<StorySeries>,
    announcements: List<Announcement>,
    isRefreshing: Boolean,
    isOfflineMode: Boolean,
    onRefresh: () -> Unit,
    onToggleLanguage: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenVideo: (VideoItem) -> Unit,
    onOpenStory: (StorySeries) -> Unit,
    onOpenAiWithPrompt: (String) -> Unit,
    onOpenTools: () -> Unit,
    onClaimCheckIn: () -> Unit,
    onToggleSection: (String) -> Unit,
    onMoveSection: (String, Int) -> Unit,
    onSelectCategoryNavigate: (String) -> Unit,
    onShowMessage: (String) -> Unit,
) {
    val lang = prefs.language
    val strings = stringsFor(lang)
    val clipboard = LocalClipboardManager.current
    var showCustomizeSheet by remember { mutableStateOf(false) }
    var remindedPremieres by remember { mutableStateOf(setOf<String>()) }

    val todayKey = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    val alreadyClaimedToday = prefs.lastCheckInDay == todayKey

    val sectionOrder = remember(prefs.homeSectionOrder) {
        prefs.homeSectionOrder.split(",").filter { it.isNotBlank() }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Top Brand Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Image(
                        painter = painterResource(id = R.drawable.img_kabir_avatar),
                        contentDescription = "Kabir Tech Lab Avatar",
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = strings.appName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = if (isOfflineMode) strings.offlineBanner else "YouTube • AI • Stories",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onRefresh, modifier = Modifier.testTag("home_refresh_btn")) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = onToggleLanguage, modifier = Modifier.testTag("home_lang_toggle")) {
                        Icon(Icons.Default.Language, contentDescription = strings.languageLabel)
                    }
                    IconButton(onClick = onOpenSearch, modifier = Modifier.testTag("home_search_btn")) {
                        Icon(Icons.Default.Search, contentDescription = strings.searchPlaceholder)
                    }
                    IconButton(onClick = onOpenNotifications, modifier = Modifier.testTag("home_notifs_btn")) {
                        Icon(Icons.Default.Notifications, contentDescription = strings.notificationsTitle)
                    }
                }
            }
        }

        // Category Quick Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(MockChannelData.categories.drop(1)) { cat ->
                    FilterChip(
                        selected = cat in prefs.favoriteCategories,
                        onClick = { onSelectCategoryNavigate(cat) },
                        label = { Text(cat) },
                    )
                }
            }
        }

        if (isRefreshing) {
            item { SkeletonLoaderCard() }
        }

        // Dynamic Reorderable Home Sections
        items(sectionOrder, key = { it }) { sectionKey ->
            if (sectionKey in prefs.hiddenHomeSections) return@items

            when (sectionKey) {
                "live" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        val liveVideo = videos.firstOrNull { it.liveStream }
                        if (liveVideo != null) {
                            Surface(
                                shape = RoundedCornerShape(22.dp),
                                color = RoseCrimson.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, RoseCrimson.copy(alpha = 0.7f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onOpenVideo(liveVideo) }
                                    .testTag("home_live_banner"),
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(Icons.Default.LiveTv, null, tint = RoseCrimson)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${strings.liveNow} • ${formatNumber(1420, prefs.useBanglaNumerals)} watching",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = RoseCrimson,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(
                                            text = if (lang == "en") liveVideo.titleEn else liveVideo.titleBn,
                                            style = MaterialTheme.typography.bodyMedium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = RoseCrimson,
                                    ) {
                                        Text(
                                            text = strings.watchLive,
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        )
                                    }
                                }
                            }
                        }
                        announcements.filter { it.pinned }.take(1).forEach { ann ->
                            GlassCard(highlightBorder = true) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Campaign, null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (lang == "en") ann.titleEn else ann.titleBn,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (lang == "en") ann.bodyEn else ann.bodyBn,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                "hero" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SectionHeader(title = strings.latestVideos)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(videos.take(4), key = { it.id }) { vid ->
                                GlassCard(
                                    modifier = Modifier.width(290.dp),
                                    onClick = { onOpenVideo(vid) },
                                    highlightBorder = true,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(145.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                    ) {
                                        VideoThumbnailImage(vid, Modifier.fillMaxSize(), vid.titleEn)
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Brush.verticalGradient(
                                                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.72f))
                                                    )
                                                )
                                        )
                                        Surface(
                                            color = Color.Black.copy(alpha = 0.7f),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(8.dp),
                                        ) {
                                            Text(
                                                text = vid.durationLabel,
                                                color = Color.White,
                                                style = MaterialTheme.typography.labelSmall,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                            )
                                        }
                                        Surface(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .padding(8.dp),
                                        ) {
                                            Text(
                                                text = vid.category,
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                style = MaterialTheme.typography.labelSmall,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = if (lang == "en") vid.titleEn else vid.titleBn,
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${formatNumber(vid.viewsCount, prefs.useBanglaNumerals)} views • ${if (lang == "en") vid.publishedLabelEn else vid.publishedLabelBn}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }

                "continue" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SectionHeader(title = strings.continueWatching)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(videos.take(2), key = { "cw_${it.id}" }) { vid ->
                                GlassCard(
                                    modifier = Modifier.width(240.dp),
                                    onClick = { onOpenVideo(vid) },
                                ) {
                                    Text(
                                        text = if (lang == "en") vid.titleEn else vid.titleBn,
                                        style = MaterialTheme.typography.labelLarge,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LinearProgressIndicator(
                                        progress = { 0.58f },
                                        modifier = Modifier.fillMaxWidth().height(5.dp).clip(CircleShape),
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "58% • ${vid.durationLabel}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                        }
                    }
                }

                "trending" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SectionHeader(
                            title = strings.trending,
                            actionLabel = strings.videosTabAll,
                            onActionClick = { onSelectCategoryNavigate("All") },
                        )
                        videos.sortedByDescending { it.viewsCount }.take(3).forEach { vid ->
                            GlassCard(onClick = { onOpenVideo(vid) }) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    VideoThumbnailImage(
                                        video = vid,
                                        modifier = Modifier
                                            .size(96.dp, 64.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentDescription = vid.titleEn,
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (lang == "en") vid.titleEn else vid.titleBn,
                                            style = MaterialTheme.typography.titleMedium,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        Text(
                                            text = "${vid.category} • ${formatNumber(vid.viewsCount, prefs.useBanglaNumerals)} views",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                    Icon(Icons.Default.PlayArrow, null, tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }

                "tips" -> {
                    GlassCard(highlightBorder = true) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bolt, null, tint = GoldAmber)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strings.todaysTechTip, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (lang == "en")
                                "Use Android's built-in Private DNS (dns.google) to speed up lookups and block shady trackers cleanly."
                            else
                                "আপনার Android ফোনের Private DNS অপশনে dns.google ব্যবহার করলে ইন্টারনেট স্পিড ও প্রাইভেসি দুটোই ভালো থাকে।",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                "story" -> {
                    val featured = stories.firstOrNull { it.storyOfTheWeek } ?: stories.firstOrNull()
                    if (featured != null) {
                        GlassCard(
                            onClick = { onOpenStory(featured) },
                            highlightBorder = true,
                        ) {
                            Text(
                                text = strings.featuredStory,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = featured.coverRes),
                                    contentDescription = featured.titleEn,
                                    modifier = Modifier
                                        .size(84.dp)
                                        .clip(RoundedCornerShape(16.dp)),
                                    contentScale = ContentScale.Crop,
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (lang == "en") featured.titleEn else featured.titleBn,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = if (lang == "en") featured.synopsisEn else featured.synopsisBn,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }

                "tool" -> {
                    val tool = MockChannelData.toolsDirectory.first()
                    GlassCard(onClick = onOpenTools) {
                        Text(strings.toolOfTheDay, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(tool.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (lang == "en") tool.descriptionEn else tool.descriptionBn,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                "prompt" -> {
                    val prompt = MockChannelData.promptLibrary.first()
                    GlassCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(strings.dailyAiPrompt, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            IconButton(onClick = {
                                clipboard.setText(AnnotatedString(prompt.promptText))
                                onShowMessage(strings.copied)
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = strings.copy)
                            }
                        }
                        Text(
                            text = prompt.promptText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { onOpenAiWithPrompt(prompt.promptText) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(Icons.Default.AutoAwesome, null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (lang == "en") "Run in Gemini Assistant" else "Gemini এআই-তে চালান")
                        }
                    }
                }

                "streak" -> {
                    GlassCard(highlightBorder = true) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(strings.dailyStreakTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${formatNumber(prefs.streakDays, prefs.useBanglaNumerals)} days • ${formatNumber(prefs.localXp, prefs.useBanglaNumerals)} XP",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                            Button(
                                onClick = onClaimCheckIn,
                                enabled = !alreadyClaimedToday,
                                modifier = Modifier.testTag("claim_daily_xp_btn"),
                            ) {
                                if (alreadyClaimedToday) {
                                    Icon(Icons.Default.CheckCircle, null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(strings.claimedToday)
                                } else {
                                    Text(strings.claimDailyXp)
                                }
                            }
                        }
                    }
                }

                "premieres" -> {
                    val premieres = videos.filter { it.premiereScheduled }
                    if (premieres.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SectionHeader(title = strings.upcomingPremieres)
                            premieres.forEach { vid ->
                                val isReminded = vid.id in remindedPremieres
                                GlassCard {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Schedule, null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (lang == "en") vid.titleEn else vid.titleBn,
                                                style = MaterialTheme.typography.titleMedium,
                                            )
                                            Text(
                                                text = "Starts in ${vid.premiereTimeLabel}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                remindedPremieres = remindedPremieres + vid.id
                                                onShowMessage(if (lang == "en") "Reminder set!" else "রিমাইন্ডার সেট করা হয়েছে!")
                                            }
                                        ) {
                                            Text(if (isReminded) "✓" else strings.remindMe)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Customize Home Sections Button
        item {
            OutlinedButton(
                onClick = { showCustomizeSheet = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("customize_home_btn"),
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(Icons.Default.Tune, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(strings.customizeHome)
            }
        }
    }

    if (showCustomizeSheet) {
        ModalBottomSheet(onDismissRequest = { showCustomizeSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(strings.customizeHome, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                sectionOrder.forEach { key ->
                    val hidden = key in prefs.hiddenHomeSections
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(key.uppercase(), style = MaterialTheme.typography.bodyLarge)
                        Row {
                            IconButton(onClick = { onMoveSection(key, -1) }) {
                                Icon(Icons.Default.KeyboardArrowUp, "Move up")
                            }
                            IconButton(onClick = { onMoveSection(key, 1) }) {
                                Icon(Icons.Default.KeyboardArrowDown, "Move down")
                            }
                            IconButton(onClick = { onToggleSection(key) }) {
                                Icon(
                                    imageVector = if (hidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle visibility",
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
