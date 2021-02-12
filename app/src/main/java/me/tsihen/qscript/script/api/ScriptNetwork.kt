package me.tsihen.qscript.script.api

import me.tsihen.qscript.script.QScript
import org.jsoup.Connection
import org.jsoup.Jsoup

class ScriptNetwork(val script: QScript) {
    private var bridge: Connection? = null

    fun fromUrl(url: String): Connection? {
        if (!script.getPermissionNetwork()) return null
        bridge = Jsoup.connect(url)
        return bridge!!
    }
}