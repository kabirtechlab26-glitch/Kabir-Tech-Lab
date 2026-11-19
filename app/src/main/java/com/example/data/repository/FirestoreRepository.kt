package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.Announcement
import com.example.data.model.CommunityPost
import com.example.data.model.MediaProgress
import com.example.data.model.UserPrivateInfo
import com.example.data.model.UserPlaylist
import com.example.data.model.UserProfile
import com.example.data.model.VideoNote
import com.example.data.model.VideoRequest
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    // --- USER PROFILE ---
    fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        val docRef = db.collection("users").document(userId)
        emitAll(
            docRef.snapshots()
                .map { snap -> snap.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    if (auth.currentUser?.uid == userId) {
                        handleFirestoreError(fsErr ?: error, OperationType.GET, docRef.path)
                    }
                    throw error
                }
        )
    }

    suspend fun ensureUserProfile(
        displayName: String,
        language: String,
        themeMode: String,
        initialXp: Long = 120L,
        initialStreak: Long = 1L,
    ): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Not signed in"))
        val path = "users/$uid"
        return try {
            val docRef = db.collection("users").document(uid)
            val existing = docRef.get().await()
            if (!existing.exists()) {
                val safeName = displayName.trim().ifBlank { "Kabir Tech Explorer" }.take(80)
                val payload = mapOf(
                    "userId" to uid,
                    "displayName" to safeName,
                    "language" to if (language == "en") "en" else "bn",
                    "themeMode" to if (themeMode in listOf("dark", "light", "system", "amoled")) themeMode else "dark",
                    "xp" to initialXp.coerceIn(0L, 1_000_000L),
                    "streakDays" to initialStreak.coerceIn(0L, 10_000L),
                    "videosWatched" to 0L,
                    "quizzesTaken" to 0L,
                    "watchTimeMinutes" to 0L,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp(),
                )
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun updateUserProfileStats(
        displayName: String? = null,
        bio: String? = null,
        language: String? = null,
        themeMode: String? = null,
        xpDelta: Long = 0L,
        streakDays: Long? = null,
        quizzesTakenDelta: Long = 0L,
        videosWatchedDelta: Long = 0L,
    ): Result<Unit> {
        var path = "users/unknown"
        return try {
            val uid = requireUserId()
            path = "users/$uid"
            val updates = mutableMapOf<String, Any>("updatedAt" to FieldValue.serverTimestamp())
            if (displayName != null) updates["displayName"] = displayName.trim().take(80).ifBlank { "Member" }
            if (bio != null) updates["bio"] = bio.trim().take(300)
            if (language != null) updates["language"] = if (language == "en") "en" else "bn"
            if (themeMode != null && themeMode in listOf("dark", "light", "system", "amoled")) {
                updates["themeMode"] = themeMode
            }
            if (xpDelta != 0L) updates["xp"] = FieldValue.increment(xpDelta)
            if (streakDays != null) updates["streakDays"] = streakDays.coerceIn(0L, 10_000L)
            if (quizzesTakenDelta != 0L) updates["quizzesTaken"] = FieldValue.increment(quizzesTakenDelta)
            if (videosWatchedDelta != 0L) updates["videosWatched"] = FieldValue.increment(videosWatchedDelta)
            db.collection("users").document(uid).update(updates).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    // --- PRIVATE JOIN FORM (PII ISOLATED) ---
    suspend fun submitCommunityJoinForm(
        fullName: String,
        contact: String,
        deviceModel: String,
        interests: String,
    ): Result<Unit> {
        var path = "users/unknown/private/join_info"
        return try {
            val uid = requireUserId()
            require(fullName.trim().isNotEmpty() && fullName.trim().length <= 100) { "Invalid name" }
            require(contact.trim().length in 3..120) { "Invalid contact" }
            path = "users/$uid/private/join_info"
            val docRef = db.collection("users").document(uid).collection("private").document("join_info")
            val existing = docRef.get().await()
            if (existing.exists()) {
                docRef.update(
                    mapOf(
                        "fullName" to fullName.trim().take(100),
                        "contact" to contact.trim().take(120),
                        "deviceModel" to deviceModel.trim().take(100),
                        "interests" to interests.trim().take(300),
                        "updatedAt" to FieldValue.serverTimestamp(),
                    )
                ).await()
            } else {
                val payload = mapOf(
                    "userId" to uid,
                    "fullName" to fullName.trim().take(100),
                    "contact" to contact.trim().take(120),
                    "deviceModel" to deviceModel.trim().take(100),
                    "interests" to interests.trim().take(300),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp(),
                )
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    // --- NOTES ---
    fun observeNotes(userId: String): Flow<List<VideoNote>> = flow {
        val path = "users/$userId/notes"
        emitAll(
            db.collection("users").document(userId).collection("notes")
                .snapshots()
                .map { it.toObjects(VideoNote::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    if (auth.currentUser?.uid == userId) {
                        handleFirestoreError(fsErr ?: error, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun addNote(videoId: String, videoTitle: String, timestampSeconds: Long, content: String): Result<Unit> {
        var path = "users/unknown/notes"
        return try {
            val uid = requireUserId()
            val clean = content.trim()
            require(clean.isNotEmpty() && clean.length <= 2000) { "Note must be 1..2000 characters" }
            val docRef = db.collection("users").document(uid).collection("notes").document()
            path = docRef.path
            docRef.set(
                mapOf(
                    "userId" to uid,
                    "videoId" to videoId.take(128),
                    "videoTitle" to videoTitle.take(200),
                    "timestampSeconds" to timestampSeconds.coerceIn(0L, 86400L),
                    "content" to clean,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp(),
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteNote(noteId: String): Result<Unit> {
        var path = "users/unknown/notes/$noteId"
        return try {
            val uid = requireUserId()
            path = "users/$uid/notes/$noteId"
            db.collection("users").document(uid).collection("notes").document(noteId).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    // --- CUSTOM PLAYLISTS ---
    fun observePlaylists(userId: String): Flow<List<UserPlaylist>> = flow {
        val path = "users/$userId/playlists"
        emitAll(
            db.collection("users").document(userId).collection("playlists")
                .snapshots()
                .map { it.toObjects(UserPlaylist::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    if (auth.currentUser?.uid == userId) {
                        handleFirestoreError(fsErr ?: error, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun createPlaylist(name: String, initialVideoId: String? = null): Result<Unit> {
        var path = "users/unknown/playlists"
        return try {
            val uid = requireUserId()
            val cleanName = name.trim()
            require(cleanName.isNotEmpty() && cleanName.length <= 80) { "Playlist name must be 1..80 characters" }
            val docRef = db.collection("users").document(uid).collection("playlists").document()
            path = docRef.path
            val ids = if (!initialVideoId.isNullOrBlank()) listOf(initialVideoId) else emptyList()
            docRef.set(
                mapOf(
                    "userId" to uid,
                    "name" to cleanName,
                    "videoIds" to ids,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp(),
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    // --- MEDIA PROGRESS & BOOKMARKS ---
    fun observeProgress(userId: String): Flow<List<MediaProgress>> = flow {
        val path = "users/$userId/progress"
        emitAll(
            db.collection("users").document(userId).collection("progress")
                .snapshots()
                .map { it.toObjects(MediaProgress::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    if (auth.currentUser?.uid == userId) {
                        handleFirestoreError(fsErr ?: error, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun upsertProgress(
        itemId: String,
        itemType: String,
        watchedSeconds: Long,
        totalSeconds: Long,
        saved: Boolean,
        liked: Boolean,
        completed: Boolean,
    ): Result<Unit> {
        var path = "users/unknown/progress/$itemId"
        return try {
            val uid = requireUserId()
            val safeId = itemId.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(128)
            path = "users/$uid/progress/$safeId"
            val docRef = db.collection("users").document(uid).collection("progress").document(safeId)
            val snap = docRef.get().await()
            if (snap.exists()) {
                docRef.update(
                    mapOf(
                        "watchedSeconds" to watchedSeconds.coerceIn(0L, 86400L),
                        "totalSeconds" to totalSeconds.coerceIn(0L, 86400L),
                        "saved" to saved,
                        "liked" to liked,
                        "completed" to completed,
                        "updatedAt" to FieldValue.serverTimestamp(),
                    )
                ).await()
            } else {
                docRef.set(
                    mapOf(
                        "userId" to uid,
                        "itemId" to safeId,
                        "itemType" to if (itemType == "STORY_PART") "STORY_PART" else "VIDEO",
                        "watchedSeconds" to watchedSeconds.coerceIn(0L, 86400L),
                        "totalSeconds" to totalSeconds.coerceIn(0L, 86400L),
                        "saved" to saved,
                        "liked" to liked,
                        "completed" to completed,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp(),
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    // --- COMMUNITY POSTS ---
    fun observeApprovedCommunityPosts(): Flow<List<CommunityPost>> = flow {
        requireUserId()
        val path = "community_posts"
        emitAll(
            db.collection(path)
                .whereEqualTo("approved", true)
                .snapshots()
                .map { it.toObjects(CommunityPost::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    if (auth.currentUser != null) {
                        handleFirestoreError(fsErr ?: error, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun createCommunityPost(
        authorName: String,
        postType: String,
        title: String,
        body: String
    ): Result<Unit> {
        var path = "community_posts"
        return try {
            val uid = requireUserId()
            val cleanTitle = title.trim()
            val cleanBody = body.trim()
            require(cleanTitle.length in 2..150) { "Title must be 2..150 characters" }
            require(cleanBody.length in 2..2000) { "Body must be 2..2000 characters" }
            val docRef = db.collection("community_posts").document()
            path = docRef.path
            docRef.set(
                mapOf(
                    "authorId" to uid,
                    "authorName" to authorName.trim().ifBlank { "Member" }.take(80),
                    "postType" to if (postType in listOf("FEED", "QA", "SHOWCASE")) postType else "FEED",
                    "title" to cleanTitle,
                    "body" to cleanBody,
                    "likesCount" to 0L,
                    "repliesCount" to 0L,
                    "approved" to true,
                    "reported" to false,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp(),
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun likeCommunityPost(postId: String): Result<Unit> {
        val path = "community_posts/$postId"
        return try {
            requireUserId()
            db.collection("community_posts").document(postId)
                .update(
                    mapOf(
                        "likesCount" to FieldValue.increment(1L),
                        "updatedAt" to FieldValue.serverTimestamp(),
                    )
                ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    // --- VIDEO REQUESTS ---
    fun observePublicVideoRequests(): Flow<List<VideoRequest>> = flow {
        requireUserId()
        val path = "video_requests"
        emitAll(
            db.collection(path)
                .whereEqualTo("visibility", "PUBLIC")
                .snapshots()
                .map { it.toObjects(VideoRequest::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    if (auth.currentUser != null) {
                        handleFirestoreError(fsErr ?: error, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun createVideoRequest(
        authorName: String,
        title: String,
        category: String,
        description: String
    ): Result<Unit> {
        var path = "video_requests"
        return try {
            val uid = requireUserId()
            val cleanTitle = title.trim()
            val cleanDesc = description.trim()
            require(cleanTitle.length in 3..150) { "Title must be 3..150 characters" }
            require(cleanDesc.length in 3..1000) { "Description must be 3..1000 characters" }
            val docRef = db.collection("video_requests").document()
            path = docRef.path
            docRef.set(
                mapOf(
                    "userId" to uid,
                    "authorName" to authorName.trim().ifBlank { "Member" }.take(80),
                    "title" to cleanTitle,
                    "category" to category.take(60).ifBlank { "Tech Tutorials" },
                    "description" to cleanDesc,
                    "upvotes" to 1L,
                    "status" to "OPEN",
                    "visibility" to "PUBLIC",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp(),
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun upvoteVideoRequest(requestId: String): Result<Unit> {
        val path = "video_requests/$requestId"
        return try {
            requireUserId()
            db.collection("video_requests").document(requestId)
                .update(
                    mapOf(
                        "upvotes" to FieldValue.increment(1L),
                        "updatedAt" to FieldValue.serverTimestamp(),
                    )
                ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    // --- ANNOUNCEMENTS ---
    fun observePublicAnnouncements(): Flow<List<Announcement>> = flow {
        requireUserId()
        val path = "announcements"
        emitAll(
            db.collection(path)
                .whereEqualTo("visibility", "PUBLIC")
                .snapshots()
                .map { it.toObjects(Announcement::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    if (auth.currentUser != null) {
                        handleFirestoreError(fsErr ?: error, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun publishAnnouncement(
        titleBn: String,
        titleEn: String,
        bodyBn: String,
        bodyEn: String,
        pinned: Boolean = true
    ): Result<Unit> {
        var path = "announcements"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("announcements").document()
            path = docRef.path
            docRef.set(
                mapOf(
                    "authorId" to uid,
                    "titleBn" to titleBn.trim().take(150).ifBlank { "নতুন ঘোষণা" },
                    "titleEn" to titleEn.trim().take(150).ifBlank { "Announcement" },
                    "bodyBn" to bodyBn.trim().take(1000).ifBlank { bodyEn.trim() },
                    "bodyEn" to bodyEn.trim().take(1000).ifBlank { bodyBn.trim() },
                    "pinned" to pinned,
                    "visibility" to "PUBLIC",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp(),
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }
}
