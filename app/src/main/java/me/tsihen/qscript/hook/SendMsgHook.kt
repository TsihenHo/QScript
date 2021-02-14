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

import android.content.Context
import me.tsihen.qscript.script.api.ScriptApi.getName
import me.tsihen.qscript.script.objects.MessageData
import me.tsihen.qscript.util.*
import java.lang.reflect.Method

class SendMsgHook : AbsDelayableHook() {
    companion object {
        private val self = SendMsgHook()
        fun get() = self
    }

    var inited = false
    private var sendTextMethod: Method? = null
    private var sendArkAppMethod: Method? = null
    private var sendAbsStructMethod: Method? = null
    private var sendPicMethod: Method? = null
    private lateinit var sessionInfoClass: Class<*>
    private lateinit var qqAppInterfaceClass: Class<*>
    private lateinit var sendMsgParamsClass: Class<*>

    override fun init(): Boolean {
        if (inited) return true
        try {
            sessionInfoClass = ClassFinder.findClass(C_SESSION_INFO)!!
            qqAppInterfaceClass = ClassFinder.findClass(C_QQ_APP_INTERFACE)!!
            sendMsgParamsClass = Initiator.load(".activity.ChatActivityFacade\$SendMsgParams")!!
        } catch (e: KotlinNullPointerException) {
            loge("SendMsgHook : FATAL : Didn't find SessionInfo or QQAppInterface or SendMsgParams.")
            log(e)
            return false
        }
        val chatActivityFacade = ClassFinder.findClass(C_CHAT_ACTIVITY_FACADE)
        if (chatActivityFacade == null) {
            loge("SendMsgHook : FATAL : Didn't find ChatActivityFacade.")
            return false
        }
        try {
            sendTextMethod = chatActivityFacade.getDeclaredMethod(
                "a",
                qqAppInterfaceClass,
                Context::class.java,
                sessionInfoClass,
                String::class.java,
                ArrayList::class.java,
                sendMsgParamsClass
            )
            val absStructMsgClass = Initiator.load(".structmsg.AbsStructMsg")!!
            val arkAppMessageClass = Initiator.load(".data.ArkAppMessage")!!
            for (m in ClassFinder.findClass(C_CHAT_ACTIVITY_FACADE)!!.methods) {
                val clz = m.parameterTypes

                if (clz.size == 3) {
                    if (clz[0].name == qqAppInterfaceClass.name &&
                        clz[1].name == sessionInfoClass.name
                    ) {
                        if (clz[2].name == arkAppMessageClass.name && m.returnType == Boolean::class.java)
                            sendArkAppMethod = m
                        else if (clz[2].name == absStructMsgClass.name && m.returnType == Void.TYPE)
                            sendAbsStructMethod = m
                    }
                } else if (clz.size == 4) {
                    if (clz[0].name == qqAppInterfaceClass.name &&
                        clz[1].name == sessionInfoClass.name &&
                        clz[2].name == Initiator.load(".data.MessageForPic")!!.name &&
                        clz[3] == Int::class.java &&
                        m.returnType == Void.TYPE
                    ) {
                        logd("find photo sender")
                        sendPicMethod = m
                    }
                }

                if (sendAbsStructMethod != null && sendArkAppMethod != null && sendPicMethod != null)
                    break
            }
        } catch (e: NoSuchMethodException) {
            loge("SendMsgHook : FATAL : Didn't find the method which can send msg.")
            log(e)
            return false
        }
        inited = true
        return true
    }

    override fun getEnabled(): Boolean = true

    override fun setEnabled(z: Boolean) {
    }

    private fun getSendMsgMethod(): Method? {
        return if (sendTextMethod != null) {
            sendTextMethod
        } else {
            logw("SendMsgHook : Get method before init.")
            null
        }
    }

    /**
     * @param qNum 消息接受者
     */
    fun sendText(msg: String, qNum: Long) {
        val method = getSendMsgMethod() ?: return
        val ctx = getApplicationNonNull()
        method.invoke(
            null,
            qqAppInterface,
//            getAppRuntime(),
            ctx,
            buildSessionInfo(qNum.toString()),
            msg,
            arrayListOf<Any?>(),
            sendMsgParamsClass.newInstance()
        )
    }

    /**
     * 这个是通过 data 发送消息
     */
    fun sendText(data: MessageData, msg: String, at: Array<Long>) {
        val method = getSendMsgMethod() ?: return
        val ctx = getApplicationNonNull()
        val arrayList = arrayListOf<Any?>()
        val atTroopMemberInfo = Initiator.load(".data.AtTroopMemberInfo")!!
        at.forEach {
            val obj = atTroopMemberInfo.newInstance()
            setObject(obj, "uin", it)
            setObject(obj, "textLen", msg.length.toShort())
            setObject(obj, "wExtBufLen", 1.toShort())
            setObject(obj, "startPos", 0.toShort())
            arrayList.add(obj)
        }
        method.invoke(
            null,
            qqAppInterface,
//            getAppRuntime(),
            ctx,
            data.sessionInfo,
            msg,
            arrayList,
            sendMsgParamsClass.newInstance()
        )
    }

    /**
     * 这个是发送群聊消息
     *
     * @param qNum 消息接受者
     */
    fun sendText(msg: String, qNum: Long, at: Array<Long>) {
        val method = getSendMsgMethod() ?: return
        val ctx = getApplicationNonNull()
        val arrayList = arrayListOf<Any?>()
        val atTroopMemberInfo = Initiator.load(".data.AtTroopMemberInfo")!!
        at.forEach {
            val obj = atTroopMemberInfo.newInstance()
            setObject(obj, "uin", it)
            setObject(obj, "textLen", getName(it.toString(), qNum.toString()).length.toShort() + 1)
            setObject(obj, "wExtBufLen", 1.toShort())
            setObject(obj, "startPos", msg.indexOf(it.toString()).toShort() - 1)
            arrayList.add(obj)
        }
        method.invoke(
            null,
            qqAppInterface,
//            getAppRuntime(),
            ctx,
            buildSessionInfo(qNum.toString(), true),
            msg,
            arrayList,
            sendMsgParamsClass.newInstance()
        )
    }

    /**
     * 发送 JSON 消息
     */
    fun sendArkApp(msg: String, qNum: Long, isGroup: Boolean) {
        if (sendArkAppMethod == null) return
        try {
            val session = buildSessionInfo(qNum.toString(), isGroup)
            val arkAppMsg = Initiator.load(".data.ArkAppMessage")!!.newInstance()
            if (!(arkAppMsg.callVisualMethod(
                    "fromAppXml",
                    msg,
                    String::class.java,
                    Boolean::class.java
                ) as Boolean)
            ) return
            sendArkAppMethod?.invoke(null, qqAppInterface, session, arkAppMsg)
        } catch (e: Exception) {
            log(e)
        }
    }

    /**
     * 发送 XML 消息
     */
    fun sendAbsStruct(msg: String, qNum: Long, isGroup: Boolean) {
        if (sendAbsStructMethod == null) return
        try {
            val session = buildSessionInfo(qNum.toString(), isGroup)
            val absStructMsg = Initiator.load(".structmsg.TestStructMsg")
                ?.callStaticMethod(
                    "a",
                    msg,
                    String::class.java,
                    Initiator.load(".structmsg.AbsStructMsg")
                )
            sendAbsStructMethod?.invoke(null, qqAppInterface, session, absStructMsg)
        } catch (e: Exception) {
            log(e)
        }
    }

    /**
     * 发送图片消息
     */
    fun sendPic(path: String, qNum: Long, isGroup: Boolean) {
        if (sendPicMethod == null) return
        try {
            val session = buildSessionInfo(qNum.toString(), isGroup)
            val chatMessage =
                ClassFinder.findClass(C_CHAT_ACTIVITY_FACADE)!!
                    .callStaticMethod(
                        "a",
                        qqAppInterface,
                        session,
                        path,
                        qqAppInterfaceClass,
                        sessionInfoClass,
                        String::class.java,
                        Initiator.load(".data.ChatMessage")
                    )
            sendPicMethod?.invoke(null, qqAppInterface, session, chatMessage, 0)
        } catch (e: java.lang.Exception) {
            log(e)
        }
    }

    private fun buildSessionInfo(qNum: String, isGroup: Boolean = false): Any? {
        val s = sessionInfoClass.newInstance()
        setObject(s, "a", qNum, String::class.java)
        setObject(s, "a", System.currentTimeMillis(), Long::class.java)
        setObject(s, "a", if (isGroup) 1 else 0, Int::class.java)
        setObject(s, "b", 32, Int::class.java)
        setObject(s, "c", 1, Int::class.java)
        setObject(s, "d", 10004, Int::class.java)
        return s
    }
}