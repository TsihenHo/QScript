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
@file:JvmName("QScriptInfo")

package me.tsihen.qscript.script

class QScriptInfo(
    val name: String,
    val label: String,
    val author: String,
    val version: String,
    val desc: String,
    val permissionNetwork: Boolean
) {
    companion object {
        fun getInfo(code: String): QScriptInfo? {
            val execute = code.replace(" ", "")
            if (!execute.startsWith("//QScript.MetaData.Start") || !execute.contains("//QScript.MetaData.End")) {
                // 不符合脚本特征
                return null
            }
            val info = execute.substring(0, execute.indexOf("//QScript.MetaData.End")).replace(
                "//QScript.MetaData.Start",
                ""
            )
            val executeLines = info.split('\n')
            val builder = Builder()
            executeLines.forEach {
                val e = it.replace("//QScript.MetaData.", "").split("=")
                if (e.size > 1) {
                    when (e[0]) {
                        "Author" -> builder.setAuthor(e[1].replace("\r", "").replace("\n", ""))
                        "Name" -> builder.setName(e[1].replace("\r", "").replace("\n", ""))
                        "Desc" -> builder.setDesc(e[1].replace("\r", "").replace("\n", ""))
                        "Version" -> builder.setVersion(e[1].replace("\r", "").replace("\n", ""))
                        "Label" -> builder.setLabel(e[1].replace("\r", "").replace("\n", ""))
                        else -> {
                        }
                    }
                } else {
                    when (e[0]) {
                        "Author", "Name", "Version", "Label" -> return null
                        "Desc" -> builder.setDesc("暂无描述")
                        else -> {
                        }
                    }
                }
            }
            if (info.contains("//QScript.MetaData.Permission.Network")) builder.setNetworkPermission(
                true
            )
            return builder.build()
        }
    }
}

class Builder {
    private var author: String? = null
    private var name: String? = null
    private var label: String? = null
    private var version: String? = null
    private var desc: String? = null
    private var permissionNetwork = false
    fun setAuthor(value: String): Builder {
        author = value
        return this
    }

    fun setName(value: String): Builder {
        name = value
        return this
    }

    fun setLabel(value: String): Builder {
        label = value
        return this
    }

    fun setVersion(value: String): Builder {
        version = value
        return this
    }

    fun setDesc(value: String): Builder {
        desc = value
        return this
    }

    fun setNetworkPermission(z: Boolean) {
        permissionNetwork = z
    }

    fun build(): QScriptInfo {
        return QScriptInfo(
            name ?: "",
            label ?: "",
            author ?: "",
            version ?: "",
            desc ?: "",
            permissionNetwork
        )
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}
