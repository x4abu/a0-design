package design.a0.app.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * Optional server engine. When the user runs the FastAPI backend (see /backend),
 * the Design Brain is assembled server-side and the request is signed with the
 * user's own key. If it is not configured the app calls the provider directly.
 *
 * No silent fallbacks: an unreachable or misconfigured server is reported as-is.
 */
object DesignApi {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private val mediaType = "application/json; charset=utf-8".toMediaType()

    private val http = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .build()

    suspend fun health(baseUrl: String): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val req = Request.Builder().url(baseUrl.trimEnd('/') + "/health").get().build()
            http.newCall(req).execute().use { it.isSuccessful }
        }.getOrDefault(false)
    }

    suspend fun generate(
        baseUrl: String,
        prompt: String,
        model: String,
        key: String
    ): LlmClient.Result = withContext(Dispatchers.IO) {
        runCatching {
            val body = buildJsonObject {
                put("prompt", prompt)
                put("model", model)
                put("mode", "Auto")
                put("platform", "android")
                put("api_key", key)
            }.toString().toRequestBody(mediaType)

            val req = Request.Builder()
                .url(baseUrl.trimEnd('/') + "/api/generate")
                .header("Authorization", "Bearer $key")
                .post(body)
                .build()

            http.newCall(req).execute().use { res ->
                val text = res.body?.string()
                if (!res.isSuccessful) {
                    val detail = runCatching {
                        json.parseToJsonElement(text ?: "").jsonObject["detail"]?.jsonPrimitive?.content
                    }.getOrNull()
                    LlmClient.Result.Err(res.code, detail ?: "Backend error " + res.code)
                } else {
                    val answer = runCatching {
                        json.parseToJsonElement(text ?: "").jsonObject["answer"]?.jsonPrimitive?.content
                    }.getOrNull()
                    if (answer.isNullOrBlank()) {
                        LlmClient.Result.Err(res.code, "The backend returned no answer.")
                    } else {
                        LlmClient.Result.Ok(answer, LlmClient.Usage(0, 0, 0.0))
                    }
                }
            }
        }.getOrElse { LlmClient.Result.Err(0, it.message ?: "Could not reach the backend") }
    }
}
