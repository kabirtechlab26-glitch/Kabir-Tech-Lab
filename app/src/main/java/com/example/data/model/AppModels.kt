package com.example.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

data class VideoChapter(
    val timeSeconds: Int,
    val timeLabel: String,
    val titleBn: String,
    val titleEn: String,
)

data class ResourceItem(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val type: String, // PDF, CODE, PROMPT, LINK, TEMPLATE
    val content: String,
    val url: String = "",
    val sizeLabel: String = "1.2 MB",
)

data class VideoItem(
    val id: String,
    val youtubeId: String,
    val titleBn: String,
    val titleEn: String,
    val descriptionBn: String,
    val descriptionEn: String,
    val category: String, // Tech Tutorials, AI Tools, Tips, Shorts, Live
    val durationSeconds: Int,
    val durationLabel: String,
    val viewsCount: Long,
    val likesCount: Long,
    val publishedLabelBn: String,
    val publishedLabelEn: String,
    val uploadDateDaysAgo: Int,
    val shortVideo: Boolean = false,
    val liveStream: Boolean = false,
    val premiereScheduled: Boolean = false,
    val premiereTimeLabel: String = "",
    val tags: List<String> = emptyList(),
    val chapters: List<VideoChapter> = emptyList(),
    val resources: List<ResourceItem> = emptyList(),
    val sampleComments: List<Pair<String, String>> = emptyList(),
    @DrawableRes val thumbnailRes: Int,
    val thumbnailUrl: String = "",
)

data class StoryCharacter(
    val nameBn: String,
    val nameEn: String,
    val roleBn: String,
    val roleEn: String,
)

data class StoryPart(
    val id: String,
    val partNumber: Int,
    val titleBn: String,
    val titleEn: String,
    val durationSeconds: Int,
    val durationLabel: String,
    val youtubeId: String,
    val storyTextBn: String,
    val storyTextEn: String,
    val rating: Float = 4.9f,
    val reactionsCount: Int = 1240,
)

data class StorySeries(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val genre: String, // Romantic, Sad, Family, Thriller, Motivational
    val synopsisBn: String,
    val synopsisEn: String,
    val previouslyOnBn: String,
    val previouslyOnEn: String,
    val storyOfTheWeek: Boolean = false,
    @DrawableRes val coverRes: Int,
    val characters: List<StoryCharacter>,
    val parts: List<StoryPart>,
)

data class CuratedPlaylist(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val descriptionBn: String,
    val descriptionEn: String,
    val category: String,
    val videoIds: List<String>,
    @DrawableRes val coverRes: Int,
)

data class ToolDirectoryItem(
    val id: String,
    val name: String,
    val category: String, // AI Tools, Design, Editing, Hosting, Apps, Websites
    val descriptionBn: String,
    val descriptionEn: String,
    val url: String,
    val freeTier: Boolean = true,
)

data class PromptTemplate(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val category: String, // Script, Thumbnail, Voiceover, App Building
    val promptText: String,
    val rating: Float = 4.8f,
)

data class CourseLesson(
    val id: String,
    val level: String, // Beginner, Intermediate, Advanced
    val titleBn: String,
    val titleEn: String,
    val summaryBn: String,
    val summaryEn: String,
    val durationLabel: String,
)

data class GlossaryTerm(
    val id: String,
    val term: String,
    val definitionBn: String,
    val definitionEn: String,
)

data class QuizQuestion(
    val id: String,
    val category: String, // AI, Android, YouTube, Internet, General Tech
    val difficulty: String, // Easy, Medium, Hard
    val questionBn: String,
    val questionEn: String,
    val optionsBn: List<String>,
    val optionsEn: List<String>,
    val correctIndex: Int,
    val explanationBn: String,
    val explanationEn: String,
)

data class FlashcardItem(
    val id: String,
    val frontTerm: String,
    val backBn: String,
    val backEn: String,
    val category: String,
)

data class PollItem(
    val id: String,
    val questionBn: String,
    val questionEn: String,
    val options: List<PollOption>,
)

data class PollOption(
    val id: String,
    val labelBn: String,
    val labelEn: String,
    val votes: Int,
)

data class CommunityEvent(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val dateLabel: String,
    val typeLabel: String,
)

data class NotificationItem(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val bodyBn: String,
    val bodyEn: String,
    val timeLabel: String,
    val targetRoute: String,
    val unread: Boolean = true,
)

data class ChatMessage(
    val id: String,
    val role: String, // "user" or "model"
    val text: String,
    val timestampLabel: String,
)

data class VideoAiSummary(
    val tldr: String,
    val keyPoints: List<String>,
    val quizQuestion: String,
    val quizOptions: List<String>,
    val correctIndex: Int,
)

// --- FIRESTORE DATA MODELS (1:1 PARITY WITH firestore.rules) ---

@Keep
@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "Kabir Tech Explorer",
    val bio: String = "",
    val language: String = "bn",
    val themeMode: String = "dark",
    val xp: Long = 0L,
    val streakDays: Long = 1L,
    val videosWatched: Long = 0L,
    val quizzesTaken: Long = 0L,
    val watchTimeMinutes: Long = 0L,
    val favoriteCategories: String = "Tech Tutorials,AI Tools,Stories",
    val badges: String = "FIRST_STEP",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
)

@Keep
@IgnoreExtraProperties
data class UserPrivateInfo(
    @DocumentId val id: String = "",
    val userId: String = "",
    val fullName: String = "",
    val contact: String = "",
    val deviceModel: String = "",
    val interests: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
)

@Keep
@IgnoreExtraProperties
data class VideoNote(
    @DocumentId val id: String = "",
    val userId: String = "",
    val videoId: String = "",
    val videoTitle: String = "",
    val timestampSeconds: Long = 0L,
    val content: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
)

@Keep
@IgnoreExtraProperties
data class UserPlaylist(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val videoIds: List<String> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
)

@Keep
@IgnoreExtraProperties
data class MediaProgress(
    @DocumentId val id: String = "",
    val userId: String = "",
    val itemId: String = "",
    val itemType: String = "VIDEO",
    val watchedSeconds: Long = 0L,
    val totalSeconds: Long = 0L,
    val saved: Boolean = false,
    val liked: Boolean = false,
    val completed: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
)

@Keep
@IgnoreExtraProperties
data class CommunityPost(
    @DocumentId val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val postType: String = "FEED",
    val title: String = "",
    val body: String = "",
    val likesCount: Long = 0L,
    val repliesCount: Long = 0L,
    val approved: Boolean = true,
    val reported: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
)

@Keep
@IgnoreExtraProperties
data class VideoRequest(
    @DocumentId val id: String = "",
    val userId: String = "",
    val authorName: String = "",
    val title: String = "",
    val category: String = "AI Tools",
    val description: String = "",
    val upvotes: Long = 1L,
    val status: String = "OPEN",
    val visibility: String = "PUBLIC",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
)

@Keep
@IgnoreExtraProperties
data class Announcement(
    @DocumentId val id: String = "",
    val authorId: String = "",
    val titleBn: String = "",
    val titleEn: String = "",
    val bodyBn: String = "",
    val bodyEn: String = "",
    val pinned: Boolean = true,
    val visibility: String = "PUBLIC",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
)
