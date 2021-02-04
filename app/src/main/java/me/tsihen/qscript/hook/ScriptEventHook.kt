/* QScript - An Xposed module to run scripts on QQ
 * Copyright (C) 2021-20222 chinese.he.amber@gmail.com
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
package me.tsihen.qscript.hook

import me.tsihen.qscript.script.QScriptManager
import me.tsihen.qscript.util.log

class ScriptEventHook : AbsDelayableHook() {
    companion object {
        private val self = ScriptEventHook()
        fun get(): ScriptEventHook = self
    }

    private var inited = false

    override fun init(): Boolean {
        if (inited) return true
        QScriptManager.init()
        return try {
            inited = true
            true
        } catch (e: Throwable) {
            log(e)
            false
        }
    }

    override fun getEnabled(): Boolean = true

    override fun setEnabled(z: Boolean) {
        // nothing
    }
}