/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
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
package me.tsihen.qscript.script.qscript

import bsh.EvalError
import bsh.Interpreter
import me.tsihen.qscript.config.ConfigManager
import me.tsihen.qscript.script.qscript.api.NewScriptApi
import me.tsihen.qscript.script.qscript.objects.MemberJoinData
import me.tsihen.qscript.script.qscript.objects.MessageData
import me.tsihen.qscript.util.FromQNotified
import me.tsihen.qscript.util.getLongAccountUin
import me.tsihen.qscript.util.getQQApplication
import me.tsihen.qscript.util.scriptLog

open class QScript protected constructor(
    protected val instance: Interpreter,
    @get:JvmName("getScriptCode") protected val code: String,
) {
    protected open val info: QScriptInfo =
        QScriptInfo.getInfo(code) ?: throw RuntimeException("无效脚本")

    @set:JvmName("setIsEnable")
    protected var enable = false
    protected var init = false

    fun isEnable(): Boolean {
        enable = ConfigManager.getDefaultConfig().getOrDefault("script_enable_${getLabel()}", false)
        return enable
    }

    fun setEnable(value: Boolean) {
        ConfigManager.getDefaultConfig()["script_enable_${getLabel()}"] = value
        enable = value
    }

    fun getName(): String = info.name
    fun getLabel(): String = info.label
    fun getVersion(): String = info.version
    fun getAuthor(): String = info.author
    fun getDesc(): String = info.desc + if (getPermissionNetwork()) "\n警告：该脚本可以使用网络" else ""
    fun getPermissionNetwork(): Boolean = info.permissionNetwork
    fun getCode(): String = code

    // 事件处理器：
    @FromQNotified
    open fun onLoad() {
        try {
            instance.set("ctx", getQQApplication())
            instance.set("thisScript", this)
//            instance.set("api", ScriptApi.get(this))
            instance.set("api", NewScriptApi(this))
            instance.set("mQNum", getLongAccountUin())
            if (!init) {
                instance.eval(code)
                init = true
            }
            Thread {
                Thread.sleep(100)
                instance.eval("onLoad()")
            }.start()
            QScriptManager.addEnable()
        } catch (evalError: EvalError) {
            if ((evalError.message ?: "d").contains("Command not found")) return // ignore
            scriptLog(evalError)
        }
    }

    open fun onMsg(data: MessageData) {
        if (!init) return
        try {
            val m = instance.nameSpace.getMethod("onMsg", arrayOf(Any::class.java))
            Thread {
                m.invoke(arrayOf(data), instance)
            }.start()
        } catch (e: Exception) {
            scriptLog(e)
        }
    }

    open fun onJoin(data: MemberJoinData) {
        if (!init) return
        try {
            val m = instance.nameSpace.getMethod("onJoin", arrayOf(Any::class.java))
            Thread {
                m.invoke(arrayOf(data), instance)
            }.start()
        } catch (e: Exception) {
            scriptLog(e)
        }
    }

    companion object {
        @FromQNotified
        fun create(lp: Interpreter, code: String): QScript {
            return QScript(lp, code)
        }
    }
}