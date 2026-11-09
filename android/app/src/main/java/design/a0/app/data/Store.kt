package design.a0.app.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import kotlinx.serialization.Serializable
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * API keys never sit in plain SharedPreferences. Each value is AES/GCM-encrypted
 * with a key that lives in the Android Keystore and cannot be exported from the
 * device. Losing the device (or clearing app data) loses the keys — which is the
 * honest trade for not shipping them anywhere.
 */
class SecureStore(context: Context) {

    private val prefs = context.getSharedPreferences("a0_keys", Context.MODE_PRIVATE)

    private fun secretKey(): SecretKey {
        val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (ks.getEntry(ALIAS, null) as? KeyStore.SecretKeyEntry)?.let { return it.secretKey }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(
            KeyGenParameterSpec.Builder(
                ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
        )
        return generator.generateKey()
    }

    fun put(provider: String, value: String) {
        if (value.isBlank()) { remove(provider); return }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey())
        val payload = cipher.iv + cipher.doFinal(value.toByteArray(Charsets.UTF_8))
        prefs.edit().putString(provider, Base64.encodeToString(payload, Base64.NO_WRAP)).apply()
    }

    fun get(provider: String): String? {
        val encoded = prefs.getString(provider, null) ?: return null
        return runCatching {
            val payload = Base64.decode(encoded, Base64.NO_WRAP)
            val iv = payload.copyOfRange(0, 12)
            val body = payload.copyOfRange(12, payload.size)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey(), GCMParameterSpec(128, iv))
            String(cipher.doFinal(body), Charsets.UTF_8)
        }.getOrNull()
    }

    fun remove(provider: String) = prefs.edit().remove(provider).apply()

    fun providers(): Set<String> = prefs.all.keys

    fun clear() = prefs.edit().clear().apply()

    private companion object {
        const val ALIAS = "a0_api_keys_v1"
    }
}

@Serializable
data class Settings(
    val provider: String = "OpenRouter",
    val model: String = "anthropic/claude-sonnet-4",
    val engine: String = "direct",          // direct | backend
    val backendUrl: String = "",
    val systemPrompt: String = "",
    val fontScaleIndex: Int = 1,
    val haptics: Boolean = true
)

/** Everything the app owns, on device. No seeded content, ever. */
class AppStore(context: Context) {

    private val prefs = context.getSharedPreferences("a0_store", Context.MODE_PRIVATE)
    val keys = SecureStore(context)

    var settings: Settings
        get() = prefs.getString(KEY_SETTINGS, null)
            ?.let { runCatching { SpecJson.json.decodeFromString(Settings.serializer(), it) }.getOrNull() }
            ?: Settings()
        set(value) {
            prefs.edit().putString(KEY_SETTINGS, SpecJson.json.encodeToString(Settings.serializer(), value)).apply()
        }

    fun projects(): List<Project> {
        val raw = prefs.getString(KEY_PROJECTS, null) ?: return emptyList()
        return runCatching {
            SpecJson.json.decodeFromString(kotlinx.serialization.builtins.ListSerializer(Project.serializer()), raw)
        }.getOrDefault(emptyList()).sortedByDescending { it.createdAt }
    }

    private fun writeProjects(list: List<Project>) {
        prefs.edit().putString(
            KEY_PROJECTS,
            SpecJson.json.encodeToString(kotlinx.serialization.builtins.ListSerializer(Project.serializer()), list)
        ).apply()
    }

    fun project(id: String): Project? = projects().firstOrNull { it.id == id }

    fun addVersion(projectId: String?, spec: Spec, label: String): Project {
        val all = projects().toMutableList()
        val now = System.currentTimeMillis()
        val existingIndex = all.indexOfFirst { it.id == projectId }
        val version = Version(id = "v${now}", label = label, createdAt = now, spec = spec)
        return if (existingIndex >= 0) {
            val existing = all[existingIndex]
            val updated = existing.copy(versions = existing.versions + version)
            all[existingIndex] = updated
            writeProjects(all)
            updated
        } else {
            val created = Project(
                id = "p$now",
                name = spec.name.ifBlank { "Untitled design" },
                createdAt = now,
                versions = listOf(version)
            )
            all.add(created)
            writeProjects(all)
            created
        }
    }

    fun renameProject(id: String, name: String) {
        val all = projects().map { if (it.id == id) it.copy(name = name) else it }
        writeProjects(all)
    }

    fun deleteProject(id: String) {
        writeProjects(projects().filterNot { it.id == id })
    }

    fun usage(): List<UsageEntry> {
        val raw = prefs.getString(KEY_USAGE, null) ?: return emptyList()
        return runCatching {
            SpecJson.json.decodeFromString(
                kotlinx.serialization.builtins.ListSerializer(UsageEntry.serializer()), raw
            )
        }.getOrDefault(emptyList())
    }

    fun addUsage(entry: UsageEntry) {
        val list = usage() + entry
        prefs.edit().putString(
            KEY_USAGE,
            SpecJson.json.encodeToString(
                kotlinx.serialization.builtins.ListSerializer(UsageEntry.serializer()), list
            )
        ).apply()
    }

    fun clearEverything() {
        prefs.edit().clear().apply()
        keys.clear()
    }

    private companion object {
        const val KEY_SETTINGS = "settings"
        const val KEY_PROJECTS = "projects"
        const val KEY_USAGE = "usage"
    }
}
