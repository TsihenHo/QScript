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
package me.tsihen.qscript.script.bugscript

import android.content.Context
import bsh.Interpreter
import dalvik.system.DexClassLoader
import dalvik.system.PathClassLoader
import me.tsihen.qscript.script.qscript.QScript
import me.tsihen.qscript.script.qscript.api.ScriptApi
import me.tsihen.qscript.util.*

@Suppress("unused")
class BugScript private constructor(private val instance: Interpreter, private val code: String) {
    init {
        instance.set("context", getApplicationNonNull())
        instance.set("mQQ", getLongAccountUin().toString())
        instance.set("mName", qqAppInterface?.callVirtualMethod("getCurrentNickname") ?: "")

        instance.eval("""
            import static me.tsihen.qscript.script.bugscript.BugScript.Companion;

            public void send(Object data, String msg) { Companion.send(data, msg);}
            public void send(Object data, String msg, String[]  atList) { Companion.send(data, msg, atList);}
            public void sendPhoto(Object data, String msg) { Companion.sendPhoto(data, msg);}
            public void sendCard(Object data, String msg) { Companion.sendCard(data, msg);}
            public void sendPtt(Object data, String msg) { Companion.sendPtt(data, msg);}
            public void sendMixed(Object data, String[][] msg) { Companion.sendMixed(data, msg);}
            public void sendReply(Object data, String msg) { Companion.sendReply(data, msg);}
            public void sendShowPhoto(Object data, String msg) { Companion.sendShowPhoto(data, msg);}
            public void sendTip(Object data, String msg) { Companion.sendTip(data, msg);}
            public void sendShake(Object data) { Companion.sendShake(data);}
            public void record(Object data) { Companion.record(data);}
            public Object createData(boolean isGroup, String uin) { return Companion.createData(isGroup, uin);}
        """.trimIndent())

        instance.eval(code)
    }

    fun onMsg(data: TextData) {
        TODO()
    }

    fun onCardMsg(data: TextData) {
        TODO()
    }

    fun onPicMsg(data: TextData) {
        TODO()
    }

    fun onRawMsg(data: TextData) {
        TODO()
    }

    fun onReplyMsg(data: ReplyData) {
        TODO()
    }

    fun onMixedMsg(data: MixedData) {
        TODO()
    }

    fun onJoin(group: String, uin: String) {
        TODO()
    }

    fun onUnload() {
        TODO()
    }

    companion object {
        private val bridge = ScriptApi.get(QScript.create(Interpreter(), "" +
                "// QScript.MetaData.Start\n" +
                "// QScript.MetaData.Name = BugScript\n" +
                "// QScript.MetaData.Version = 1.0.0\n" +
                "// QScript.MetaData.Author = Tsihen-Ho\n" +
                "// QScript.MetaData.Label = bug-script-${Math.random()}\n" +
                "// QScript.MetaData.End"))

        fun create(instance: Interpreter, code: String) = BugScript(instance, code)

        // apis
        fun send(data: Data, str: String) {
            if (data.isGroup) {
                bridge.sendTextMsg(str, data.friendUin.toLong(), LongArray(0))
            } else {
                bridge.sendTextMsg(str, data.senderUin.toLong())
            }
        }

        fun send(data: Data, str: String, atList: LongArray) {
            bridge.sendTextMsg(str, data.friendUin.toLong(), atList)
        }

        /**
         * In fact it doesn't return anything
         */
        fun sendCard(data: Data, str: String) =
            bridge.sendCardMsg(str, data.senderUin.toLong(), data.isGroup)

        fun sendPhoto(data: Data, path: String) =
            bridge.sendPicMsg(path, data.friendUin.toLong(), data.isGroup)

        fun sendShowPhoto(data: Data, path: String) {
            TODO()
        }

        fun sendPtt(data: Data, path: String) {
            TODO()
        }

        fun sendShake(data: Data) {
            TODO()
        }

        fun sendTip(data: Data, path: String) {
            TODO()
        }

        fun sendReply(data: Data, str: String) {
            TODO()
        }

        fun sendMixed(data: Data, msg: Array<Array<String>>) {
            TODO()
        }

        fun record(data: Data) {
            TODO()
        }

        fun createData(isGroup: Boolean, str: String): Data =
            Data(-1, false, str, isGroup, "null", str, -1L, arrayOf())

        fun shutUp(group: String, uin: String, time: Long) =
            bridge.shutUp(group.toLong(), uin.toLong(), time)

        fun shutUp(group: String, enable: Boolean) = bridge.shutAllUp(group.toLong(), enable)
        fun loadApp(applicationId: String): PathClassLoader {
            val apk = getApplicationNonNull().createPackageContext(applicationId,
                Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY).packageCodePath
            return PathClassLoader(apk, null, getApplicationNonNull().classLoader)
        }

        fun loadDex(path: String) = DexClassLoader(path,
            getApplicationNonNull().cacheDir.absolutePath,
            null,
            getApplicationNonNull().classLoader)

        // other apis
        fun Toast(msg: String) = Toasts.show(getQQApplication(), msg)

        fun print(msg: String) = bridge.log(msg)

        fun load(path: String) {
            TODO()
        }
    }
}