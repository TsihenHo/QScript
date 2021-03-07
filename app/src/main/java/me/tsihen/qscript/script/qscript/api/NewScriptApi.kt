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
package me.tsihen.qscript.script.qscript.api

import me.tsihen.qscript.script.qscript.QScript
import me.tsihen.qscript.script.qscript.objects.MessageData
import me.tsihen.qscript.util.*

@Suppress("unused")
class NewScriptApi(private val script: QScript) : ScriptApi(script) {
    fun sendTextMsg(data: MessageData, msg: String) {
        if (data.isGroup) {
            sendTextMsg(msg, data.friendUin.toLong(), longArrayOf())
        } else {
            sendTextMsg(msg, data.friendUin.toLong())
        }
    }

    fun sendTextMsg(data: MessageData, msg: String, atList: Array<String>) {
        val atListLong = atList.map { it.toLong() }.toTypedArray()
        sendTextMsg(msg, data.friendUin.toLong(), LongArray(atListLong.size) { atListLong[it] })
    }

    fun sendPhotoMsg(data: MessageData, path: String) {
        sendPicMsg(path, data.friendUin.toLong(), data.isGroup)
    }

    fun sendCardMsg(data: MessageData, code: String) {
        sendCardMsg(code, data.friendUin.toLong(), data.isGroup)
    }

    fun sendTip(data: MessageData, msg: String) {
        try {
            val messageRecord = ClassFinder.findClass(C_MESSAGE_RECORD)?.newInstance()
            if (messageRecord == null) {
                logw("NewScriptApi : 找不到 MessageRecord")
                return
            }
            val messageFacade = qqAppInterface?.callVirtualMethod("getMessageFacade")
            if (messageFacade == null) {
                logw("NewScriptApi : 找不到 QQMessageFacade")
                return
            }
            messageRecord.callVirtualMethod(
                "initInner",
                data.selfUin,
                data.friendUin,
                data.senderUin,
                msg,
                data.time,
                MSG_TYPE_TIP,
                if (data.isGroup) 1 else 0,
                data.time,
                String::class.java,
                String::class.java,
                String::class.java,
                String::class.java,
                Long::class.java,
                Int::class.java,
                Int::class.java,
                Long::class.java
            )
            val m = messageFacade::class.java.getDeclaredMethod("a",
                messageRecord::class.java,
                String::class.java)
            m.invoke(messageFacade, messageRecord, data.selfUin)
            logd("成功")
        } catch (e: Exception) {
            log(e)
        }
    }
}