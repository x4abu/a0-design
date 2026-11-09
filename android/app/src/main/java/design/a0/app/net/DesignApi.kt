package design.a0.app.net

import design.a0.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
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
 * Thin client for the a0 backend (FastAPI, see /backend).
 *
 * The app is fully usable offline: every call here is best-effort and the
 * ViewModel falls back to the local design engine when the server is not
 * reachable. That keeps the product honest — no fake "connected" state.
 */
object DesignApi {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val mediaType = "application/json; charset=utf-8".toMediaType()

    var baseUrl: String = BuildConfig.A0_BACKEND_URL
    var apiKey: String = ""

    private fun url(path: String) = baseUrl.trimEnd('/') + path

    suspend fun health(): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val req = Request.Builder().url(url("/health")).get().build()
            client.newCall(req).execute().use { it.isSuccessful }
        }.getOrDefault(false)
    }

    /**
     * Assemble the Design Brain context on the server and ask the model for a
     * design. Returns the assistant text (already anti-slop checked server side).
     */
    suspend fun generate(
        prompt: String,
        model: String = "anthropic/claude-sonnet-4",
        mode: String = "Auto",
        systemPrompt: String = "",
        platform: String = "android"
    ): String? = withContext(Dispatchers.IO) {
        runCatching {
            val body = buildJsonObject {
                put("prompt", prompt)
                put("model", model)
                put("mode", mode)
                put("platform", platform)
                put("system_prompt", systemPrompt)
                put("theme", "pastel-soft-lime")
            }.toString().toRequestBody(mediaType)

            val builder = Request.Builder().url(url("/api/generate")).post(body)
            if (apiKey.isNotBlank()) builder.header("Authorization", "Bearer $apiKey")
            client.newCall(builder.build()).execute().use { res ->
                if (!res.isSuccessful) return@use null
                val text = res.body?.string() ?: return@use null
                val root = json.parseToJsonElement(text).jsonObject
                root["answer"]?.jsonPrimitive?.content
            }
        }.getOrNull()
    }

    /** Deterministic anti-slop audit of the current design system (15 checks). */
    suspend fun audit(payload: JsonObject): JsonObject? = withContext(Dispatchers.IO) {
        runCatching {
            val body = payload.toString().toRequestBody(mediaType)
            val req = Request.Builder().url(url("/api/antislop/check")).post(body).build()
            client.newCall(req).execute().use { res ->
                if (!res.isSuccessful) return@use null
                json.parseToJsonElement(res.body?.string() ?: return@use null).jsonObject
            }
        }.getOrNull()
    }

    suspend fun designBrainSections(): List<String> = withContext(Dispatchers.IO) {
        runCatching {
            val req = Request.Builder().url(url("/api/brain/sections")).get().build()
            client.newCall(req).execute().use { res ->
                if (!res.isSuccessful) return@use emptyList()
                val root = json.parseToJsonElement(res.body?.string() ?: "").jsonObject
                root["sections"]?.jsonPrimitive?.content?.split("|") ?: emptyList()
            }
        }.getOrDefault(emptyList())
    }
}
