package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Announcement
import com.example.data.model.CommunityPost
import com.example.data.model.MediaProgress
import com.example.data.model.UserPlaylist
import com.example.data.model.UserProfile
import com.example.data.model.VideoNote
import com.example.data.model.VideoRequest
import com.example.data.repository.FirestoreRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class CloudSyncViewModel(
    private val repository: FirestoreRepository,
    val currentUserId: String,
    initialDisplayName: String,
    initialLanguage: String,
    initialThemeMode: String,
) : ViewModel() {

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    val userProfile: StateFlow<UiState<UserProfile?>> =
        repository.observeUserProfile(currentUserId)
            .toUiState("Failed to load profile")

    val notes: StateFlow<UiState<List<VideoNote>>> =
        repository.observeNotes(currentUserId)
            .toUiState("Failed to load notes")

    val playlists: StateFlow<UiState<List<UserPlaylist>>> =
        repository.observePlaylists(currentUserId)
            .toUiState("Failed to load playlists")

    val progressList: StateFlow<UiState<List<MediaProgress>>> =
        repository.observeProgress(currentUserId)
            .toUiState("Failed to load watch progress")

    val communityPosts: StateFlow<UiState<List<CommunityPost>>> =
        repository.observeApprovedCommunityPosts()
            .toUiState("Failed to load community feed")

    val videoRequests: StateFlow<UiState<List<VideoRequest>>> =
        repository.observePublicVideoRequests()
            .toUiState("Failed to load video requests")

    val announcements: StateFlow<UiState<List<Announcement>>> =
        repository.observePublicAnnouncements()
            .toUiState("Failed to load announcements")

    init {
        viewModelScope.launch {
            repository.ensureUserProfile(
                displayName = initialDisplayName,
                language = initialLanguage,
                themeMode = initialThemeMode,
            )
        }
    }

    private fun <T> Flow<T>.toUiState(fallbackError: String): StateFlow<UiState<T>> {
        return map<T, UiState<T>> { UiState.Success(it) }
            .catch { error ->
                val fsError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (fsError == null && error is CancellationException) throw error
                val root = fsError ?: error
                Log.w(TAG, fallbackError, root)
                emit(UiState.Error(root.message ?: fallbackError))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = UiState.Loading
            )
    }

    fun clearMessage() { _actionMessage.value = null }

    fun addNote(videoId: String, videoTitle: String, timestampSeconds: Long, content: String) {
        viewModelScope.launch {
            val res = repository.addNote(videoId, videoTitle, timestampSeconds, content)
            _actionMessage.value = res.fold(
                onSuccess = { "Note saved to cloud" },
                onFailure = { it.localizedMessage ?: "Failed to save note" }
            )
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            repository.deleteNote(noteId)
        }
    }

    fun createPlaylist(name: String, initialVideoId: String? = null) {
        viewModelScope.launch {
            val res = repository.createPlaylist(name, initialVideoId)
            _actionMessage.value = res.fold(
                onSuccess = { "Playlist created" },
                onFailure = { it.localizedMessage ?: "Failed to create playlist" }
            )
        }
    }

    fun syncMediaProgress(
        itemId: String,
        itemType: String,
        watchedSeconds: Long,
        totalSeconds: Long,
        saved: Boolean,
        liked: Boolean,
        completed: Boolean,
    ) {
        viewModelScope.launch {
            repository.upsertProgress(itemId, itemType, watchedSeconds, totalSeconds, saved, liked, completed)
        }
    }

    fun createPost(authorName: String, postType: String, title: String, body: String) {
        viewModelScope.launch {
            val res = repository.createCommunityPost(authorName, postType, title, body)
            _actionMessage.value = res.fold(
                onSuccess = { "Post published!" },
                onFailure = { it.localizedMessage ?: "Could not publish post" }
            )
        }
    }

    fun likePost(postId: String) {
        viewModelScope.launch { repository.likeCommunityPost(postId) }
    }

    fun createVideoRequest(authorName: String, title: String, category: String, description: String) {
        viewModelScope.launch {
            val res = repository.createVideoRequest(authorName, title, category, description)
            _actionMessage.value = res.fold(
                onSuccess = { "Video request submitted!" },
                onFailure = { it.localizedMessage ?: "Failed to submit request" }
            )
        }
    }

    fun upvoteRequest(requestId: String) {
        viewModelScope.launch { repository.upvoteVideoRequest(requestId) }
    }

    fun submitJoinForm(fullName: String, contact: String, deviceModel: String, interests: String) {
        viewModelScope.launch {
            val res = repository.submitCommunityJoinForm(fullName, contact, deviceModel, interests)
            _actionMessage.value = res.fold(
                onSuccess = { "Welcome to Kabir Tech Lab Community!" },
                onFailure = { it.localizedMessage ?: "Failed to submit form" }
            )
        }
    }

    fun publishAnnouncement(titleBn: String, titleEn: String, bodyBn: String, bodyEn: String) {
        viewModelScope.launch {
            val res = repository.publishAnnouncement(titleBn, titleEn, bodyBn, bodyEn)
            _actionMessage.value = res.fold(
                onSuccess = { "Announcement pinned!" },
                onFailure = { it.localizedMessage ?: "Admin permission required" }
            )
        }
    }

    fun syncXpAndQuiz(xpDelta: Long, quizzesDelta: Long = 0L) {
        viewModelScope.launch {
            repository.updateUserProfileStats(xpDelta = xpDelta, quizzesTakenDelta = quizzesDelta)
        }
    }

    private companion object {
        const val TAG = "CloudSyncVM"
    }
}
