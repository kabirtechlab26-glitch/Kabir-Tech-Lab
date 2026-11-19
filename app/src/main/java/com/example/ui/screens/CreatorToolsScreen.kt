package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.repository.UserPreferences
import com.example.i18n.stringsFor
import com.example.ui.components.GlassCard
import com.example.ui.theme.DeepBlue800
import com.example.ui.theme.DeepBlue950
import com.example.ui.theme.ElectricCyan
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreatorToolsScreen(
    prefs: UserPreferences,
    onGenerateAi: suspend (String) -> Result<String>,
    onBack: () -> Unit,
    onShowMessage: (String) -> Unit,
) {
    val lang = prefs.language
    val strings = stringsFor(lang)
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    var selectedTool by remember { mutableStateOf("Titles & Tags") }
    val toolList = listOf(
        "Titles & Tags",
        "Thumbnail Maker",
        "Script Cleaner",
        "Word Counter",
        "QR Generator",
        "Earnings Est.",
        "Channel Stats",
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.creatorToolsTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    toolList.forEach { t ->
                        FilterChip(
                            selected = selectedTool == t,
                            onClick = { selectedTool = t },
                            label = { Text(t) },
                        )
                    }
                }
            }

            item {
                when (selectedTool) {
                    "Titles & Tags" -> {
                        var topic by remember { mutableStateOf("") }
                        var resultText by remember { mutableStateOf("") }
                        var loading by remember { mutableStateOf(false) }

                        GlassCard(highlightBorder = true) {
                            Text("YouTube Title, Hashtag & Idea Generator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = topic,
                                onValueChange = { topic = it },
                                label = { Text(if (lang == "en") "Video Topic" else "ভিডিওর বিষয় লিখুন") },
                                modifier = Modifier.fillMaxWidth().testTag("creator_topic_input"),
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        if (topic.isNotBlank()) {
                                            loading = true
                                            scope.launch {
                                                val res = onGenerateAi("Generate 5 high-CTR YouTube titles, 10 SEO tags, and 5 hashtags for: $topic")
                                                loading = false
                                                resultText = res.getOrElse {
                                                    "• $topic — সম্পূর্ণ বাংলা গাইড ২০২৬!\n• কীভাবে $topic শিখবেন মাত্র ১০ মিনিটে?\nTags: #KabirTechLab #BanglaTech #AI"
                                                }
                                            }
                                        }
                                    },
                                    enabled = !loading,
                                ) {
                                    Icon(Icons.Default.AutoAwesome, null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (loading) strings.loading else "Generate")
                                }
                                if (resultText.isNotBlank()) {
                                    OutlinedButton(onClick = {
                                        clipboard.setText(AnnotatedString(resultText))
                                        onShowMessage(strings.copied)
                                    }) {
                                        Icon(Icons.Default.ContentCopy, null)
                                    }
                                }
                            }
                            if (resultText.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(resultText, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                    "Thumbnail Maker" -> {
                        var overlayText by remember { mutableStateOf("AI দিয়ে আয় করুন!") }
                        var subBadge by remember { mutableStateOf("নতুন গাইড ২০২৬") }
                        GlassCard(highlightBorder = true) {
                            Text("Thumbnail Text Overlay Preview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(175.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Brush.linearGradient(listOf(DeepBlue950, DeepBlue800))),
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(ElectricCyan)
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(subBadge, color = DeepBlue950, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = overlayText,
                                        color = Color.White,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(value = overlayText, onValueChange = { overlayText = it }, label = { Text("Headline") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(value = subBadge, onValueChange = { subBadge = it }, label = { Text("Badge") }, modifier = Modifier.fillMaxWidth())
                        }
                    }

                    "Script Cleaner" -> {
                        var rawScript by remember { mutableStateOf("[INTRO] আসসালামু আলাইকুম বন্ধুরা (pause 2s) আজকের ভিডিওতে [MUSIC] আমরা শিখব AI টুলস।") }
                        val cleaned = remember(rawScript) {
                            rawScript
                                .replace(Regex("\\[.*?]"), "")
                                .replace(Regex("\\(.*?\\)"), "")
                                .replace(Regex("\\s+"), " ")
                                .trim()
                        }
                        GlassCard {
                            Text("Voice Script Cleaner & Case Converter", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = rawScript,
                                onValueChange = { rawScript = it },
                                label = { Text("Paste raw script") },
                                minLines = 3,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Cleaned Script:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Text(cleaned, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = {
                                    clipboard.setText(AnnotatedString(cleaned))
                                    onShowMessage(strings.copied)
                                }) {
                                    Text(strings.copy)
                                }
                                OutlinedButton(onClick = { rawScript = rawScript.uppercase() }) { Text("UPPER") }
                                OutlinedButton(onClick = { rawScript = rawScript.lowercase() }) { Text("lower") }
                            }
                        }
                    }

                    "Word Counter" -> {
                        var text by remember { mutableStateOf("") }
                        val words = remember(text) { text.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size }
                        val readingMins = (words / 130.0).coerceAtLeast(if (words > 0) 0.2 else 0.0)
                        GlassCard {
                            Text("Word, Character & Voiceover Time Counter", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(value = text, onValueChange = { text = it }, minLines = 4, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Words: $words • Characters: ${text.length} • Est. Speech: ${String.format("%.1f", readingMins)} min", color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    "QR Generator" -> {
                        var qrInput by remember { mutableStateOf("https://youtube.com/@kabirtechlab") }
                        GlassCard {
                            Text("Instant Deterministic QR Pattern", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(value = qrInput, onValueChange = { qrInput = it }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Canvas(modifier = Modifier.size(168.dp).background(Color.White).padding(10.dp)) {
                                    val grid = 15
                                    val cell = size.width / grid
                                    val seed = qrInput.hashCode()
                                    for (r in 0 until grid) {
                                        for (c in 0 until grid) {
                                            val isFinder = (r < 4 && c < 4) || (r < 4 && c >= grid - 4) || (r >= grid - 4 && c < 4)
                                            val filled = isFinder || (abs(seed + r * 31 + c * 17) % 3 == 0)
                                            if (filled) {
                                                drawRect(
                                                    color = DeepBlue950,
                                                    topLeft = Offset(c * cell, r * cell),
                                                    size = Size(cell * 0.92f, cell * 0.92f),
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "Earnings Est." -> {
                        var monthlyViewsK by remember { mutableFloatStateOf(150f) }
                        var rpmUsd by remember { mutableFloatStateOf(1.2f) }
                        val estUsd = monthlyViewsK * rpmUsd
                        val estBdt = estUsd * 120f
                        GlassCard {
                            Text("YouTube Earnings Estimator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Monthly Views: ${monthlyViewsK.toInt()}K")
                            Slider(value = monthlyViewsK, onValueChange = { monthlyViewsK = it }, valueRange = 10f..2000f)
                            Text("Estimated RPM: $${String.format("%.2f", rpmUsd)}")
                            Slider(value = rpmUsd, onValueChange = { rpmUsd = it }, valueRange = 0.3f..6.0f)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Estimated Monthly Income: $${estUsd.toInt()} (~৳${estBdt.toInt()} BDT)",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    else -> {
                        GlassCard {
                            Text("Kabir Tech Lab • Channel Snapshot", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("• Subscribers: 145K+", style = MaterialTheme.typography.bodyLarge)
                            Text("• Total Views: 12.4M+", style = MaterialTheme.typography.bodyLarge)
                            Text("• Story Series Parts: 21+", style = MaterialTheme.typography.bodyLarge)
                            Text("• Thumbnail Downloader Format: https://img.youtube.com/vi/{VIDEO_ID}/maxresdefault.jpg", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
