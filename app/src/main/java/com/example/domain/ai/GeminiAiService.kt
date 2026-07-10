package com.example.domain.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiAiService {
    private const val TAG = "GeminiAiService"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite-preview:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    data class GeminiMoveResponse(
        val reasoning: String,
        val move: Int
    )

    suspend fun getBestMove(board: List<String?>): GeminiMoveResponse? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty()) {
            Log.e(TAG, "Gemini API key is empty.")
            return@withContext null
        }

        // Format board to show empty, X, and O
        val boardString = board.mapIndexed { index, value ->
            "Index $index: ${value ?: "empty"}"
        }.joinToString("\n")

        val prompt = """
            You are a Tic-Tac-Toe AI player playing as 'O'. The opponent plays as 'X'.
            The board is a 3x3 grid with indices 0 to 8:
            0 | 1 | 2
            ---------
            3 | 4 | 5
            ---------
            6 | 7 | 8

            Current board state:
            $boardString

            You MUST choose exactly one of the empty indices to place your 'O'. 
            Prioritize:
            1. Winning immediately if there is a line of two 'O's and one empty cell.
            2. Blocking 'X' if they have two in a row and one empty cell.
            3. Taking the center (index 4) if it's empty.
            4. Taking corners (indices 0, 2, 6, 8) if they are empty.
            5. Taking any other available empty cell.

            Your response must be extremely brief. Respond ONLY with a single JSON object in the following format (no markdown code blocks, no backticks, just raw JSON):
            {"reasoning": "brief 1-sentence reasoning", "move": <selected_empty_index>}
        """.trimIndent()

        // Create JSON payload for Gemini API
        val requestJson = JSONObject().apply {
            put("contents", org.json.JSONArray().put(
                JSONObject().put("parts", org.json.JSONArray().put(
                    JSONObject().put("text", prompt)
                ))
            ))
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.1) // Low temperature for tactical consistency
            })
        }

        val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
        val url = "$BASE_URL?key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "Request failed: ${response.code} ${response.message}")
                    return@withContext null
                }

                val responseBodyStr = response.body?.string() ?: return@withContext null
                Log.d(TAG, "Response string: $responseBodyStr")

                val responseJson = JSONObject(responseBodyStr)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    if (content != null) {
                        val parts = content.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val text = parts.getJSONObject(0).optString("text")
                            if (!text.isNullOrEmpty()) {
                                val cleanedText = text.trim()
                                val moveJson = JSONObject(cleanedText)
                                val reasoning = moveJson.optString("reasoning", "Thinking...")
                                val move = moveJson.optInt("move", -1)
                                if (move in 0..8 && board[move] == null) {
                                    return@withContext GeminiMoveResponse(reasoning, move)
                                } else {
                                    Log.e(TAG, "Gemini returned invalid or occupied move: $move")
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error calling Gemini API", e)
        }
        return@withContext null
    }
}
