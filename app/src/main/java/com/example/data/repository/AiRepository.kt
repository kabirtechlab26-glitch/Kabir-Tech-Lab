package com.example.data.repository

import android.util.Log
import com.example.data.model.VideoAiSummary
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

class AiRepository {
    private val chatModel by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    private val summarySchema = Schema.obj(
        properties = mapOf(
            "tldr" to Schema.string(description = "Concise 2-sentence TL;DR summary"),
            "keyPoints" to Schema.array(
                items = Schema.string(),
                description = "3 to 4 key takeaways"
            ),
            "quizQuestion" to Schema.string(description = "One multiple-choice comprehension question"),
            "quizOptions" to Schema.array(
                items = Schema.string(),
                description = "Exactly 4 answer choices"
            ),
            "correctIndex" to Schema.integer(description = "0-based index of the correct option"),
        )
    )

    private val structuredSummaryModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = summarySchema
            }
        )
    }

    fun streamAssistantReply(
        prompt: String,
        language: String,
        videoContext: String? = null
    ): Flow<String> = flow {
        val langLabel = if (language == "en") "English" else "natural Bangla (বাংলা)"
        val fullPrompt = buildString {
            append("You are the helpful tech & creator assistant for Kabir Tech Lab. ")
            append("Reply clearly and concisely in $langLabel. ")
            if (!videoContext.isNullOrBlank()) {
                append("\nVideo Context:\n$videoContext\n")
            }
            append("\nUser Request:\n$prompt")
        }
        val accumulated = StringBuilder()
        try {
            withTimeout(60_000L) {
                chatModel.generateContentStream(fullPrompt).collect { chunk ->
                    val piece = chunk.text.orEmpty()
                    if (piece.isNotEmpty()) {
                        accumulated.append(piece)
                        emit(accumulated.toString())
                    }
                }
            }
            if (accumulated.isBlank()) {
                throw IllegalStateException("Model returned an empty response.")
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Gemini streaming timed out", e)
            throw IllegalStateException("AI request timed out after 60 seconds. Please try again.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gemini streaming failed", e)
            throw e
        }
    }

    suspend fun generateVideoSummary(
        title: String,
        description: String,
        language: String
    ): Result<VideoAiSummary> {
        val langLabel = if (language == "en") "English" else "natural Bangla (বাংলা)"
        val prompt = """
            Analyze this Kabir Tech Lab video and return JSON in $langLabel:
            Title: $title
            Description: $description
        """.trimIndent()
        return try {
            val response = withTimeout(60_000L) {
                structuredSummaryModel.generateContent(prompt)
            }
            val raw = response.text ?: throw IllegalStateException("Empty AI summary response.")
            val json = JSONObject(raw)
            val pointsArr = json.getJSONArray("keyPoints")
            val points = (0 until pointsArr.length()).map { pointsArr.getString(it) }
            val optsArr = json.getJSONArray("quizOptions")
            val opts = (0 until optsArr.length()).map { optsArr.getString(it) }
            Result.success(
                VideoAiSummary(
                    tldr = json.getString("tldr"),
                    keyPoints = points,
                    quizQuestion = json.getString("quizQuestion"),
                    quizOptions = opts,
                    correctIndex = json.optInt("correctIndex", 0).coerceIn(0, (opts.size - 1).coerceAtLeast(0)),
                )
            )
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Summary timed out", e)
            Result.failure(IllegalStateException("AI summary timed out after 60s. Please retry."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate structured video summary", e)
            Result.failure(e)
        }
    }

    suspend fun generateCreatorOutput(prompt: String, language: String): Result<String> {
        val langLabel = if (language == "en") "English" else "Bangla (বাংলা)"
        return try {
            val response = withTimeout(60_000L) {
                chatModel.generateContent("Respond in $langLabel for a YouTube creator:\n$prompt")
            }
            val text = response.text
            if (!text.isNullOrBlank()) Result.success(text)
            else Result.failure(IllegalStateException("Empty AI response."))
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Creator AI timed out", e)
            Result.failure(IllegalStateException("Timed out after 60s. Please try again."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Creator AI failed", e)
            Result.failure(e)
        }
    }

    private companion object {
        const val TAG = "AiRepository"
    }
}
