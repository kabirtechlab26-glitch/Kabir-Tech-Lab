package com.example.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "kabir_tech_lab_prefs")

data class UserPreferences(
    val onboardingCompleted: Boolean = false,
    val offlinePreviewMode: Boolean = false,
    val language: String = "bn",
    val useBanglaNumerals: Boolean = true,
    val themeMode: String = "dark",
    val accentKey: String = "cyan",
    val seasonalKey: String = "none",
    val fontScale: Float = 1.0f,
    val highContrast: Boolean = false,
    val reducedMotion: Boolean = false,
    val dataSaver: Boolean = false,
    val autoplayNext: Boolean = true,
    val wifiOnlyDownloads: Boolean = true,
    val appLockPin: String = "",
    val favoriteCategories: Set<String> = setOf("Tech Tutorials", "AI Tools", "Stories"),
    val localXp: Int = 120,
    val streakDays: Int = 3,
    val lastCheckInDay: String = "",
    val aiCallsToday: Int = 0,
    val aiCallDate: String = "",
    val savedIds: Set<String> = emptySet(),
    val likedIds: Set<String> = emptySet(),
    val downloadedIds: Set<String> = emptySet(),
    val hiddenHomeSections: Set<String> = emptySet(),
    val homeSectionOrder: String = "live,hero,continue,trending,tips,story,tool,prompt,streak,premieres",
    val recentSearches: Set<String> = emptySet(),
    val completedLessons: Set<String> = emptySet(),
    val masteredFlashcards: Set<String> = emptySet(),
    val notificationsEnabled: Boolean = true,
    val quietHoursEnabled: Boolean = false,
    val dailyTipTime: String = "09:00 AM",
    val subscribedTopics: Set<String> = setOf("Tech", "Stories", "AI"),
)

class PreferencesRepository(private val context: Context) {
    private object Keys {
        val ONBOARDING = booleanPreferencesKey("onboarding_completed")
        val OFFLINE_PREVIEW = booleanPreferencesKey("offline_preview")
        val LANGUAGE = stringPreferencesKey("language")
        val BANGLA_NUMERALS = booleanPreferencesKey("bangla_numerals")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val ACCENT_KEY = stringPreferencesKey("accent_key")
        val SEASONAL_KEY = stringPreferencesKey("seasonal_key")
        val FONT_SCALE = floatPreferencesKey("font_scale")
        val HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
        val REDUCED_MOTION = booleanPreferencesKey("reduced_motion")
        val DATA_SAVER = booleanPreferencesKey("data_saver")
        val AUTOPLAY_NEXT = booleanPreferencesKey("autoplay_next")
        val WIFI_ONLY = booleanPreferencesKey("wifi_only")
        val APP_LOCK_PIN = stringPreferencesKey("app_lock_pin")
        val FAV_CATEGORIES = stringSetPreferencesKey("fav_categories")
        val LOCAL_XP = intPreferencesKey("local_xp")
        val STREAK_DAYS = intPreferencesKey("streak_days")
        val LAST_CHECKIN = stringPreferencesKey("last_checkin")
        val AI_CALLS_TODAY = intPreferencesKey("ai_calls_today")
        val AI_CALL_DATE = stringPreferencesKey("ai_call_date")
        val SAVED_IDS = stringSetPreferencesKey("saved_ids")
        val LIKED_IDS = stringSetPreferencesKey("liked_ids")
        val DOWNLOADED_IDS = stringSetPreferencesKey("downloaded_ids")
        val HIDDEN_SECTIONS = stringSetPreferencesKey("hidden_sections")
        val SECTION_ORDER = stringPreferencesKey("section_order")
        val RECENT_SEARCHES = stringSetPreferencesKey("recent_searches")
        val COMPLETED_LESSONS = stringSetPreferencesKey("completed_lessons")
        val MASTERED_CARDS = stringSetPreferencesKey("mastered_cards")
        val NOTIFS_ENABLED = booleanPreferencesKey("notifs_enabled")
        val QUIET_HOURS = booleanPreferencesKey("quiet_hours")
        val DAILY_TIP_TIME = stringPreferencesKey("daily_tip_time")
        val TOPICS = stringSetPreferencesKey("topics")
    }

    val preferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            onboardingCompleted = prefs[Keys.ONBOARDING] ?: false,
            offlinePreviewMode = prefs[Keys.OFFLINE_PREVIEW] ?: false,
            language = prefs[Keys.LANGUAGE] ?: "bn",
            useBanglaNumerals = prefs[Keys.BANGLA_NUMERALS] ?: true,
            themeMode = prefs[Keys.THEME_MODE] ?: "dark",
            accentKey = prefs[Keys.ACCENT_KEY] ?: "cyan",
            seasonalKey = prefs[Keys.SEASONAL_KEY] ?: "none",
            fontScale = prefs[Keys.FONT_SCALE] ?: 1.0f,
            highContrast = prefs[Keys.HIGH_CONTRAST] ?: false,
            reducedMotion = prefs[Keys.REDUCED_MOTION] ?: false,
            dataSaver = prefs[Keys.DATA_SAVER] ?: false,
            autoplayNext = prefs[Keys.AUTOPLAY_NEXT] ?: true,
            wifiOnlyDownloads = prefs[Keys.WIFI_ONLY] ?: true,
            appLockPin = prefs[Keys.APP_LOCK_PIN] ?: "",
            favoriteCategories = prefs[Keys.FAV_CATEGORIES] ?: setOf("Tech Tutorials", "AI Tools", "Stories"),
            localXp = prefs[Keys.LOCAL_XP] ?: 120,
            streakDays = prefs[Keys.STREAK_DAYS] ?: 3,
            lastCheckInDay = prefs[Keys.LAST_CHECKIN] ?: "",
            aiCallsToday = prefs[Keys.AI_CALLS_TODAY] ?: 0,
            aiCallDate = prefs[Keys.AI_CALL_DATE] ?: "",
            savedIds = prefs[Keys.SAVED_IDS] ?: emptySet(),
            likedIds = prefs[Keys.LIKED_IDS] ?: emptySet(),
            downloadedIds = prefs[Keys.DOWNLOADED_IDS] ?: emptySet(),
            hiddenHomeSections = prefs[Keys.HIDDEN_SECTIONS] ?: emptySet(),
            homeSectionOrder = prefs[Keys.SECTION_ORDER]
                ?: "live,hero,continue,trending,tips,story,tool,prompt,streak,premieres",
            recentSearches = prefs[Keys.RECENT_SEARCHES] ?: emptySet(),
            completedLessons = prefs[Keys.COMPLETED_LESSONS] ?: emptySet(),
            masteredFlashcards = prefs[Keys.MASTERED_CARDS] ?: emptySet(),
            notificationsEnabled = prefs[Keys.NOTIFS_ENABLED] ?: true,
            quietHoursEnabled = prefs[Keys.QUIET_HOURS] ?: false,
            dailyTipTime = prefs[Keys.DAILY_TIP_TIME] ?: "09:00 AM",
            subscribedTopics = prefs[Keys.TOPICS] ?: setOf("Tech", "Stories", "AI"),
        )
    }

    suspend fun completeOnboarding(
        language: String,
        categories: Set<String>,
        offlinePreview: Boolean
    ) {
        context.dataStore.edit {
            it[Keys.ONBOARDING] = true
            it[Keys.LANGUAGE] = language
            it[Keys.FAV_CATEGORIES] = categories
            it[Keys.OFFLINE_PREVIEW] = offlinePreview
        }
    }

    suspend fun setOfflinePreviewMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.OFFLINE_PREVIEW] = enabled }
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { it[Keys.LANGUAGE] = language }
    }

    suspend fun setBanglaNumerals(enabled: Boolean) {
        context.dataStore.edit { it[Keys.BANGLA_NUMERALS] = enabled }
    }

    suspend fun setThemeSettings(
        themeMode: String? = null,
        accentKey: String? = null,
        seasonalKey: String? = null,
        fontScale: Float? = null,
        highContrast: Boolean? = null,
        reducedMotion: Boolean? = null,
    ) {
        context.dataStore.edit {
            if (themeMode != null) it[Keys.THEME_MODE] = themeMode
            if (accentKey != null) it[Keys.ACCENT_KEY] = accentKey
            if (seasonalKey != null) it[Keys.SEASONAL_KEY] = seasonalKey
            if (fontScale != null) it[Keys.FONT_SCALE] = fontScale
            if (highContrast != null) it[Keys.HIGH_CONTRAST] = highContrast
            if (reducedMotion != null) it[Keys.REDUCED_MOTION] = reducedMotion
        }
    }

    suspend fun setPlaybackAndNetworkSettings(
        dataSaver: Boolean? = null,
        autoplayNext: Boolean? = null,
        wifiOnly: Boolean? = null,
        pin: String? = null,
    ) {
        context.dataStore.edit {
            if (dataSaver != null) it[Keys.DATA_SAVER] = dataSaver
            if (autoplayNext != null) it[Keys.AUTOPLAY_NEXT] = autoplayNext
            if (wifiOnly != null) it[Keys.WIFI_ONLY] = wifiOnly
            if (pin != null) it[Keys.APP_LOCK_PIN] = pin
        }
    }

    suspend fun claimDailyCheckIn(todayKey: String, bonusXp: Int = 25): Boolean {
        var claimed = false
        context.dataStore.edit { prefs ->
            if (prefs[Keys.LAST_CHECKIN] != todayKey) {
                prefs[Keys.LAST_CHECKIN] = todayKey
                prefs[Keys.STREAK_DAYS] = (prefs[Keys.STREAK_DAYS] ?: 3) + 1
                prefs[Keys.LOCAL_XP] = (prefs[Keys.LOCAL_XP] ?: 120) + bonusXp
                claimed = true
            }
        }
        return claimed
    }

    suspend fun addLocalXp(points: Int) {
        context.dataStore.edit {
            it[Keys.LOCAL_XP] = (it[Keys.LOCAL_XP] ?: 120) + points
        }
    }

    suspend fun incrementAiUsage(todayKey: String): Int {
        var count = 0
        context.dataStore.edit { prefs ->
            val current = if (prefs[Keys.AI_CALL_DATE] == todayKey) prefs[Keys.AI_CALLS_TODAY] ?: 0 else 0
            count = current + 1
            prefs[Keys.AI_CALL_DATE] = todayKey
            prefs[Keys.AI_CALLS_TODAY] = count
        }
        return count
    }

    suspend fun toggleSaved(id: String) {
        context.dataStore.edit { prefs ->
            val set = prefs[Keys.SAVED_IDS].orEmpty().toMutableSet()
            if (!set.add(id)) set.remove(id)
            prefs[Keys.SAVED_IDS] = set
        }
    }

    suspend fun toggleLiked(id: String) {
        context.dataStore.edit { prefs ->
            val set = prefs[Keys.LIKED_IDS].orEmpty().toMutableSet()
            if (!set.add(id)) set.remove(id)
            prefs[Keys.LIKED_IDS] = set
        }
    }

    suspend fun markDownloaded(id: String) {
        context.dataStore.edit { prefs ->
            val set = prefs[Keys.DOWNLOADED_IDS].orEmpty().toMutableSet()
            set.add(id)
            prefs[Keys.DOWNLOADED_IDS] = set
        }
    }

    suspend fun toggleHomeSectionVisibility(key: String) {
        context.dataStore.edit { prefs ->
            val set = prefs[Keys.HIDDEN_SECTIONS].orEmpty().toMutableSet()
            if (!set.add(key)) set.remove(key)
            prefs[Keys.HIDDEN_SECTIONS] = set
        }
    }

    suspend fun setHomeSectionOrder(order: List<String>) {
        context.dataStore.edit { it[Keys.SECTION_ORDER] = order.joinToString(",") }
    }

    suspend fun addRecentSearch(query: String) {
        if (query.isBlank()) return
        context.dataStore.edit { prefs ->
            val list = prefs[Keys.RECENT_SEARCHES].orEmpty().toMutableList()
            list.remove(query)
            list.add(0, query)
            prefs[Keys.RECENT_SEARCHES] = list.take(8).toSet()
        }
    }

    suspend fun toggleLessonComplete(lessonId: String) {
        context.dataStore.edit { prefs ->
            val set = prefs[Keys.COMPLETED_LESSONS].orEmpty().toMutableSet()
            if (!set.add(lessonId)) set.remove(lessonId)
            prefs[Keys.COMPLETED_LESSONS] = set
        }
    }

    suspend fun toggleFlashcardMastered(cardId: String) {
        context.dataStore.edit { prefs ->
            val set = prefs[Keys.MASTERED_CARDS].orEmpty().toMutableSet()
            if (!set.add(cardId)) set.remove(cardId)
            prefs[Keys.MASTERED_CARDS] = set
        }
    }

    suspend fun setNotificationPreferences(
        enabled: Boolean? = null,
        quietHours: Boolean? = null,
        dailyTipTime: String? = null,
        topicToggle: String? = null,
    ) {
        context.dataStore.edit { prefs ->
            if (enabled != null) prefs[Keys.NOTIFS_ENABLED] = enabled
            if (quietHours != null) prefs[Keys.QUIET_HOURS] = quietHours
            if (dailyTipTime != null) prefs[Keys.DAILY_TIP_TIME] = dailyTipTime
            if (topicToggle != null) {
                val topics = prefs[Keys.TOPICS].orEmpty().toMutableSet()
                if (!topics.add(topicToggle)) topics.remove(topicToggle)
                prefs[Keys.TOPICS] = topics
            }
        }
    }
}
