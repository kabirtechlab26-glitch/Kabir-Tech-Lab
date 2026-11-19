package com.example.data.remote

import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class YouTubeSearchResponse(
    val nextPageToken: String? = null,
    val items: List<YouTubeSearchItem> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class YouTubeSearchItem(
    val id: YouTubeSearchId = YouTubeSearchId(),
    val snippet: YouTubeSnippet = YouTubeSnippet(),
)

@JsonClass(generateAdapter = true)
data class YouTubeSearchId(
    val videoId: String? = null,
)

@JsonClass(generateAdapter = true)
data class YouTubeSnippet(
    val title: String = "",
    val description: String = "",
    val publishedAt: String = "",
    val thumbnails: YouTubeThumbnails? = null,
)

@JsonClass(generateAdapter = true)
data class YouTubeThumbnails(
    val high: YouTubeThumbnailDetail? = null,
    val medium: YouTubeThumbnailDetail? = null,
)

@JsonClass(generateAdapter = true)
data class YouTubeThumbnailDetail(
    val url: String = "",
)

interface YouTubeApiService {
    @GET("search")
    suspend fun getChannelVideos(
        @Header("If-None-Match") etag: String? = null,
        @Query("part") part: String = "snippet",
        @Query("channelId") channelId: String,
        @Query("maxResults") maxResults: Int = 20,
        @Query("order") order: String = "date",
        @Query("type") type: String = "video",
        @Query("key") apiKey: String,
    ): Response<YouTubeSearchResponse>
}
