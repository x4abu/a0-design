package design.a0.app.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * Direct model access with the user's own key. Nothing is proxied through us, and
 * every failure is reported with the provider's own words — no invented success.
 */
object LlmClient {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private val mediaType = "application/json; charset=utf-8".toMediaType()

    private val http = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    data class Model(
        val id: String,
        val label: String,
        val context: String,
        val promptPrice: Double,
        val completionPrice: Double
    )

    data class Usage(val promptTokens: Int, val completionTokens: Int, val costUsd: Double)

    sealed interface Result {
        data class Ok(val text: String, val usage: Usage) : Result
        data class Err(val status: Int, val message: String) : Result
    }

    private fun errText(body: String?, code: Int): String {
        if (body.isNullOrBlank()) return "HTTP $code"
        val parsed = runCatching {
            val root = json.parseToJsonElement(body).jsonObject
            (root["error"] as? JsonObject)?.get("message")?.jsonPrimitive?.content
                ?: root["error"]?.jsonPrimitive?.content
                ?: root["message"]?.jsonPrimitive?.content
        }.getOrNull()
        return parsed ?: "HTTP $code: " + body.take(200)
    }

    // ---------------------------------------------------------------- models
    /** OpenRouter's catalogue is public, so the picker works before a key is added. */
    suspend fun openRouterModels(): List<Model> = withContext(Dispatchers.IO) {
        runCatching {
            val req = Request.Builder().url("https://openrouter.ai/api/v1/models").get().build()
            http.newCall(req).execute().use { res ->
                if (!res.isSuccessful) return@use emptyList()
                val root = json.parseToJsonElement(res.body?.string() ?: "").jsonObject
                (root["data"] as? JsonArray)?.mapNotNull { element ->
                    val obj = element.jsonObject
                    val id = obj["id"]?.jsonPrimitive?.content ?: return@mapNotNull null
                    val pricing = obj["pricing"] as? JsonObject
                    Model(
                        id = id,
                        label = obj["name"]?.jsonPrimitive?.content ?: id,
                        context = obj["context_length"]?.jsonPrimitive?.content?.let { "${it.toLongOrNull()?.div(1000)}k" } ?: "",
                        promptPrice = pricing?.get("prompt")?.jsonPrimitive?.doubleOrNull ?: 0.0,
                        completionPrice = pricing?.get("completion")?.jsonPrimitive?.doubleOrNull ?: 0.0
                    )
                } ?: emptyList()
            }
        }.getOrDefault(emptyList())
    }

    suspend fun openAiModels(key: String): Result = withContext(Dispatchers.IO) {
        runCatching {
            val req = Request.Builder()
                .url("https://api.openai.com/v1/models")
                .header("Authorization", "Bearer $key")
                .get().build()
            http.newCall(req).execute().use { res ->
                val body = res.body?.string()
                if (!res.isSuccessful) Result.Err(res.code, errText(body, res.code))
                else {
                    val root = json.parseToJsonElement(body ?: "").jsonObject
                    val ids = (root["data"] as? JsonArray)?.mapNotNull {
                        it.jsonObject["id"]?.jsonPrimitive?.content
                    } ?: emptyList()
                    Result.Ok(ids.joinToString("|"), Usage(0, 0, 0.0))
                }
            }
        }.getOrElse { Result.Err(0, it.message ?: "Network error") }
    }

    // ------------------------------------------------------------ validation
    /** A real key check: one authenticated request, the provider's own answer. */
    suspend fun checkKey(provider: String, key: String): Result = withContext(Dispatchers.IO) {
        when (provider) {
            "OpenRouter" -> {
                val req = Request.Builder()
                    .url("https://openrouter.ai/api/v1/key")
                    .header("Authorization", "Bearer $key")
                    .get().build()
                runCatching {
                    http.newCall(req).execute().use { res ->
                        val body = res.body?.string()
                        if (res.isSuccessful) Result.Ok("ok", Usage(0, 0, 0.0))
                        else Result.Err(res.code, errText(body, res.code))
                    }
                }.getOrElse { Result.Err(0, it.message ?: "Network error") }
            }
            "OpenAI" -> openAiModels(key).let {
                when (it) {
                    is Result.Ok -> Result.Ok("ok", Usage(0, 0, 0.0))
                    is Result.Err -> it
                }
            }
            else -> {
                // Anthropic has no cheap list endpoint; a 1-token message is the honest check.
                complete(
                    provider = provider,
                    key = key,
                    model = "claude-sonnet-4-20250514",
                    system = "Reply with OK.",
                    prompt = "OK?",
                    maxTokens = 4
                )
            }
        }
    }

    // -------------------------------------------------------------- complete
    suspend fun complete(
        provider: String,
        key: String,
        model: String,
        system: String,
        prompt: String,
        maxTokens: Int = 2600
    ): Result = withContext(Dispatchers.IO) {
        when (provider) {
            "Anthropic" -> anthropic(key, model, system, prompt, maxTokens)
            "OpenAI" -> openAi(key, model, system, prompt, maxTokens)
            else -> openRouter(key, model, system, prompt, maxTokens)
        }
    }

    private fun post(url: String, headers: Map<String, String>, body: String): Result =
        runCatching {
            val builder = Request.Builder().url(url).post(body.toRequestBody(mediaType))
            headers.forEach { (k, v) -> builder.header(k, v) }
            http.newCall(builder.build()).execute().use { res ->
                val text = res.body?.string()
                if (!res.isSuccessful) Result.Err(res.code, errText(text, res.code))
                else parseChat(text ?: "", "openai")
            }
        }.getOrElse { Result.Err(0, it.message ?: "Network error") }

    private fun openRouter(key: String, model: String, system: String, prompt: String, maxTokens: Int): Result {
        val body = buildJsonObject {
            put("model", model)
            put("max_tokens", maxTokens)
            put("messages", buildJsonArray {
                add(buildJsonObject { put("role", "system"); put("content", system) })
                add(buildJsonObject { put("role", "user"); put("content", prompt) })
            })
            put("usage", buildJsonObject { put("include", true) })
        }.toString()
        return post(
            "https://openrouter.ai/api/v1/chat/completions",
            mapOf(
                "Authorization" to "Bearer $key",
                "HTTP-Referer" to "https://github.com/x4abu/a0-design",
                "X-Title" to "a0 design"
            ),
            body
        )
    }

    private fun openAi(key: String, model: String, system: String, prompt: String, maxTokens: Int): Result {
        val body = buildJsonObject {
            put("model", model)
            put("max_completion_tokens", maxTokens)
            put("messages", buildJsonArray {
                add(buildJsonObject { put("role", "system"); put("content", system) })
                add(buildJsonObject { put("role", "user"); put("content", prompt) })
            })
        }.toString()
        return post(
            "https://api.openai.com/v1/chat/completions",
            mapOf("Authorization" to "Bearer $key"),
            body
        )
    }

    private fun anthropic(key: String, model: String, system: String, prompt: String, maxTokens: Int): Result {
        val body = buildJsonObject {
            put("model", model)
            put("max_tokens", maxTokens)
            put("system", system)
            put("messages", buildJsonArray {
                add(buildJsonObject { put("role", "user"); put("content", prompt) })
            })
        }.toString()
        return runCatching {
            val req = Request.Builder()
                .url("https://api.anthropic.com/v1/messages")
                .header("x-api-key", key)
                .header("anthropic-version", "2023-06-01")
                .post(body.toRequestBody(mediaType))
                .build()
            http.newCall(req).execute().use { res ->
                val text = res.body?.string()
                if (!res.isSuccessful) Result.Err(res.code, errText(text, res.code))
                else parseChat(text ?: "", "anthropic")
            }
        }.getOrElse { Result.Err(0, it.message ?: "Network error") }
    }

    private fun parseChat(raw: String, flavour: String): Result {
        val root = json.parseToJsonElement(raw).jsonObject
        val content = when (flavour) {
            "anthropic" -> (root["content"] as? JsonArray)
                ?.joinToString("") { it.jsonObject["text"]?.jsonPrimitive?.content ?: "" }
                ?: ""
            else -> root["choices"]?.jsonArray?.firstOrNull()
                ?.jsonObject?.get("message")?.jsonObject?.get("content")?.jsonPrimitive?.content ?: ""
        }
        val usageObj = root["usage"] as? JsonObject
        val promptTokens = usageObj?.get("prompt_tokens")?.jsonPrimitive?.content?.toIntOrNull()
            ?: usageObj?.get("input_tokens")?.jsonPrimitive?.content?.toIntOrNull() ?: 0
        val completionTokens = usageObj?.get("completion_tokens")?.jsonPrimitive?.content?.toIntOrNull()
            ?: usageObj?.get("output_tokens")?.jsonPrimitive?.content?.toIntOrNull() ?: 0
        return Result.Ok(content, Usage(promptTokens, completionTokens, 0.0))
    }
}
