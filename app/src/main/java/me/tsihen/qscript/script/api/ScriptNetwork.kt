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
@file:Suppress("unused")

package me.tsihen.qscript.script.api

import me.tsihen.qscript.script.QScript
import me.tsihen.qscript.util.log
import org.jsoup.Connection
import org.jsoup.Jsoup

class ScriptNetwork(val script: QScript) {
    private var bridge: Connection? = null

    fun fromUrl(url: String): Connection? {
        try {
            if (!script.getPermissionNetwork()) return null
            bridge = Jsoup.connect(url)
            return bridge!!
        } catch (e: Exception) {
            log(e)
            return null
        }
    }
}