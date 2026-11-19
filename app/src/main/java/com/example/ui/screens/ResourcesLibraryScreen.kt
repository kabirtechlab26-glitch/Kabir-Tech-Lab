package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.mock.MockChannelData
import com.example.data.model.VideoItem
import com.example.data.model.VideoNote
import com.example.data.repository.UserPreferences
import com.example.i18n.stringsFor
import com.example.ui.components.GlassCard
import com.example.ui.components.formatSeconds
import com.example.ui.theme.EmeraldMint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcesHubScreen(
    prefs: UserPreferences,
    onToggleLesson: (String) -> Unit,
    onBack: () -> Unit,
    onShowMessage: (String) -> Unit,
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val lang = prefs.language
    val strings = stringsFor(lang)
    var tab by remember { mutableIntStateOf(0) } // 0=Tools, 1=Prompts, 2=Courses, 3=Glossary
    var toolQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.resourcesHubTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                },
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            ScrollableTabRow(selectedTabIndex = tab, edgePadding = 12.dp) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Tools Directory") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Prompts & Templates") })
                Tab(selected = tab == 2, onClick = { tab = 2 }, text = { Text("Courses") })
                Tab(selected = tab == 3, onClick = { tab = 3 }, text = { Text("Glossary") })
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                when (tab) {
                    0 -> {
                        item {
                            OutlinedTextField(
                                value = toolQuery,
                                onValueChange = { toolQuery = it },
                                label = { Text(strings.searchPlaceholder) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        val filteredTools = MockChannelData.toolsDirectory.filter {
                            toolQuery.isBlank() || it.name.contains(toolQuery, ignoreCase = true) || it.category.contains(toolQuery, ignoreCase = true)
                        }
                        items(filteredTools, key = { it.id }) { tool ->
                            GlassCard {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${tool.name} (${tool.category})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text(if (lang == "en") tool.descriptionEn else tool.descriptionBn, style = MaterialTheme.typography.bodyMedium)
                                    }
                                    IconButton(onClick = {
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(tool.url)))
                                    }) {
                                        Icon(Icons.Default.OpenInNew, "Open")
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        items(MockChannelData.promptLibrary, key = { it.id }) { prm ->
                            GlassCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(if (lang == "en") prm.titleEn else prm.titleBn, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    IconButton(onClick = {
                                        clipboard.setText(AnnotatedString(prm.promptText))
                                        onShowMessage(strings.copied)
                                    }) { Icon(Icons.Default.ContentCopy, strings.copy) }
                                }
                                Text(prm.promptText, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    2 -> {
                        val completedCount = prefs.completedLessons.size
                        item {
                            GlassCard(highlightBorder = true) {
                                Text("Learning Path Progress: $completedCount / ${MockChannelData.courseLessons.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                if (completedCount == MockChannelData.courseLessons.size) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Button(onClick = {
                                        val share = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, "Certificate of Completion — Kabir Tech Lab AI & Creator Mastery!")
                                        }
                                        context.startActivity(Intent.createChooser(share, strings.share))
                                    }) {
                                        Icon(Icons.Default.Share, null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Share Completion Certificate")
                                    }
                                }
                            }
                        }
                        items(MockChannelData.courseLessons, key = { it.id }) { lesson ->
                            val done = lesson.id in prefs.completedLessons
                            GlassCard(onClick = { onToggleLesson(lesson.id) }, highlightBorder = done) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${lesson.level} • ${lesson.durationLabel}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                        Text(if (lang == "en") lesson.titleEn else lesson.titleBn, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text(if (lang == "en") lesson.summaryEn else lesson.summaryBn, style = MaterialTheme.typography.bodyMedium)
                                    }
                                    Icon(Icons.Default.CheckCircle, null, tint = if (done) EmeraldMint else MaterialTheme.colorScheme.outline)
                                }
                            }
                        }
                    }

                    else -> {
                        items(MockChannelData.glossaryTerms, key = { it.id }) { term ->
                            GlassCard {
                                Text(term.term, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(if (lang == "en") term.definitionEn else term.definitionBn, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    prefs: UserPreferences,
    videos: List<VideoItem>,
    notes: List<VideoNote>,
    onDeleteNote: (String) -> Unit,
    onOpenVideo: (VideoItem) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val lang = prefs.language
    val strings = stringsFor(lang)
    var tab by remember { mutableIntStateOf(0) } // 0=Saved, 1=Notes, 2=Downloads, 3=Liked

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.libraryTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                },
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            ScrollableTabRow(selectedTabIndex = tab, edgePadding = 12.dp) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Saved (${prefs.savedIds.size})") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Notes (${notes.size})") })
                Tab(selected = tab == 2, onClick = { tab = 2 }, text = { Text("Downloads (${prefs.downloadedIds.size})") })
                Tab(selected = tab == 3, onClick = { tab = 3 }, text = { Text("Liked (${prefs.likedIds.size})") })
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                when (tab) {
                    0 -> {
                        val savedVids = videos.filter { it.id in prefs.savedIds }
                        if (savedVids.isEmpty()) {
                            item { Text(strings.emptyStateMessage, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        } else {
                            items(savedVids, key = { it.id }) { vid ->
                                GlassCard(onClick = { onOpenVideo(vid) }) {
                                    Text(if (lang == "en") vid.titleEn else vid.titleBn, style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        }
                    }

                    1 -> {
                        if (notes.isNotEmpty()) {
                            item {
                                OutlinedButton(
                                    onClick = {
                                        val exported = notes.joinToString("\n\n") {
                                            "[${formatSeconds(it.timestampSeconds.toInt())}] ${it.videoTitle}\n${it.content}"
                                        }
                                        val share = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, exported)
                                        }
                                        context.startActivity(Intent.createChooser(share, "Export Notes"))
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Icon(Icons.Default.Share, null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Export Notes as Text")
                                }
                            }
                        }
                        if (notes.isEmpty()) {
                            item { Text(strings.emptyStateMessage, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        } else {
                            items(notes, key = { it.id }) { note ->
                                GlassCard {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("${note.videoTitle} • ${formatSeconds(note.timestampSeconds.toInt())}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                            Text(note.content, style = MaterialTheme.typography.bodyMedium)
                                        }
                                        IconButton(onClick = { onDeleteNote(note.id) }) {
                                            Icon(Icons.Default.Delete, "Delete")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        item {
                            Text("Offline Storage Used: ${prefs.downloadedIds.size * 1.2f} MB", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        }
                        if (prefs.downloadedIds.isEmpty()) {
                            item { Text(strings.emptyStateMessage, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        } else {
                            items(prefs.downloadedIds.toList()) { resId ->
                                GlassCard {
                                    Text("Offline Resource: $resId", style = MaterialTheme.typography.titleMedium)
                                    Text("Ready offline", style = MaterialTheme.typography.labelSmall, color = EmeraldMint)
                                }
                            }
                        }
                    }

                    else -> {
                        val likedVids = videos.filter { it.id in prefs.likedIds }
                        if (likedVids.isEmpty()) {
                            item { Text(strings.emptyStateMessage, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        } else {
                            items(likedVids, key = { it.id }) { vid ->
                                GlassCard(onClick = { onOpenVideo(vid) }) {
                                    Text(if (lang == "en") vid.titleEn else vid.titleBn, style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
