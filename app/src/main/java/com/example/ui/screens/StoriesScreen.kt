package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StoryPart
import com.example.data.model.StorySeries
import com.example.data.repository.UserPreferences
import com.example.i18n.formatNumber
import com.example.i18n.stringsFor
import com.example.ui.components.GlassCard
import com.example.ui.theme.GoldAmber
import com.example.ui.viewmodel.ActivePlayerState

@Composable
fun StoriesScreen(
    prefs: UserPreferences,
    stories: List<StorySeries>,
    initialSelectedSeries: StorySeries? = null,
    activePlayer: ActivePlayerState?,
    onPlayStoryPart: (StorySeries, StoryPart, Boolean) -> Unit,
    onUpdatePlayer: ((ActivePlayerState) -> ActivePlayerState) -> Unit,
    onToggleSave: (String) -> Unit,
    onShowMessage: (String) -> Unit,
) {
    val lang = prefs.language
    val strings = stringsFor(lang)
    val genres = listOf("All", "Romantic", "Sad", "Family", "Thriller", "Motivational")
    var selectedGenre by remember { mutableStateOf("All") }
    var activeSeries by remember(initialSelectedSeries) { mutableStateOf(initialSelectedSeries) }
    var readerPart by remember { mutableStateOf<Pair<StorySeries, StoryPart>?>(null) }

    val filtered = remember(stories, selectedGenre) {
        if (selectedGenre == "All") stories else stories.filter { it.genre == selectedGenre }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text(
                text = strings.storiesHeader,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(genres) { genre ->
                    FilterChip(
                        selected = selectedGenre == genre,
                        onClick = { selectedGenre = genre },
                        label = { Text(genre) },
                    )
                }
            }
        }

        // Story of the Week Highlight
        val storyOfWeek = stories.firstOrNull { it.storyOfTheWeek }
        if (storyOfWeek != null && (selectedGenre == "All" || selectedGenre == storyOfWeek.genre)) {
            item {
                GlassCard(
                    onClick = { activeSeries = storyOfWeek },
                    highlightBorder = true,
                ) {
                    Surface(
                        color = GoldAmber.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(
                            text = "★ ${strings.storyOfTheWeek}",
                            color = GoldAmber,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Image(
                        painter = painterResource(id = storyOfWeek.coverRes),
                        contentDescription = storyOfWeek.titleEn,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(165.dp)
                            .clip(RoundedCornerShape(18.dp)),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (lang == "en") storyOfWeek.titleEn else storyOfWeek.titleBn,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = if (lang == "en") storyOfWeek.synopsisEn else storyOfWeek.synopsisBn,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val firstPart = storyOfWeek.parts.first()
                            onPlayStoryPart(storyOfWeek, firstPart, true)
                            readerPart = storyOfWeek to firstPart
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Default.PlayArrow, null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.continueStory)
                    }
                }
            }
        }

        // All Series Cards
        items(filtered, key = { it.id }) { series ->
            val saved = series.id in prefs.savedIds
            GlassCard(onClick = { activeSeries = series }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = series.coverRes),
                        contentDescription = series.titleEn,
                        modifier = Modifier
                            .size(92.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${series.genre} • ${formatNumber(series.parts.size, prefs.useBanglaNumerals)} parts",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = if (lang == "en") series.titleEn else series.titleBn,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(progress = { 0.3f }, modifier = Modifier.fillMaxWidth())
                    }
                    IconButton(onClick = { onToggleSave(series.id) }) {
                        Icon(if (saved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, null)
                    }
                }
            }
        }
    }

    // Series Detail Sheet
    activeSeries?.let { series ->
        StorySeriesDetailSheet(
            series = series,
            prefs = prefs,
            onDismiss = { activeSeries = null },
            onSelectPart = { part, audioMode ->
                onPlayStoryPart(series, part, audioMode)
                readerPart = series to part
            }
        )
    }

    // Audio Lock-Screen / Reading Mode Sheet
    readerPart?.let { (series, part) ->
        StoryReaderAndAudioSheet(
            series = series,
            part = part,
            prefs = prefs,
            activePlayer = activePlayer,
            onDismiss = { readerPart = null },
            onUpdatePlayer = onUpdatePlayer,
            onNextPart = {
                val next = series.parts.firstOrNull { it.partNumber == part.partNumber + 1 }
                if (next != null) {
                    onPlayStoryPart(series, next, activePlayer?.audioOnly ?: true)
                    readerPart = series to next
                } else {
                    onShowMessage(if (lang == "en") "You completed this series!" else "সম্পূর্ণ সিরিজ শোনা শেষ!")
                }
            },
            onShowMessage = onShowMessage,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StorySeriesDetailSheet(
    series: StorySeries,
    prefs: UserPreferences,
    onDismiss: () -> Unit,
    onSelectPart: (StoryPart, Boolean) -> Unit,
) {
    val lang = prefs.language
    val strings = stringsFor(lang)
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
                    text = if (lang == "en") series.titleEn else series.titleBn,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, strings.close) }
            }

            // Previously on...
            GlassCard(highlightBorder = true) {
                Text(strings.previouslyOn, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(if (lang == "en") series.previouslyOnEn else series.previouslyOnBn, style = MaterialTheme.typography.bodyMedium)
            }

            // Characters
            Text(strings.charactersTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(series.characters) { ch ->
                    GlassCard(modifier = Modifier.width(175.dp)) {
                        Text(if (lang == "en") ch.nameEn else ch.nameBn, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(if (lang == "en") ch.roleEn else ch.roleBn, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Parts List
            series.parts.forEach { part ->
                GlassCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(if (lang == "en") part.titleEn else part.titleBn, style = MaterialTheme.typography.titleMedium)
                            Text("${part.durationLabel} • ★ ${part.rating}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { onSelectPart(part, false) }) {
                            Icon(Icons.Default.MenuBook, strings.readMode)
                        }
                        IconButton(onClick = { onSelectPart(part, true) }) {
                            Icon(Icons.Default.Headphones, strings.audioMode, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun StoryReaderAndAudioSheet(
    series: StorySeries,
    part: StoryPart,
    prefs: UserPreferences,
    activePlayer: ActivePlayerState?,
    onDismiss: () -> Unit,
    onUpdatePlayer: ((ActivePlayerState) -> ActivePlayerState) -> Unit,
    onNextPart: () -> Unit,
    onShowMessage: (String) -> Unit,
) {
    val lang = prefs.language
    val strings = stringsFor(lang)
    var isReadingMode by remember { mutableStateOf(false) }
    var bedtimeDim by remember { mutableStateOf(false) }
    var readerFontSize by remember { mutableFloatStateOf(17f) }
    var lineSpacing by remember { mutableFloatStateOf(27f) }
    var sleepTimer by remember { mutableIntStateOf(0) }
    var reacted by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = if (bedtimeDim) Color(0xFF030712) else MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilterChip(
                    selected = !isReadingMode,
                    onClick = { isReadingMode = false },
                    leadingIcon = { Icon(Icons.Default.Headphones, null, modifier = Modifier.size(16.dp)) },
                    label = { Text(strings.audioMode) },
                )
                FilterChip(
                    selected = isReadingMode,
                    onClick = { isReadingMode = true },
                    leadingIcon = { Icon(Icons.Default.MenuBook, null, modifier = Modifier.size(16.dp)) },
                    label = { Text(strings.readMode) },
                )
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, strings.close) }
            }

            if (!isReadingMode) {
                // Lock-Screen Style Audio Story Player
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(id = series.coverRes),
                        contentDescription = series.titleEn,
                        modifier = Modifier
                            .size(170.dp)
                            .clip(RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (lang == "en") part.titleEn else part.titleBn,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = if (lang == "en") series.titleEn else series.titleBn,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = { onUpdatePlayer { it.copy(playing = !it.playing) } },
                            shape = CircleShape,
                            modifier = Modifier.size(64.dp),
                        ) {
                            Icon(
                                if (activePlayer?.playing == true) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play pause",
                            )
                        }
                        OutlinedButton(onClick = onNextPart) {
                            Text(if (lang == "en") "Next Part →" else "পরবর্তী পর্ব →")
                        }
                    }
                }

                // Sleep Timer + Bedtime Dim
                GlassCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.sleepTimer, style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(0 to "Off", 15 to "15m", 30 to "30m", 60 to "60m").forEach { (mins, lbl) ->
                            FilterChip(
                                selected = sleepTimer == mins,
                                onClick = {
                                    sleepTimer = mins
                                    onUpdatePlayer { it.copy(sleepTimerMinutes = mins) }
                                },
                                label = { Text(lbl) },
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bedtime, null, tint = GoldAmber)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strings.bedtimeMode)
                        }
                        Switch(checked = bedtimeDim, onCheckedChange = { bedtimeDim = it })
                    }
                }
            } else {
                // Reading Mode with Font Size & Line Spacing Controls
                GlassCard {
                    Text(if (lang == "en") "Text Size: ${readerFontSize.toInt()}sp" else "ফন্ট সাইজ: ${readerFontSize.toInt()}sp", style = MaterialTheme.typography.labelLarge)
                    Slider(value = readerFontSize, onValueChange = { readerFontSize = it }, valueRange = 14f..26f)
                    Text(if (lang == "en") "Line Spacing" else "লাইনের দূরত্ব", style = MaterialTheme.typography.labelLarge)
                    Slider(value = lineSpacing, onValueChange = { lineSpacing = it }, valueRange = 22f..38f)
                }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (bedtimeDim) Color.Black else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                ) {
                    Text(
                        text = if (lang == "en") part.storyTextEn else part.storyTextBn,
                        fontSize = readerFontSize.sp,
                        lineHeight = lineSpacing.sp,
                        color = if (bedtimeDim) Color(0xFFBDBDBD) else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(18.dp),
                    )
                }
            }

            // Part Reactions & Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(onClick = {
                    reacted = !reacted
                    onShowMessage("♥ Reaction added!")
                }) {
                    Icon(Icons.Default.Favorite, null, tint = if (reacted) Color(0xFFF43F5E) else MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(formatNumber(part.reactionsCount + if (reacted) 1 else 0, prefs.useBanglaNumerals))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = GoldAmber)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${part.rating} / 5.0", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
