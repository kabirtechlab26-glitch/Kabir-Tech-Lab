package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.config.AppConfig
import com.example.data.mock.MockChannelData
import com.example.data.model.CommunityPost
import com.example.data.model.VideoRequest
import com.example.data.repository.UserPreferences
import com.example.i18n.formatNumber
import com.example.i18n.stringsFor
import com.example.ui.components.GlassCard
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.GoldAmber

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    prefs: UserPreferences,
    isAuthenticated: Boolean,
    posts: List<CommunityPost>,
    videoRequests: List<VideoRequest>,
    pollVotes: Map<String, String>,
    onVotePoll: (String, String) -> Unit,
    onCreatePost: (String, String, String) -> Unit,
    onLikePost: (String) -> Unit,
    onCreateVideoRequest: (String, String, String) -> Unit,
    onUpvoteRequest: (String) -> Unit,
    onSubmitJoinForm: (String, String, String, String) -> Unit,
    onRequestSignIn: () -> Unit,
    onShowMessage: (String) -> Unit,
) {
    val context = LocalContext.current
    val lang = prefs.language
    val strings = stringsFor(lang)
    var selectedTab by remember { mutableIntStateOf(0) } // 0=Feed, 1=Q&A, 2=Polls, 3=Requests, 4=Showcase
    var showNewPostDialog by remember { mutableStateOf(false) }
    var showRequestDialog by remember { mutableStateOf(false) }
    var showJoinFormDialog by remember { mutableStateOf(false) }
    var joinSubmitted by remember { mutableStateOf(false) }

    val badWords = remember { listOf("spam", "scam", "abuse") }

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(selectedTabIndex = selectedTab, edgePadding = 12.dp) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text(strings.feedTab) })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text(strings.qaTab) })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text(strings.pollsTab) })
            Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text(strings.requestsTab) })
            Tab(selected = selectedTab == 4, onClick = { selectedTab = 4 }, text = { Text(strings.showcaseTab) })
        }

        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Social Links + Join Form Banner
            item {
                GlassCard(highlightBorder = true) {
                    Text(strings.joinChannelsTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        listOf(
                            "YouTube" to AppConfig.YOUTUBE_CHANNEL_LINK,
                            "WhatsApp" to AppConfig.WHATSAPP_LINK,
                            "Telegram" to AppConfig.TELEGRAM_LINK,
                            "Facebook" to AppConfig.FACEBOOK_LINK,
                            "Instagram" to AppConfig.INSTAGRAM_LINK,
                        ).forEach { (label, url) ->
                            OutlinedButton(onClick = {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            }) {
                                Text(label)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (isAuthenticated) showJoinFormDialog = true
                            else onRequestSignIn()
                        },
                        modifier = Modifier.fillMaxWidth().testTag("open_join_form_btn"),
                    ) {
                        Text(
                            if (joinSubmitted) "✓ ${strings.joinCommunityForm}"
                            else if (isAuthenticated) strings.joinCommunityForm
                            else strings.signInWithGoogle
                        )
                    }
                }
            }

            when (selectedTab) {
                0, 1, 4 -> {
                    val typeFilter = when (selectedTab) {
                        1 -> "QA"
                        4 -> "SHOWCASE"
                        else -> "FEED"
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = when (selectedTab) {
                                    1 -> strings.qaTab
                                    4 -> strings.showcaseTab
                                    else -> strings.feedTab
                                },
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                            )
                            Button(
                                onClick = { showNewPostDialog = true },
                                modifier = Modifier.testTag("new_post_btn"),
                            ) {
                                Icon(Icons.Default.Add, null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(strings.newPost)
                            }
                        }
                    }

                    val filteredPosts = posts.filter { it.postType == typeFilter }
                    items(filteredPosts, key = { it.id }) { post ->
                        GlassCard {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(post.authorName, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                if (post.postType == "QA") {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.Verified, null, tint = EmeraldMint)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(post.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(post.body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedButton(onClick = { onLikePost(post.id) }) {
                                    Icon(Icons.Default.ThumbUp, null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(formatNumber(post.likesCount, prefs.useBanglaNumerals))
                                }
                                OutlinedButton(onClick = { onShowMessage(if (lang == "en") "Replies sync via community thread." else "রিপ্লাই শীঘ্রই খুলছে।") }) {
                                    Icon(Icons.Default.Forum, null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(formatNumber(post.repliesCount, prefs.useBanglaNumerals))
                                }
                            }
                        }
                    }
                }

                2 -> {
                    items(MockChannelData.samplePolls, key = { it.id }) { poll ->
                        val pickedOpt = pollVotes[poll.id]
                        val totalVotes = poll.options.sumOf { it.votes } + if (pickedOpt != null) 1 else 0
                        GlassCard(highlightBorder = true) {
                            Text(
                                text = if (lang == "en") poll.questionEn else poll.questionBn,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            poll.options.forEach { opt ->
                                val count = opt.votes + if (pickedOpt == opt.id) 1 else 0
                                val pct = if (totalVotes > 0) count.toFloat() / totalVotes else 0f
                                Surface(
                                    onClick = { onVotePoll(poll.id, opt.id) },
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (pickedOpt == opt.id) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(if (lang == "en") opt.labelEn else opt.labelBn, style = MaterialTheme.typography.bodyMedium)
                                            Text("${(pct * 100).toInt()}%", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LinearProgressIndicator(progress = { pct }, modifier = Modifier.fillMaxWidth())
                                    }
                                }
                            }
                        }
                    }

                    // Events Calendar
                    item {
                        Text(if (lang == "en") "Upcoming Live Events" else "আসন্ন লাইভ ইভেন্ট", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    items(MockChannelData.sampleEvents, key = { it.id }) { evt ->
                        GlassCard {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Event, null, tint = GoldAmber)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(if (lang == "en") evt.titleEn else evt.titleBn, style = MaterialTheme.typography.titleMedium)
                                    Text("${evt.dateLabel} • ${evt.typeLabel}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }

                3 -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(strings.requestsTab, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Button(onClick = { showRequestDialog = true }) {
                                Icon(Icons.Default.Add, null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(strings.requestVideo)
                            }
                        }
                    }
                    items(videoRequests, key = { it.id }) { req ->
                        GlassCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp),
                                ) {
                                    Text(
                                        text = req.status,
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    )
                                }
                                Text(req.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(req.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(req.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(onClick = { onUpvoteRequest(req.id) }) {
                                Icon(Icons.Default.ThumbUp, null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Upvote (${formatNumber(req.upvotes, prefs.useBanglaNumerals)})")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showNewPostDialog) {
        var title by remember { mutableStateOf("") }
        var body by remember { mutableStateOf("") }
        var postType by remember { mutableStateOf(if (selectedTab == 1) "QA" else if (selectedTab == 4) "SHOWCASE" else "FEED") }
        AlertDialog(
            onDismissRequest = { showNewPostDialog = false },
            title = { Text(strings.newPost) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("FEED", "QA", "SHOWCASE").forEach { t ->
                            FilterChip(selected = postType == t, onClick = { postType = t }, label = { Text(t) })
                        }
                    }
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                    OutlinedTextField(value = body, onValueChange = { body = it }, label = { Text("Message") }, minLines = 3)
                }
            },
            confirmButton = {
                Button(onClick = {
                    val combined = "$title $body".lowercase()
                    if (badWords.any { combined.contains(it) }) {
                        onShowMessage("Please avoid spam or inappropriate words.")
                    } else if (title.trim().length >= 2 && body.trim().length >= 2) {
                        onCreatePost(postType, title, body)
                        showNewPostDialog = false
                    }
                }) { Text(strings.submit) }
            },
            dismissButton = { TextButton(onClick = { showNewPostDialog = false }) { Text(strings.cancel) } }
        )
    }

    if (showRequestDialog) {
        var title by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("AI Tools") }
        var desc by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showRequestDialog = false },
            title = { Text(strings.requestVideo) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Topic Title") })
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Details") }, minLines = 3)
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (title.trim().length >= 3 && desc.trim().length >= 3) {
                        onCreateVideoRequest(title, category, desc)
                        showRequestDialog = false
                    }
                }) { Text(strings.submit) }
            },
            dismissButton = { TextButton(onClick = { showRequestDialog = false }) { Text(strings.cancel) } }
        )
    }

    if (showJoinFormDialog) {
        var fullName by remember { mutableStateOf("") }
        var contact by remember { mutableStateOf("") }
        var device by remember { mutableStateOf("${Build.MANUFACTURER} ${Build.MODEL}") }
        var interests by remember { mutableStateOf(prefs.favoriteCategories.joinToString(", ")) }
        AlertDialog(
            onDismissRequest = { showJoinFormDialog = false },
            title = { Text(strings.joinCommunityForm) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name") })
                    OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text("Phone or Email") })
                    OutlinedTextField(value = device, onValueChange = { device = it }, label = { Text("Device Model") })
                    OutlinedTextField(value = interests, onValueChange = { interests = it }, label = { Text("Interests") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (fullName.isNotBlank() && contact.length >= 3) {
                        onSubmitJoinForm(fullName, contact, device, interests)
                        joinSubmitted = true
                        showJoinFormDialog = false
                    }
                }) {
                    Icon(Icons.Default.CheckCircle, null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.submit)
                }
            },
            dismissButton = { TextButton(onClick = { showJoinFormDialog = false }) { Text(strings.cancel) } }
        )
    }
}
