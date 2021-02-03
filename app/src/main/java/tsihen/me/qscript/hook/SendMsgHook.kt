package tsihen.me.qscript.hook

import android.content.Context
import android.os.Build
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import tsihen.me.qscript.util.*
import java.lang.reflect.Method

class SendMsgHook : AbsDelayableHook() {
    companion object {
        private val self = SendMsgHook()
        fun get() = self
    }

    var inited = false
    private var sendMsgMethod: Method? = null
    private lateinit var sessionInfo: Class<*>
    private lateinit var qqAppInterface: Class<*>
    private lateinit var sendMsgParams: Class<*>

    private var qqAppInterfaceObject: Any? = null
    private var qqContextObject: Any? = null

    override fun init(): Boolean {
        if (inited) return true
        try {
            sessionInfo = Initiator.load(".activity.aio.SessionInfo")!!
            qqAppInterface = Initiator.load(".app.QQAppInterface")!!
            sendMsgParams = Initiator.load(".activity.ChatActivityFacade\$SendMsgParams")!!
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
            sendMsgMethod = chatActivityFacade.getDeclaredMethod(
                "a",
                qqAppInterface,
                Context::class.java,
                sessionInfo,
                String::class.java,
                ArrayList::class.java,
                sendMsgParams
            )
        } catch (e: NoSuchMethodException) {
            loge("SendMsgHook : FATAL : Didn't find the method which can send msg.")
            log(e)
            return false
        }
        XposedBridge.hookMethod(sendMsgMethod, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                qqAppInterfaceObject = param.args[0]
                qqContextObject = param.args[1]
            }
        })
        inited = true
        return true
    }

    override fun getEnabled(): Boolean = true

    override fun setEnabled(z: Boolean) {
    }

    private fun getSendMsgMethod(): Method? {
        return if (sendMsgMethod != null) {
            sendMsgMethod
        } else {
            logw("SendMsgHook : Get method before init.")
            null
        }
    }

    /**
     * @param qNum 消息接受者
     */
    fun sendText(msg: String, qNum: Long, vararg at: Long) {
        val method = getSendMsgMethod() ?: return
        val ctx = getApplicationNonNull()
        val arrayList = arrayListOf<Any?>()
        val atTroopMemberInfo = Initiator.load(".data.AtTroopMemberInfo")!!
        at.forEach {
            val obj = atTroopMemberInfo.newInstance()
            setObject(obj, "uin", it)
            setObject(obj, "textLen", msg.length)
            setObject(obj, "wExtBufLen", 0)
            setObject(obj, "startPos", 1)
            arrayList.add(obj)
        }
        method.invoke(
            null,
            getAppRuntime(),
            ctx,
            buildSessionInfo(qNum.toString()),
            msg,
            arrayList,
            sendMsgParams.newInstance()
        )
    }

    private fun buildSessionInfo(qNum: String): Any? {
        val s = sessionInfo.newInstance()
        setObject(s, "a", qNum, String::class.java)
        setObject(s, "a", System.currentTimeMillis(), Long::class.java)
        setObject(s, "a", 0, Int::class.java)
        setObject(s, "b", 32, Int::class.java)
        setObject(s, "c", 1, Int::class.java)
        setObject(s, "d", 10004, Int::class.java)
        return s
    }
}