package tsihen.me.qscript.config

import com.google.gson.Gson
import tsihen.me.qscript.util.*
import java.io.File

class ConfigManager private constructor(private val file: File, private val type: Int) {
    private var config = HashMap<String, Any?>()

    init {
        if (!file.exists()) {
            file.createNewFile()
        }
        val fileContent = file.readText()
        config = if (fileContent.isNotEmpty()) {
            Gson().fromJson(fileContent)!!
        } else {
            HashMap()
        }
    }

    companion object {
        private var sDefaultConfig: ConfigManager? = null
        private var sCache: ConfigManager? = null

        fun getDefaultConfig(): ConfigManager {
            if (sDefaultConfig == null) {
                sDefaultConfig = ConfigManager(
                    File(getApplicationNonNull().filesDir.absolutePath + "/qscript_config.json"),
                    FILE_DEFAULT_CONFIG
                )
            }
            return sDefaultConfig!!
        }

        fun getCache(): ConfigManager {
            if (sCache == null) {
                sCache = ConfigManager(
                    File(getApplicationNonNull().filesDir.absolutePath + "/qscript_config.json"),
                    FILE_DEFAULT_CONFIG
                )
            }
            return sCache!!
        }

        fun tryGetDefaultConfig(): ConfigManager? {
            return try {
                getDefaultConfig()
            } catch (e: Exception) {
                null
            }
        }
    }

    operator fun get(key: String): Any? = config[key]
    operator fun set(key: String, value: Any?) {
        config[key] = value
        save()
    }

    inline fun <reified T> getOrDefault(key: String, default: T): T {
        val value = this[key]
        if (value == null || value !is T) {
            return default
        }
        return value
    }

    fun remove(key: String) {
        config.remove(key)
        save()
    }

    fun removeAll() {
        config = HashMap()
        save()
    }

    fun getFileContent() = file.readText()

    private fun save() {
        val writer = file.writer()
        writer.write(Gson().toJson(config))
        writer.flush()
        writer.close()
    }
}