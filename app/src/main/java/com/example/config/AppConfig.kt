package com.example.config

import com.example.BuildConfig

/**
 * Centralized configuration for Kabir Tech Lab.
 * Replace placeholder channel/social links below with your live URLs,
 * and set YOUTUBE_API_KEY in the AI Studio Secrets panel (never hardcode secrets here).
 */
object AppConfig {
    // YouTube Data API v3 key injected from .env / Secrets panel via BuildConfig
    val youtubeApiKey: String
        get() = BuildConfig.YOUTUBE_API_KEY

    // Replace with your YouTube Channel ID (e.g., "UCxxxxxxxxxxxxxxxxxxxxxx")
    const val CHANNEL_ID = "UC_KABIR_TECH_LAB_CHANNEL_ID"

    // Admin email addresses authorized for the Admin Dashboard & Firestore admin rules
    val ADMIN_EMAILS = setOf("kabirtechlab26@gmail.com")

    // Community & Support Links (editable from here or synced via Admin Remote Config)
    const val YOUTUBE_CHANNEL_LINK = "https://www.youtube.com/@kabirtechlab"
    const val WHATSAPP_LINK = "https://whatsapp.com/channel/kabirtechlab"
    const val TELEGRAM_LINK = "https://t.me/kabirtechlab"
    const val FACEBOOK_LINK = "https://facebook.com/kabirtechlab"
    const val INSTAGRAM_LINK = "https://instagram.com/kabirtechlab"
    const val DONATION_LINK = "https://buymeacoffee.com/kabirtechlab"

    // Daily AI Assistant free message limit per user
    const val DAILY_AI_MESSAGE_LIMIT = 25

    val hasLiveYouTubeKey: Boolean
        get() = youtubeApiKey.isNotBlank() && youtubeApiKey != "YOUTUBE_API_KEY_PLACEHOLDER"
}
