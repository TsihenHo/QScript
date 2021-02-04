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

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.tsihen.qscript.script.QScriptEventSender
import me.tsihen.qscript.script.objects.MessageData.Companion.getMessage
import me.tsihen.qscript.util.C_QQ_APP_INTERFACE
import me.tsihen.qscript.util.ClassFinder
import me.tsihen.qscript.util.Initiator
import me.tsihen.qscript.util.log

class GetMsgHook : AbsDelayableHook() {
    companion object {
        private val self = GetMsgHook()
        fun get() = self
    }

    private var inited = false

    override fun init(): Boolean {
        if (inited) return true
        try {
            val getMsgMethod = Initiator.load(".app.MessageHandlerUtils")!!.getDeclaredMethod(
                "a",
                ClassFinder.findClass(C_QQ_APP_INTERFACE),
                Initiator.load(".data.MessageRecord"),
                true.javaClass
            )
            XposedBridge.hookMethod(getMsgMethod, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val p = getMessage(param.args[0], param.args[1])
                    QScriptEventSender.doOnMsg(p)
                }
            })
        } catch (e: Exception) {
            log(e)
            return false
        }
        return true
    }

    override fun getEnabled(): Boolean = true

    override fun setEnabled(z: Boolean) {}
}