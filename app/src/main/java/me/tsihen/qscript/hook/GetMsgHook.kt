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
package me.tsihen.qscript.hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.tsihen.qscript.script.qscript.QScriptEventSender
import me.tsihen.qscript.script.qscript.objects.MessageData.Companion.getMessage
import me.tsihen.qscript.util.*

class GetMsgHook : AbsDelayableHook() {
    companion object {
        private val self = GetMsgHook()
        fun get() = self
    }

    private var inited = false
    private val msgDone = mutableListOf<Long>()

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
                    try {
                        val p = getMessage(param.args[0], param.args[1])
                        val type = param.args[1].javaClass
                        logd("GetMsgHook : type is ${type.simpleName}.")
                        // 仅仅接受用户发送的部分类型消息
                        val loader = Initiator.getHostClassLoader()!!
                        if (
                            !(loader.loadClass("$PACKAGE_NAME_QQ.data.MessageForText"))
                                .isAssignableFrom(type) &&
                            !(loader.loadClass("$PACKAGE_NAME_QQ.data.MessageForLongMsg"))
                                .isAssignableFrom(type) &&
                            !(loader.loadClass("$PACKAGE_NAME_QQ.data.MessageForPic"))
                                .isAssignableFrom(type) &&
                            !(loader.loadClass("$PACKAGE_NAME_QQ.data.MessageForStructing"))
                                .isAssignableFrom(type) &&
                            !(loader.loadClass("$PACKAGE_NAME_QQ.data.MessageForArkApp"))
                                .isAssignableFrom(type) &&
                            !(loader.loadClass("$PACKAGE_NAME_QQ.data.MessageForReplyText"))
                                .isAssignableFrom(type) &&
                            !(loader.loadClass("$PACKAGE_NAME_QQ.data.MessageForMixedMsg"))
                                .isAssignableFrom(type)
                        ) return

                        // 防止重复
                        if (msgDone.contains(p.id)) return
                        msgDone.add(p.id)
                        // 控制内存
                        if (msgDone.size > 10) msgDone.removeAt(0)

                        QScriptEventSender.doOnMsg(p)
                    } catch (e: Exception) {
                        log(e)
                    }
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