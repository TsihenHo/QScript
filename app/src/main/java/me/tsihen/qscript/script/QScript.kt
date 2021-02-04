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
package me.tsihen.qscript.script

import bsh.EvalError
import bsh.Interpreter
import me.tsihen.qscript.config.ConfigManager
import me.tsihen.qscript.script.api.ScriptApi
import me.tsihen.qscript.script.objects.MessageData
import me.tsihen.qscript.util.getLongAccountUin
import me.tsihen.qscript.util.getQQApplication
import me.tsihen.qscript.util.log

class QScript(private val instance: Interpreter, private val code: String) {
    private val info: QScriptInfo = QScriptInfo.getInfo(code) ?: throw RuntimeException("无效脚本")
    private var enable = false
    private var init = false

    fun isEnable(): Boolean {
        enable = ConfigManager.getDefaultConfig().getOrDefault("script_enable_${getLabel()}", false)
        return enable
    }

    fun getName(): String = info.name
    fun getLabel(): String = info.label
    fun getVersion(): String = info.version
    fun getAuthor(): String = info.author
    fun getDesc(): String = info.desc
    fun getCode(): String = code

    fun setEnable(z: Boolean) {
        ConfigManager.getDefaultConfig()["script_enable_${getLabel()}"] = z
        enable = z
    }

    fun setInitToFalse() {
        init = false
    }

    // 事件处理器：
    fun onLoad() {
        try {
            if (!init) {
                instance.eval(code)
                init = true
            }
            instance.set("ctx", getQQApplication())
            instance.set("thisScript", this)
            instance.set("api", ScriptApi.get(this))
            instance.set("mQNum", getLongAccountUin())
            instance.eval("onLoad()")
            QScriptManager.addEnable()
        } catch (evalError: EvalError) {
            if ((evalError.message ?: "d").contains("Command not found")) return // ignore
            log(evalError)
        }
    }

    fun onMsg(data: MessageData) {
        if (!init) return
        try {
            val m = instance.nameSpace.getMethod("onMsg", arrayOf(Any::class.java))
            m.invoke(arrayOf(data), instance)
        } catch (e: Exception) {
            log(e)
        }
    }

    companion object {
        fun create(lp: Interpreter, code: String): QScript {
            return QScript(lp, code)
        }
    }
}