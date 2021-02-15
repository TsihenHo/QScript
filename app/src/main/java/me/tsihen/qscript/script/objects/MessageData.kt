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
package me.tsihen.qscript.script.objects

import me.tsihen.qscript.util.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class MessageData {
    var senderUin: String = ""

    var content: String = ""

    var content2: String = ""

    var isGroup = false

    var atMe = false

    var time = -1L

    var friendUin: String = ""

    var nickname: String = ""

    var sessionInfo: Any? = null

    var selfUin = ""

    var id = -1L

    var atList: LinkedList<String>? = null

    var source: String = ""

    var type = 0

    fun isGroupMsg() = isGroup

    companion object {
        fun getMessage(qqAppInterface: Any, messageRecord: Any): MessageData {
            val data = MessageData()
            try {
                val isTroop = getObject<Int>(messageRecord, "istroop")
                val senderUin = getObject<String>(messageRecord, "senderuin") ?: ""
                val session = ClassFinder.findClass(C_SESSION_INFO)!!.newInstance()
                setObject(session, "a", isTroop, Int::class.java)
                setObject(session, "a", senderUin, String::class.java)
                val atList = LinkedList<String>()
                try {
                    val jsonObject: JSONObject = getObject(messageRecord, "mExJsonObject")
                        ?: throw NoSuchFieldException("ignored")
                    if (jsonObject.has("troop_at_info_list")) {
                        val atMemberString = jsonObject.getString("troop_at_info_list")
                        val atMemberArray: JSONArray? = JSONArray(atMemberString)
                        if (atMemberArray != null) {
                            for (i: Int in 0..atMemberArray.length()) {
                                atList.add(atMemberArray.getJSONObject(i).getLong("uin").toString())
                            }
                        }
                    }
                    logd("JSONObject : $jsonObject")
                } catch (ignored: Throwable) {
                }

                data.atMe = atList.contains(getLongAccountUin().toString())
                data.atList = atList
                data.sessionInfo = session
                data.senderUin = senderUin
                data.selfUin = getObject<String>(messageRecord, "selfuin") ?: ""
                data.friendUin = getObject<String>(messageRecord, "frienduin") ?: ""
                data.time = getObject<Long>(messageRecord, "time") ?: -1L
                data.isGroup = isTroop == 1
                data.content = getObject<String>(messageRecord, "msg") ?: ""
                data.content2 = getObject<String>(messageRecord, "msg2") ?: ""
                data.id = getObject<Long>(messageRecord, "msgUid") ?: {
                    logw("MessageData : GetMessage : 找不到 MsgId")
                    -1L
                }.invoke()
                data.nickname = Initiator.load(".utils.ContactUtils")?.callStaticMethod(
                    "a",
                    qqAppInterface,
                    data.senderUin,
                    data.friendUin,
                    1,
                    0,
                    ClassFinder.findClass(C_QQ_APP_INTERFACE),
                    String::class.java,
                    String::class.java,
                    Int::class.java,
                    Int::class.java
                ) as? String? ?: ""

                when (messageRecord.javaClass.simpleName) {
                    ("MessageForText") -> data.type = 1 // 1 for text
                    ("MessageForPic") -> data.type = 2 // 2 for pic
                    ("MessageForStructing") -> {
                        data.type = 3 // 3 for xml
                        data.source = getObject<Any>(
                            messageRecord,
                            "structingMsg"
                        )?.callVisualMethod("getXml") as? String? ?: ""
                    }
                    ("MessageForArkApp") -> {
                        data.type = 4 // 4 for json
                        data.source = getObject<Any>(
                            messageRecord,
                            "ark_app_message"
                        )?.callVisualMethod("toAppXml") as? String? ?: ""
                    }
                    ("MessageForReplyMsg") -> data.type = 5 // 5 for reply
                    ("MessageForMixedMsg") -> data.type = 6 // 6 for mixed
                    else -> data.type = 0 // others
                }
            } catch (e: Exception) {
                log(e)
            }
            return data
        }
    }
}