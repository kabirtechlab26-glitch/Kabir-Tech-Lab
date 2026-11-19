package com.example.data.repository

import android.util.Log
import com.example.R
import com.example.config.AppConfig
import com.example.data.mock.MockChannelData
import com.example.data.model.VideoChapter
import com.example.data.model.VideoItem
import com.example.data.remote.YouTubeApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class YouTubeRepository {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttp = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val api: YouTubeApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/youtube/v3/")
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(YouTubeApiService::class.java)
    }

    private var cachedVideos: List<VideoItem> = MockChannelData.videos
    private var lastEtag: String? = null
    private var lastFetchMs: Long = 0L

    suspend fun fetchVideos(forceRefresh: Boolean = false): Pair<List<VideoItem>, Boolean> =
        withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            // Respect quota: cache for 5 minutes unless force refreshed
            if (!forceRefresh && now - lastFetchMs < 300_000L && cachedVideos.isNotEmpty()) {
                return@withContext cachedVideos to false
            }

            if (!AppConfig.hasLiveYouTubeKey || AppConfig.CHANNEL_ID.startsWith("UC_KABIR")) {
                lastFetchMs = now
                return@withContext MockChannelData.videos to false
            }

            try {
                val response = api.getChannelVideos(
                    etag = lastEtag,
                    channelId = AppConfig.CHANNEL_ID,
                    apiKey = AppConfig.youtubeApiKey,
                )
                if (response.code() == 304) {
                    lastFetchMs = now
                    return@withContext cachedVideos to true
                }
                if (response.isSuccessful) {
                    lastEtag = response.headers()["ETag"]
                    val items = response.body()?.items.orEmpty().mapNotNull { item ->
                        val vidId = item.id.videoId ?: return@mapNotNull null
                        val desc = item.snippet.description
                        VideoItem(
                            id = "yt_$vidId",
                            youtubeId = vidId,
                            titleBn = item.snippet.title,
                            titleEn = item.snippet.title,
                            descriptionBn = desc.ifBlank { item.snippet.title },
                            descriptionEn = desc.ifBlank { item.snippet.title },
                            category = guessCategory(item.snippet.title),
                            durationSeconds = 600,
                            durationLabel = "10:00",
                            viewsCount = 1200,
                            likesCount = 140,
                            publishedLabelBn = item.snippet.publishedAt.take(10),
                            publishedLabelEn = item.snippet.publishedAt.take(10),
                            uploadDateDaysAgo = 1,
                            chapters = parseChaptersFromDescription(desc),
                            thumbnailRes = R.drawable.img_hero_tech_lab,
                            thumbnailUrl = item.snippet.thumbnails?.high?.url
                                ?: item.snippet.thumbnails?.medium?.url.orEmpty(),
                        )
                    }
                    if (items.isNotEmpty()) {
                        cachedVideos = items
                        lastFetchMs = now
                        return@withContext items to true
                    }
                }
            } catch (e: Exception) {
                Log.w("YouTubeRepo", "Falling back to cached/mock channel videos: ${e.message}")
            }
            MockChannelData.videos to false
        }

    private fun guessCategory(title: String): String {
        val lower = title.lowercase()
        return when {
            lower.contains("short") || lower.contains("#shorts") -> "Shorts"
            lower.contains("ai") || lower.contains("gemini") -> "AI Tools"
            lower.contains("golpo") || lower.contains("গল্প") || lower.contains("story") -> "Stories"
            lower.contains("live") -> "Live"
            else -> "Tech Tutorials"
        }
    }

    companion object {
        private val timestampRegex = Regex("""(\d{1,2}):(\d{2})\s+([^\n,]+)""")

        fun parseChaptersFromDescription(description: String): List<VideoChapter> {
            return timestampRegex.findAll(description).map { match ->
                val min = match.groupValues[1].toIntOrNull() ?: 0
                val sec = match.groupValues[2].toIntOrNull() ?: 0
                val title = match.groupValues[3].trim()
                val label = "${match.groupValues[1].padStart(2, '0')}:${match.groupValues[2]}"
                VideoChapter(min * 60 + sec, label, title, title)
            }.toList()
        }

        // Banglish-to-Bangla fuzzy search normalizer
        fun matchesSmartQuery(query: String, vararg fields: String): Boolean {
            val q = query.trim().lowercase()
            if (q.isBlank()) return true
            val banglishMap = mapOf(
                "premer" to listOf("প্রেম", "ভালোবাসা", "বৃষ্টি", "romantic"),
                "golpo" to listOf("গল্প", "story", "পর্ব"),
                "bhalobasha" to listOf("ভালোবাসা", "love", "romantic"),
                "brishti" to listOf("বৃষ্টি", "rain"),
                "android" to listOf("অ্যান্ড্রয়েড", "android", "compose"),
                "bangla" to listOf("বাংলা", "bangla"),
                "ai" to listOf("এআই", "ai", "gemini"),
                "koster" to listOf("কষ্ট", "sad", "অশ্রু"),
            )
            val expanded = mutableListOf(q)
            q.split(" ").forEach { token ->
                banglishMap[token]?.let { expanded.addAll(it) }
            }
            return fields.any { field ->
                val lowerField = field.lowercase()
                expanded.any { keyword -> lowerField.contains(keyword) }
            }
        }
    }
}
