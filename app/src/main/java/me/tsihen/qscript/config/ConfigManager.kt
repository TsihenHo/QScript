/* QScript - An Xposed module to run scripts on QQ
 * Copyright (C) 2021-2022 chinese.he.amber@gmail.com
 * https://github.com/GoldenHuaji/QScript
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package me.tsihen.qscript.config

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import me.tsihen.qscript.util.FILE_DEFAULT_CONFIG
import me.tsihen.qscript.util.fromJson
import me.tsihen.qscript.util.getApplicationNonNull
import me.tsihen.qscript.util.log
import java.io.File

class ConfigManager private constructor(private val file: File, private val type: Int) {
    private var config = HashMap<String, Any?>()

    init {
        if (!file.exists()) {
            file.createNewFile()
        }
        val fileContent = file.readText()
        config = try {
            if (fileContent.isNotEmpty()) {
                Gson().fromJson(fileContent)!!
            } else {
                HashMap()
            }
        } catch (e: JsonSyntaxException) {
            log(e, true)
            // 读取失败，清空 file
            file.delete()
            file.createNewFile()
            HashMap()
        }
    }

    companion object {
        private var sDefaultConfig: ConfigManager? = null
        private var sCache: ConfigManager? = null

        fun getDefaultConfig(): ConfigManager {
            try {
                if (sDefaultConfig == null) {
                    sDefaultConfig = ConfigManager(
                        File(getApplicationNonNull().filesDir.absolutePath + "/qscript_config.json"),
                        FILE_DEFAULT_CONFIG
                    )
                }
                return sDefaultConfig!!
            } catch (e: java.lang.Exception) {
                log(e)
                throw e
            }
        }

        fun getCache(): ConfigManager {
            if (sCache == null) {
                sCache = ConfigManager(
                    File(getApplicationNonNull().filesDir.absolutePath + "/qscript_cache.json"),
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