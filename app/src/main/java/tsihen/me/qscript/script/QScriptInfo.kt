@file:JvmName("QScriptInfo")
package tsihen.me.qscript.script

import tsihen.me.qscript.util.logd

class QScriptInfo(
    val name: String,
    val label: String,
    val author: String,
    val version: String,
    val desc: String
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

    fun build(): QScriptInfo {
        return QScriptInfo(name ?: "", label ?: "", author ?: "", version ?: "", desc ?: "")
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}
