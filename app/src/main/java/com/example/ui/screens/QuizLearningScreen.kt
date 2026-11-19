package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.mock.MockChannelData
import com.example.data.repository.UserPreferences
import com.example.i18n.formatNumber
import com.example.i18n.stringsFor
import com.example.ui.components.GlassCard
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.GoldAmber
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizLearningScreen(
    prefs: UserPreferences,
    onQuizCompleted: (Int) -> Unit,
    onToggleFlashcardMastered: (String) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val lang = prefs.language
    val strings = stringsFor(lang)
    var tab by remember { mutableIntStateOf(0) } // 0=Quiz, 1=Flashcards, 2=Leaderboard

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.quizLearningTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            TabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Quiz") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Flashcards") })
                Tab(selected = tab == 2, onClick = { tab = 2 }, text = { Text("Leaderboard") })
            }

            when (tab) {
                0 -> {
                    val questions = MockChannelData.quizQuestions
                    var currentIndex by remember { mutableIntStateOf(0) }
                    var selectedOption by remember { mutableStateOf<Int?>(null) }
                    var score by remember { mutableIntStateOf(0) }
                    var timerSeconds by remember { mutableIntStateOf(30) }
                    var finished by remember { mutableStateOf(false) }

                    LaunchedEffect(currentIndex, finished) {
                        if (!finished) {
                            timerSeconds = 30
                            while (timerSeconds > 0 && selectedOption == null) {
                                delay(1000L)
                                timerSeconds--
                            }
                        }
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        if (finished) {
                            item {
                                GlassCard(highlightBorder = true) {
                                    Text(
                                        text = if (lang == "en") "Quiz Complete!" else "কুইজ সম্পন্ন হয়েছে!",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Score: $score / ${questions.size} (+${score * 25} XP)", color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(onClick = {
                                            currentIndex = 0
                                            selectedOption = null
                                            score = 0
                                            finished = false
                                        }) { Text(if (lang == "en") "Play Again" else "আবার খেলুন") }
                                        OutlinedButton(onClick = {
                                            val share = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_TEXT, "I scored $score/${questions.size} on today's Kabir Tech Lab Tech Quiz!")
                                            }
                                            context.startActivity(Intent.createChooser(share, strings.share))
                                        }) {
                                            Icon(Icons.Default.Share, null)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(strings.share)
                                        }
                                    }
                                }
                            }
                        } else {
                            val q = questions[currentIndex]
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text("${q.category} • ${q.difficulty}", color = MaterialTheme.colorScheme.primary)
                                    Text("⏱ ${timerSeconds}s", fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                GlassCard(highlightBorder = true) {
                                    Text(
                                        text = if (lang == "en") q.questionEn else q.questionBn,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    val opts = if (lang == "en") q.optionsEn else q.optionsBn
                                    opts.forEachIndexed { idx, opt ->
                                        val isPicked = selectedOption == idx
                                        val isCorrect = idx == q.correctIndex
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .clickable(enabled = selectedOption == null) {
                                                    selectedOption = idx
                                                    if (idx == q.correctIndex) score++
                                                },
                                            shape = RoundedCornerShape(14.dp),
                                            color = when {
                                                selectedOption != null && isCorrect -> EmeraldMint.copy(alpha = 0.25f)
                                                isPicked && !isCorrect -> MaterialTheme.colorScheme.error.copy(alpha = 0.22f)
                                                else -> MaterialTheme.colorScheme.surfaceVariant
                                            },
                                        ) {
                                            Text(opt, modifier = Modifier.padding(14.dp), style = MaterialTheme.typography.bodyLarge)
                                        }
                                    }
                                    if (selectedOption != null) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = if (lang == "en") q.explanationEn else q.explanationBn,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = {
                                                if (currentIndex < questions.lastIndex) {
                                                    currentIndex++
                                                    selectedOption = null
                                                } else {
                                                    finished = true
                                                    onQuizCompleted(score * 25)
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                        ) {
                                            Text(if (currentIndex < questions.lastIndex) strings.next else "Finish")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    var flippedId by remember { mutableStateOf<String?>(null) }
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(MockChannelData.flashcards, key = { it.id }) { card ->
                            val flipped = flippedId == card.id
                            val mastered = card.id in prefs.masteredFlashcards
                            GlassCard(
                                onClick = { flippedId = if (flipped) null else card.id },
                                highlightBorder = mastered,
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(card.category, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                                    Icon(Icons.Default.Style, null)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (flipped) (if (lang == "en") card.backEn else card.backBn) else card.frontTerm,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                FilterChip(
                                    selected = mastered,
                                    onClick = { onToggleFlashcardMastered(card.id) },
                                    label = { Text(if (mastered) "Mastered ✓" else "Mark Mastered") },
                                )
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(MockChannelData.sampleLeaderboard.indices.toList()) { idx ->
                            val (name, xp, streak) = MockChannelData.sampleLeaderboard[idx]
                            GlassCard(highlightBorder = idx == 0) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.EmojiEvents, null, tint = if (idx == 0) GoldAmber else MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("#${idx + 1} $name", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text("${formatNumber(streak, prefs.useBanglaNumerals)} day streak", style = MaterialTheme.typography.labelSmall)
                                    }
                                    Text("${formatNumber(xp, prefs.useBanglaNumerals)} XP", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
