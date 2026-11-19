package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.VolunteerActivism
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.config.AppConfig
import com.example.data.mock.MockChannelData
import com.example.data.model.UserProfile
import com.example.data.repository.UserPreferences
import com.example.i18n.formatNumber
import com.example.i18n.stringsFor
import com.example.ui.components.GlassCard
import com.example.ui.theme.AccentOption
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.SeasonalTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    prefs: UserPreferences,
    userProfile: UserProfile?,
    userEmail: String?,
    isAuthenticated: Boolean,
    onOpenLibrary: () -> Unit,
    onOpenResources: () -> Unit,
    onOpenAi: () -> Unit,
    onOpenCreatorTools: () -> Unit,
    onOpenQuiz: () -> Unit,
    onOpenAdmin: () -> Unit,
    onSignInClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onSetLanguage: (String) -> Unit,
    onSetBanglaNumerals: (Boolean) -> Unit,
    onUpdateTheme: (String?, String?, String?, Float?, Boolean?, Boolean?) -> Unit,
    onUpdatePlayback: (Boolean?, Boolean?, Boolean?, String?) -> Unit,
    onUpdateNotifications: (Boolean?, Boolean?, String?, String?) -> Unit,
    onShowMessage: (String) -> Unit,
) {
    val context = LocalContext.current
    val lang = prefs.language
    val strings = stringsFor(lang)
    val isAdmin = isAuthenticated && (userEmail?.lowercase() in AppConfig.ADMIN_EMAILS)

    val xp = (userProfile?.xp ?: prefs.localXp.toLong()).toInt()
    val level = (xp / 200) + 1

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Profile Header
        item {
            GlassCard(highlightBorder = true) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.img_kabir_avatar),
                        contentDescription = "Profile Avatar",
                        modifier = Modifier.size(64.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userProfile?.displayName ?: if (isAuthenticated) "Member" else "Guest Explorer",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Level $level • ${formatNumber(xp, prefs.useBanglaNumerals)} XP • ${formatNumber(prefs.streakDays, prefs.useBanglaNumerals)}d Streak",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        if (userEmail != null) {
                            Text(userEmail, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                if (!isAuthenticated) {
                    Button(
                        onClick = onSignInClick,
                        modifier = Modifier.fillMaxWidth().testTag("profile_signin_btn"),
                    ) {
                        Text(strings.signInWithGoogle)
                    }
                } else {
                    OutlinedButton(
                        onClick = onSignOutClick,
                        modifier = Modifier.fillMaxWidth().testTag("profile_signout_btn"),
                    ) {
                        Icon(Icons.Default.Logout, null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.signOut)
                    }
                }
            }
        }

        // Feature Hubs
        item {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                val hubs = listOf(
                    Triple(strings.libraryTitle, Icons.Default.Bookmark, onOpenLibrary),
                    Triple(strings.resourcesHubTitle, Icons.Default.FolderOpen, onOpenResources),
                    Triple(strings.aiAssistantTitle, Icons.Default.AutoAwesome, onOpenAi),
                    Triple(strings.creatorToolsTitle, Icons.Default.Build, onOpenCreatorTools),
                    Triple(strings.quizLearningTitle, Icons.Default.Quiz, onOpenQuiz),
                )
                hubs.forEach { (label, icon, action) ->
                    GlassCard(onClick = action, modifier = Modifier.fillMaxWidth(0.48f)) {
                        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }
                }
                if (isAdmin) {
                    GlassCard(onClick = onOpenAdmin, highlightBorder = true, modifier = Modifier.fillMaxWidth(0.48f)) {
                        Icon(Icons.Default.AdminPanelSettings, null, tint = GoldAmber)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(strings.adminPanelTitle, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Premium & Support
        item {
            GlassCard(highlightBorder = true) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VolunteerActivism, null, tint = GoldAmber)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.premiumSupportTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (lang == "en") "Referral Code: KABIR2026 • Share to earn +50 XP!" else "রেফারেল কোড: KABIR2026 • বন্ধুদের আমন্ত্রণ জানিয়ে +৫০ XP পান!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.DONATION_LINK)))
                    }) { Text(if (lang == "en") "Support Channel" else "চ্যানেল সাপোর্ট") }
                    OutlinedButton(onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.YOUTUBE_CHANNEL_LINK)))
                    }) { Text("Subscribe") }
                }
            }
        }

        // Appearance, Language & Accessibility Settings
        item {
            GlassCard {
                Text(strings.settingsTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                Text(strings.languageLabel, style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = lang == "bn", onClick = { onSetLanguage("bn") }, label = { Text("বাংলা") })
                    FilterChip(selected = lang == "en", onClick = { onSetLanguage("en") }, label = { Text("English") })
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (lang == "en") "Bangla Numerals (১২৩)" else "বাংলা সংখ্যা (১২৩)")
                    Switch(checked = prefs.useBanglaNumerals, onCheckedChange = onSetBanglaNumerals)
                }

                Text("Theme Mode", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("dark", "amoled", "light", "system").forEach { mode ->
                        FilterChip(
                            selected = prefs.themeMode == mode,
                            onClick = { onUpdateTheme(mode, null, null, null, null, null) },
                            label = { Text(mode.uppercase()) },
                        )
                    }
                }

                Text("Accent Color", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AccentOption.entries.forEach { acc ->
                        FilterChip(
                            selected = prefs.accentKey == acc.key,
                            onClick = { onUpdateTheme(null, acc.key, null, null, null, null) },
                            label = { Text(acc.label) },
                        )
                    }
                }

                Text("Seasonal Theme", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SeasonalTheme.entries.forEach { st ->
                        FilterChip(
                            selected = prefs.seasonalKey == st.key,
                            onClick = { onUpdateTheme(null, null, st.key, null, null, null) },
                            label = { Text(if (lang == "en") st.labelEn else st.labelBn) },
                        )
                    }
                }

                Text("Font Size (${(prefs.fontScale * 100).toInt()}%)", style = MaterialTheme.typography.labelLarge)
                Slider(
                    value = prefs.fontScale,
                    onValueChange = { onUpdateTheme(null, null, null, it, null, null) },
                    valueRange = 0.85f..1.3f,
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Data Saver Mode")
                    Switch(checked = prefs.dataSaver, onCheckedChange = { onUpdatePlayback(it, null, null, null) })
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Autoplay Next")
                    Switch(checked = prefs.autoplayNext, onCheckedChange = { onUpdatePlayback(null, it, null, null) })
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("High Contrast")
                    Switch(checked = prefs.highContrast, onCheckedChange = { onUpdateTheme(null, null, null, null, it, null) })
                }
            }
        }

        // Notifications Preferences
        item {
            GlassCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.notificationsTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Push Alerts")
                    Switch(checked = prefs.notificationsEnabled, onCheckedChange = { onUpdateNotifications(it, null, null, null) })
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Quiet Hours (11 PM - 7 AM)")
                    Switch(checked = prefs.quietHoursEnabled, onCheckedChange = { onUpdateNotifications(null, it, null, null) })
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Tech", "Stories", "AI").forEach { topic ->
                        FilterChip(
                            selected = topic in prefs.subscribedTopics,
                            onClick = { onUpdateNotifications(null, null, null, topic) },
                            label = { Text("Topic: $topic") },
                        )
                    }
                }
            }
        }

        // About & Policies
        item {
            GlassCard {
                Text("Kabir Tech Lab • v1.0.0", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = "Privacy Policy • Terms • Disclaimer: Educational & creative content app. All videos stream via official YouTube integrations.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedButton(onClick = { onShowMessage("Thank you for rating Kabir Tech Lab ★") }) {
                    Text(if (lang == "en") "Rate & Share App" else "অ্যাপটি রেটিং ও শেয়ার করুন")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    prefs: UserPreferences,
    adsEnabled: Boolean,
    maintenanceMode: Boolean,
    onToggleAds: () -> Unit,
    onToggleMaintenance: () -> Unit,
    onPublishAnnouncement: (String, String, String, String) -> Unit,
    onBack: () -> Unit,
) {
    val strings = stringsFor(prefs.language)
    var titleBn by remember { mutableStateOf("") }
    var titleEn by remember { mutableStateOf("") }
    var bodyBn by remember { mutableStateOf("") }
    var bodyEn by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.adminPanelTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                },
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                GlassCard(highlightBorder = true) {
                    Text("Channel Analytics Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Active Users: 14,820 • Daily Quizzes: 3,240 • Story Completions: 8,910", color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(10.dp))
                    Canvas(modifier = Modifier.fillMaxWidth().height(90.dp)) {
                        val values = listOf(0.45f, 0.72f, 0.6f, 0.88f, 0.95f, 0.78f, 1.0f)
                        val barW = size.width / (values.size * 1.6f)
                        values.forEachIndexed { idx, v ->
                            val x = idx * barW * 1.6f + 8f
                            val h = size.height * v
                            drawRect(
                                color = ElectricCyan,
                                topLeft = Offset(x, size.height - h),
                                size = Size(barW, h),
                            )
                        }
                    }
                }
            }

            item {
                GlassCard {
                    Text("Pin New Announcement to Home", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = titleBn, onValueChange = { titleBn = it }, label = { Text("Bangla Title") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = titleEn, onValueChange = { titleEn = it }, label = { Text("English Title") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = bodyBn, onValueChange = { bodyBn = it }, label = { Text("Bangla Body") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = bodyEn, onValueChange = { bodyEn = it }, label = { Text("English Body") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (titleBn.isNotBlank() || titleEn.isNotBlank()) {
                                onPublishAnnouncement(titleBn, titleEn, bodyBn, bodyEn)
                                titleBn = ""; titleEn = ""; bodyBn = ""; bodyEn = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text("Publish Pinned Announcement") }
                }
            }

            item {
                GlassCard {
                    Text("Remote Config & Feature Flags", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("In-App Ads Enabled")
                        Switch(checked = adsEnabled, onCheckedChange = { onToggleAds() })
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Maintenance Mode Preview")
                        Switch(checked = maintenanceMode, onCheckedChange = { onToggleMaintenance() })
                    }
                }
            }

            item {
                GlassCard {
                    Text("Recent Notifications Queue (${MockChannelData.sampleNotifications.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    MockChannelData.sampleNotifications.forEach { n ->
                        Text("• ${n.titleEn} (${n.timeLabel})", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
