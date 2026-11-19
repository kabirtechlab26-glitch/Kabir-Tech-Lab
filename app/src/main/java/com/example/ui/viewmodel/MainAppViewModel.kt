package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.config.AppConfig
import com.example.data.mock.MockChannelData
import com.example.data.model.Announcement
import com.example.data.model.ChatMessage
import com.example.data.model.CommunityPost
import com.example.data.model.StoryPart
import com.example.data.model.StorySeries
import com.example.data.model.VideoAiSummary
import com.example.data.model.VideoItem
import com.example.data.model.VideoNote
import com.example.data.model.VideoRequest
import com.example.data.repository.AiRepository
import com.example.data.repository.PreferencesRepository
import com.example.data.repository.UserPreferences
import com.example.data.repository.YouTubeRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed interface AiGenerationState {
    data object Idle : AiGenerationState
    data object Loading : AiGenerationState
    data class Streaming(val text: String) : AiGenerationState
    data class Success(val text: String) : AiGenerationState
    data class Error(val message: String) : AiGenerationState
}

data class ActivePlayerState(
    val itemId: String,
    val titleBn: String,
    val titleEn: String,
    val youtubeId: String,
    val durationSeconds: Int,
    val positionSeconds: Int = 0,
    val playing: Boolean = true,
    val audioOnly: Boolean = false,
    val speed: Float = 1.0f,
    val loopEnabled: Boolean = false,
    val captionsEnabled: Boolean = true,
    val quality: String = "1080p",
    val sleepTimerMinutes: Int = 0,
    val storySeriesId: String? = null,
    val storyPartNumber: Int? = null,
)

class MainAppViewModel(
    private val prefsRepo: PreferencesRepository,
    private val youtubeRepo: YouTubeRepository = YouTubeRepository(),
    private val aiRepo: AiRepository = AiRepository(),
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = prefsRepo.preferences.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = UserPreferences()
    )

    private val _videos = MutableStateFlow(MockChannelData.videos)
    val videos: StateFlow<List<VideoItem>> = _videos.asStateFlow()

    private val _isRefreshingVideos = MutableStateFlow(false)
    val isRefreshingVideos: StateFlow<Boolean> = _isRefreshingVideos.asStateFlow()

    private val _stories = MutableStateFlow(MockChannelData.stories)
    val stories: StateFlow<List<StorySeries>> = _stories.asStateFlow()

    private val _activePlayer = MutableStateFlow<ActivePlayerState?>(null)
    val activePlayer: StateFlow<ActivePlayerState?> = _activePlayer.asStateFlow()

    // Local preview/guest fallback lists (merged with Firestore when signed in)
    private val _localNotes = MutableStateFlow<List<VideoNote>>(emptyList())
    val localNotes: StateFlow<List<VideoNote>> = _localNotes.asStateFlow()

    private val _localPosts = MutableStateFlow(MockChannelData.sampleCommunityPosts)
    val localPosts: StateFlow<List<CommunityPost>> = _localPosts.asStateFlow()

    private val _localRequests = MutableStateFlow(MockChannelData.sampleVideoRequests)
    val localRequests: StateFlow<List<VideoRequest>> = _localRequests.asStateFlow()

    private val _localAnnouncements = MutableStateFlow(MockChannelData.sampleAnnouncements)
    val localAnnouncements: StateFlow<List<Announcement>> = _localAnnouncements.asStateFlow()

    private val _pollVotes = MutableStateFlow<Map<String, String>>(emptyMap())
    val pollVotes: StateFlow<Map<String, String>> = _pollVotes.asStateFlow()

    private val _chatHistory = MutableStateFlow(
        listOf(
            ChatMessage(
                id = "welcome",
                role = "model",
                text = "আসসালামু আলাইকুম! আমি কবির টেক ল্যাবের Gemini AI অ্যাসিস্ট্যান্ট। টেক টিউটোরিয়াল, AI প্রম্পট, ইউটিউব স্ক্রিপ্ট বা বাংলা থেকে ইংরেজি যেকোনো প্রশ্ন করুন!",
                timestampLabel = "Now",
            )
        )
    )
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

    private val _aiState = MutableStateFlow<AiGenerationState>(AiGenerationState.Idle)
    val aiState: StateFlow<AiGenerationState> = _aiState.asStateFlow()

    private val _videoSummaryMap = MutableStateFlow<Map<String, Result<VideoAiSummary>>>(emptyMap())
    val videoSummaryMap: StateFlow<Map<String, Result<VideoAiSummary>>> = _videoSummaryMap.asStateFlow()

    private val _summaryLoadingId = MutableStateFlow<String?>(null)
    val summaryLoadingId: StateFlow<String?> = _summaryLoadingId.asStateFlow()

    private val _showConfetti = MutableStateFlow(false)
    val showConfetti: StateFlow<Boolean> = _showConfetti.asStateFlow()

    // Admin Remote Config flags (in-app previewable)
    private val _adsEnabled = MutableStateFlow(false)
    val adsEnabled: StateFlow<Boolean> = _adsEnabled.asStateFlow()

    private val _maintenanceMode = MutableStateFlow(false)
    val maintenanceMode: StateFlow<Boolean> = _maintenanceMode.asStateFlow()

    init {
        refreshVideos(force = false)
        startPlaybackTicker()
    }

    private fun startPlaybackTicker() {
        viewModelScope.launch {
            while (true) {
                delay(1000L)
                val current = _activePlayer.value
                if (current != null && current.playing) {
                    val nextPos = (current.positionSeconds + 1).coerceAtMost(current.durationSeconds)
                    _activePlayer.value = current.copy(positionSeconds = nextPos)
                }
            }
        }
    }

    fun refreshVideos(force: Boolean = true) {
        viewModelScope.launch {
            _isRefreshingVideos.value = true
            val (list, _) = youtubeRepo.fetchVideos(forceRefresh = force)
            _videos.value = list
            _isRefreshingVideos.value = false
        }
    }

    fun completeOnboarding(language: String, categories: Set<String>, offlinePreview: Boolean) {
        viewModelScope.launch {
            prefsRepo.completeOnboarding(language, categories, offlinePreview)
        }
    }

    fun setOfflinePreviewMode(enabled: Boolean) {
        viewModelScope.launch { prefsRepo.setOfflinePreviewMode(enabled) }
    }

    fun toggleLanguage() {
        viewModelScope.launch {
            val next = if (preferences.value.language == "bn") "en" else "bn"
            prefsRepo.setLanguage(next)
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch { prefsRepo.setLanguage(lang) }
    }

    fun setBanglaNumerals(enabled: Boolean) {
        viewModelScope.launch { prefsRepo.setBanglaNumerals(enabled) }
    }

    fun updateTheme(
        themeMode: String? = null,
        accentKey: String? = null,
        seasonalKey: String? = null,
        fontScale: Float? = null,
        highContrast: Boolean? = null,
        reducedMotion: Boolean? = null,
    ) {
        viewModelScope.launch {
            prefsRepo.setThemeSettings(
                themeMode,
                accentKey,
                seasonalKey,
                fontScale,
                highContrast,
                reducedMotion
            )
        }
    }

    fun updatePlaybackSettings(
        dataSaver: Boolean? = null,
        autoplayNext: Boolean? = null,
        wifiOnly: Boolean? = null,
        pin: String? = null,
    ) {
        viewModelScope.launch {
            prefsRepo.setPlaybackAndNetworkSettings(dataSaver, autoplayNext, wifiOnly, pin)
        }
    }

    fun updateNotificationPrefs(
        enabled: Boolean? = null,
        quietHours: Boolean? = null,
        dailyTipTime: String? = null,
        topicToggle: String? = null,
    ) {
        viewModelScope.launch {
            prefsRepo.setNotificationPreferences(enabled, quietHours, dailyTipTime, topicToggle)
        }
    }

    fun claimDailyCheckIn(onResult: (Boolean, Boolean) -> Unit) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        viewModelScope.launch {
            val isSurpriseDay = (preferences.value.streakDays + 1) % 7 == 0
            val bonus = if (isSurpriseDay) 75 else 25
            val claimed = prefsRepo.claimDailyCheckIn(today, bonus)
            if (claimed) {
                triggerConfetti()
            }
            onResult(claimed, isSurpriseDay)
        }
    }

    fun triggerConfetti() {
        viewModelScope.launch {
            _showConfetti.value = true
            delay(2200L)
            _showConfetti.value = false
        }
    }

    fun addXp(points: Int) {
        viewModelScope.launch { prefsRepo.addLocalXp(points) }
    }

    fun toggleSaved(id: String) {
        viewModelScope.launch { prefsRepo.toggleSaved(id) }
    }

    fun toggleLiked(id: String) {
        viewModelScope.launch { prefsRepo.toggleLiked(id) }
    }

    fun markDownloaded(id: String) {
        viewModelScope.launch { prefsRepo.markDownloaded(id) }
    }

    fun toggleHomeSection(key: String) {
        viewModelScope.launch { prefsRepo.toggleHomeSectionVisibility(key) }
    }

    fun moveHomeSection(key: String, direction: Int) {
        val list = preferences.value.homeSectionOrder.split(",").filter { it.isNotBlank() }.toMutableList()
        val idx = list.indexOf(key)
        if (idx == -1) return
        val newIdx = (idx + direction).coerceIn(0, list.lastIndex)
        if (newIdx != idx) {
            list.removeAt(idx)
            list.add(newIdx, key)
            viewModelScope.launch { prefsRepo.setHomeSectionOrder(list) }
        }
    }

    fun addRecentSearch(query: String) {
        viewModelScope.launch { prefsRepo.addRecentSearch(query) }
    }

    fun toggleLessonComplete(id: String) {
        viewModelScope.launch {
            prefsRepo.toggleLessonComplete(id)
            prefsRepo.addLocalXp(20)
        }
    }

    fun toggleFlashcardMastered(id: String) {
        viewModelScope.launch {
            prefsRepo.toggleFlashcardMastered(id)
            prefsRepo.addLocalXp(10)
        }
    }

    fun playVideo(video: VideoItem, startSeconds: Int = 0) {
        _activePlayer.value = ActivePlayerState(
            itemId = video.id,
            titleBn = video.titleBn,
            titleEn = video.titleEn,
            youtubeId = video.youtubeId,
            durationSeconds = video.durationSeconds,
            positionSeconds = startSeconds.coerceIn(0, video.durationSeconds),
            playing = true,
        )
    }

    fun playStoryPart(series: StorySeries, part: StoryPart, audioOnly: Boolean = true) {
        _activePlayer.value = ActivePlayerState(
            itemId = part.id,
            titleBn = "${series.titleBn} • ${part.titleBn}",
            titleEn = "${series.titleEn} • ${part.titleEn}",
            youtubeId = part.youtubeId,
            durationSeconds = part.durationSeconds,
            positionSeconds = 0,
            playing = true,
            audioOnly = audioOnly,
            storySeriesId = series.id,
            storyPartNumber = part.partNumber,
        )
    }

    fun updatePlayer(transform: (ActivePlayerState) -> ActivePlayerState) {
        _activePlayer.value = _activePlayer.value?.let(transform)
    }

    fun closePlayer() {
        _activePlayer.value = null
    }

    fun votePoll(pollId: String, optionId: String) {
        _pollVotes.value = _pollVotes.value + (pollId to optionId)
    }

    fun addLocalNote(videoId: String, videoTitle: String, ts: Long, content: String) {
        val note = VideoNote(
            id = "local_${System.currentTimeMillis()}",
            videoId = videoId,
            videoTitle = videoTitle,
            timestampSeconds = ts,
            content = content,
        )
        _localNotes.value = listOf(note) + _localNotes.value
    }

    fun addLocalPost(author: String, type: String, title: String, body: String) {
        val post = CommunityPost(
            id = "local_post_${System.currentTimeMillis()}",
            authorId = "local_user",
            authorName = author,
            postType = type,
            title = title,
            body = body,
            likesCount = 0,
            repliesCount = 0,
            approved = true,
            reported = false,
        )
        _localPosts.value = listOf(post) + _localPosts.value
    }

    fun addLocalRequest(author: String, title: String, category: String, desc: String) {
        val req = VideoRequest(
            id = "local_req_${System.currentTimeMillis()}",
            userId = "local_user",
            authorName = author,
            title = title,
            category = category,
            description = desc,
            upvotes = 1,
            status = "OPEN",
            visibility = "PUBLIC",
        )
        _localRequests.value = listOf(req) + _localRequests.value
    }

    fun toggleAdsEnabled() { _adsEnabled.value = !_adsEnabled.value }
    fun toggleMaintenanceMode() { _maintenanceMode.value = !_maintenanceMode.value }

    // --- GEMINI AI FEATURES (Firebase AI Logic) ---
    fun sendAiMessage(prompt: String, videoContext: String? = null) {
        val clean = prompt.trim()
        if (clean.isBlank()) return
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val currentCount = if (preferences.value.aiCallDate == today) preferences.value.aiCallsToday else 0
        if (currentCount >= AppConfig.DAILY_AI_MESSAGE_LIMIT) {
            _aiState.value = AiGenerationState.Error(
                if (preferences.value.language == "en")
                    "Daily free AI limit (${AppConfig.DAILY_AI_MESSAGE_LIMIT}) reached. Please try again tomorrow!"
                else
                    "আজকের ফ্রি এআই সীমা (${AppConfig.DAILY_AI_MESSAGE_LIMIT}) পূর্ণ হয়েছে। আগামীকাল আবার চেষ্টা করুন!"
            )
            return
        }

        val timeLabel = SimpleDateFormat("hh:mm a", Locale.US).format(Date())
        _chatHistory.value = _chatHistory.value + ChatMessage(
            id = "u_${System.currentTimeMillis()}",
            role = "user",
            text = clean,
            timestampLabel = timeLabel,
        )

        viewModelScope.launch {
            prefsRepo.incrementAiUsage(today)
            _aiState.value = AiGenerationState.Loading
            var finalReply = ""
            try {
                aiRepo.streamAssistantReply(
                    prompt = clean,
                    language = preferences.value.language,
                    videoContext = videoContext
                ).collect { accumulated ->
                    finalReply = accumulated
                    _aiState.value = AiGenerationState.Streaming(accumulated)
                }
                _chatHistory.value = _chatHistory.value + ChatMessage(
                    id = "m_${System.currentTimeMillis()}",
                    role = "model",
                    text = finalReply,
                    timestampLabel = SimpleDateFormat("hh:mm a", Locale.US).format(Date()),
                )
                _aiState.value = AiGenerationState.Success(finalReply)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("MainAppVM", "AI chat error", e)
                _aiState.value = AiGenerationState.Error(
                    e.localizedMessage ?: "Failed to generate AI response."
                )
            }
        }
    }

    fun generateVideoSummary(video: VideoItem) {
        viewModelScope.launch {
            _summaryLoadingId.value = video.id
            val lang = preferences.value.language
            val title = if (lang == "en") video.titleEn else video.titleBn
            val desc = if (lang == "en") video.descriptionEn else video.descriptionBn
            val result = aiRepo.generateVideoSummary(title, desc, lang)
            _videoSummaryMap.value = _videoSummaryMap.value + (video.id to result)
            _summaryLoadingId.value = null
        }
    }

    suspend fun generateCreatorToolAi(prompt: String): Result<String> {
        return aiRepo.generateCreatorOutput(prompt, preferences.value.language)
    }
}
