package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.config.AppConfig
import com.example.data.model.ChatMessage
import com.example.data.repository.UserPreferences
import com.example.i18n.stringsFor
import com.example.ui.components.GlassCard
import com.example.ui.viewmodel.AiGenerationState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    prefs: UserPreferences,
    chatHistory: List<ChatMessage>,
    aiState: AiGenerationState,
    initialPrompt: String = "",
    videoContext: String? = null,
    onSendMessage: (String, String?) -> Unit,
    onBack: () -> Unit,
    onShowMessage: (String) -> Unit,
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val lang = prefs.language
    val strings = stringsFor(lang)
    var input by remember(initialPrompt) { mutableStateOf(initialPrompt) }

    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    val usedToday = if (prefs.aiCallDate == today) prefs.aiCallsToday else 0

    val quickPrompts = if (lang == "en") {
        listOf(
            "Write a viral YouTube script hook about AI tools",
            "Convert Banglish to pure Bangla: Ami tomader tech channel dekhe Android shikchi",
            "Generate 5 thumbnail ideas & CTR titles",
            "Explain Jetpack Compose StateFlow simply",
        )
    } else {
        listOf(
            "AI টুলস নিয়ে একটি আকর্ষণীয় ইউটিউব স্ক্রিপ্ট লিখুন",
            "বাংলিশ থেকে শুদ্ধ বাংলা করুন: Ami tomader tech channel dekhe Android shikchi",
            "ইউটিউব ভিডিওর জন্য ৫টি ভাইরাল টাইটেল ও থাম্বনেইল আইডিয়া দিন",
            "সহজ বাংলায় Jetpack Compose বুঝিয়ে দিন",
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(strings.aiAssistantTitle, style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = "Gemini 3.8 Flash • $usedToday/${AppConfig.DAILY_AI_MESSAGE_LIMIT} today",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            if (!videoContext.isNullOrBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                ) {
                    Text(
                        text = "Context: ${videoContext.take(100)}...",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(10.dp),
                    )
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 6.dp),
            ) {
                items(quickPrompts) { qp ->
                    FilterChip(
                        selected = false,
                        onClick = { input = qp },
                        leadingIcon = { Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(16.dp)) },
                        label = { Text(qp.take(34) + if (qp.length > 34) "…" else "") },
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
            ) {
                items(chatHistory, key = { it.id }) { msg ->
                    val isUser = msg.role == "user"
                    GlassCard(
                        highlightBorder = !isUser,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = if (isUser) (if (lang == "en") "You" else "আপনি") else "Gemini AI",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isUser) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                            )
                            if (!isUser) {
                                Row {
                                    IconButton(
                                        onClick = {
                                            clipboard.setText(AnnotatedString(msg.text))
                                            onShowMessage(strings.copied)
                                        },
                                        modifier = Modifier.size(32.dp),
                                    ) {
                                        Icon(Icons.Default.ContentCopy, strings.copy, modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            val share = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_TEXT, msg.text)
                                            }
                                            context.startActivity(Intent.createChooser(share, strings.share))
                                        },
                                        modifier = Modifier.size(32.dp),
                                    ) {
                                        Icon(Icons.Default.Share, strings.share, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(msg.text, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                when (aiState) {
                    is AiGenerationState.Loading -> {
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(strings.loading)
                            }
                        }
                    }
                    is AiGenerationState.Streaming -> {
                        item {
                            GlassCard(highlightBorder = true) {
                                Text("Gemini AI...", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(aiState.text, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                    is AiGenerationState.Error -> {
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(14.dp),
                            ) {
                                Text(
                                    text = aiState.message,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(12.dp),
                                )
                            }
                        }
                    }
                    else -> {}
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text(if (lang == "en") "Ask anything in Bangla or English..." else "বাংলা বা ইংরেজিতে যেকোনো প্রশ্ন লিখুন...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_chat_input"),
                    shape = RoundedCornerShape(18.dp),
                    maxLines = 3,
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        val prompt = input
                        input = ""
                        onSendMessage(prompt, videoContext)
                    },
                    enabled = input.isNotBlank() && aiState !is AiGenerationState.Loading && aiState !is AiGenerationState.Streaming,
                    modifier = Modifier.testTag("ai_chat_send"),
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, "Send", tint = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
