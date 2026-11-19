package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.R
import com.example.i18n.stringsFor
import com.example.ui.auth.GoogleAuthHelper
import com.example.ui.components.GlassCard
import com.example.ui.theme.DeepBlue900
import com.example.ui.theme.DeepBlue950
import com.example.ui.theme.ElectricCyan
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AnimatedSplashScreen(
    language: String,
    onFinished: () -> Unit,
) {
    val scale = remember { Animatable(0.65f) }
    val strings = stringsFor(language)

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing)
        )
        delay(650L)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(DeepBlue950, DeepBlue900, Color(0xFF0B2244)))
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scale.value)
                .padding(32.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_kabir_logo_fg),
                contentDescription = "Kabir Tech Lab Logo",
                modifier = Modifier
                    .size(116.dp)
                    .clip(RoundedCornerShape(28.dp)),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = strings.appName,
                style = MaterialTheme.typography.displaySmall,
                color = ElectricCyan,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = strings.tagline,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingAndAuthScreen(
    initialLanguage: String,
    onboardingAlreadyCompleted: Boolean,
    onLanguageChange: (String) -> Unit,
    onAuthSuccess: (selectedLang: String, selectedCategories: Set<String>) -> Unit,
    onContinueOfflinePreview: (selectedLang: String, selectedCategories: Set<String>) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    var selectedLang by remember { mutableStateOf(initialLanguage) }
    var selectedCategories by remember {
        mutableStateOf(setOf("Tech Tutorials", "AI Tools", "Stories"))
    }
    var notificationGranted by remember { mutableStateOf(false) }
    var isSigningIn by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }

    val strings = stringsFor(selectedLang)
    val startPage = if (onboardingAlreadyCompleted) 3 else 0
    val pagerState = rememberPagerState(initialPage = startPage, pageCount = { 4 })

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        notificationGranted = granted
    }

    val allCategories = listOf("Tech Tutorials", "AI Tools", "Stories", "Tips", "Shorts", "Live")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.background,
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Top Language Switch + Skip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedLang == "bn",
                        onClick = {
                            selectedLang = "bn"
                            onLanguageChange("bn")
                        },
                        label = { Text("বাংলা") },
                        modifier = Modifier.testTag("onboard_lang_bn"),
                    )
                    FilterChip(
                        selected = selectedLang == "en",
                        onClick = {
                            selectedLang = "en"
                            onLanguageChange("en")
                        },
                        label = { Text("English") },
                        modifier = Modifier.testTag("onboard_lang_en"),
                    )
                }
                if (pagerState.currentPage < 3) {
                    TextButton(
                        onClick = { scope.launch { pagerState.animateScrollToPage(3) } },
                        modifier = Modifier.testTag("onboard_skip_btn"),
                    ) {
                        Text(strings.skip)
                    }
                }
            }

            // 4 Onboarding Slides
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    when (page) {
                        0 -> {
                            Image(
                                painter = painterResource(id = R.drawable.img_hero_tech_lab),
                                contentDescription = "Kabir Tech Lab Studio",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(190.dp)
                                    .clip(RoundedCornerShape(24.dp)),
                                contentScale = ContentScale.Crop,
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Icon(Icons.Default.PlayCircle, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (selectedLang == "en") "Tech Tutorials & AI Tool Guides" else "টেক টিউটোরিয়াল ও এআই টুল গাইড",
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (selectedLang == "en")
                                    "Watch curated YouTube lessons, copy code & prompts, download resources, and learn with Gemini AI summaries."
                                else
                                    "সাজানো ইউটিউব টিউটোরিয়াল দেখুন, কোড ও প্রম্পট কপি করুন, রিসোর্স ডাউনলোড করুন এবং Gemini AI সারসংক্ষেপ পান।",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                        }

                        1 -> {
                            Image(
                                painter = painterResource(id = R.drawable.img_story_rainy_dhaka),
                                contentDescription = "Bangla Audio Stories",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(190.dp)
                                    .clip(RoundedCornerShape(24.dp)),
                                contentScale = ContentScale.Crop,
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Icon(Icons.Default.MenuBook, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (selectedLang == "en") "Multi-Part Bangla Stories & Audio Mode" else "ধারাবাহিক বাংলা গল্প ও অডিও আসর",
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (selectedLang == "en")
                                    "Listen in Audio-Only bedtime mode with a sleep timer, or switch to Reading Mode with adjustable fonts."
                                else
                                    "স্লিপ টাইমার ও বেডটাইম মোডে গল্প শুনুন, অথবা আরামদায়ক ফন্টে পড়ার মোডে উপভোগ করুন।",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                        }

                        2 -> {
                            Icon(Icons.Default.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (selectedLang == "en") "Pick Your Interests & Alerts" else "আপনার পছন্দের বিষয় ও নোটিফিকেশন",
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                allCategories.forEach { cat ->
                                    val chosen = cat in selectedCategories
                                    FilterChip(
                                        selected = chosen,
                                        onClick = {
                                            selectedCategories = if (chosen && selectedCategories.size > 1) {
                                                selectedCategories - cat
                                            } else {
                                                selectedCategories + cat
                                            }
                                        },
                                        label = { Text(cat) },
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.NotificationsActive, null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (selectedLang == "en") "Stay Updated" else "নতুন আপডেট ও লাইভ অ্যালার্ট",
                                            style = MaterialTheme.typography.titleMedium,
                                        )
                                        Text(
                                            text = if (selectedLang == "en") "Get notified for new tutorials, stories & live Q&A." else "নতুন টিউটোরিয়াল, গল্প এবং লাইভের সময় নোটিফিকেশন পান।",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedButton(
                                    onClick = {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        } else {
                                            notificationGranted = true
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    if (notificationGranted) {
                                        Icon(Icons.Default.CheckCircle, null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (selectedLang == "en") "Notifications Enabled" else "নোটিফিকেশন চালু হয়েছে")
                                    } else {
                                        Text(if (selectedLang == "en") "Allow Notifications" else "নোটিফিকেশন অনুমতি দিন")
                                    }
                                }
                            }
                        }

                        else -> {
                            Image(
                                painter = painterResource(id = R.drawable.img_kabir_avatar),
                                contentDescription = "Kabir Tech Lab Creator",
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = strings.appName,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (selectedLang == "en")
                                    "Sign in with Google to sync notes, XP, playlists & join the community — or explore in Offline Preview Mode."
                                else
                                    "ক্লাউড সিঙ্ক, নোটস, XP ও কমিউনিটিতে যুক্ত হতে Google দিয়ে সাইন-ইন করুন — অথবা অফলাইন প্রিভিউ মোডে দেখুন।",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            if (authError != null) {
                                Surface(
                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.16f),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                ) {
                                    Text(
                                        text = authError.orEmpty(),
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(12.dp),
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    isSigningIn = true
                                    authError = null
                                    GoogleAuthHelper.onGoogleSignInClicked(
                                        context = context,
                                        credentialManager = credentialManager,
                                        scope = scope,
                                        onAuthSuccess = {
                                            isSigningIn = false
                                            onAuthSuccess(selectedLang, selectedCategories)
                                        },
                                        onAuthError = { msg ->
                                            isSigningIn = false
                                            authError = msg
                                        },
                                        onAuthCancelled = {
                                            isSigningIn = false
                                        }
                                    )
                                },
                                enabled = !isSigningIn,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("google_sign_in_button"),
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                if (isSigningIn) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(22.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp,
                                    )
                                } else {
                                    Text(strings.signInWithGoogle, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = {
                                    onContinueOfflinePreview(selectedLang, selectedCategories)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("offline_preview_button"),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            ) {
                                Text(strings.continueOfflinePreview)
                            }
                        }
                    }
                }
            }

            // Bottom Pager Dots + Next Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(4) { index ->
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(if (pagerState.currentPage == index) 24.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline
                                )
                                .clickable { scope.launch { pagerState.animateScrollToPage(index) } }
                        )
                    }
                }

                if (pagerState.currentPage < 3) {
                    Button(
                        onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.testTag("onboard_next_btn"),
                    ) {
                        Text(strings.next)
                    }
                }
            }
        }
    }
}
