package tsihen.me.qscript.hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import tsihen.me.qscript.script.params.ParamFactory
import tsihen.me.qscript.util.*

class GetMsgHook : AbsDelayableHook() {
    companion object {
        private val self = GetMsgHook()
        fun get() = self
    }

    private var inited = false

    override fun init(): Boolean {
        if (inited) return true
        try {
            val getMsgMethod = Initiator.load(".troop.data.TroopMessageProcessor")!!.getDeclaredMethod(
                "a",
                Initiator.load("com.tencent.qphone.base.remote.ToServiceMsg"),
                java.util.ArrayList::class.java,
                Initiator.load("msf.msgsvc.msg_svc\$PbGetGroupMsgResp"),
                String::class.java
            )
            XposedBridge.hookMethod(getMsgMethod, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
//                    var content = ""
//                    var uin = ""
//
//                    uin = getObject<String>(param.args[0], "senderuin") ?: ""
//                    content = getObject<String>(param.args[0], "msg") ?: ""
//
//                    val p = ParamFactory.friendMessage()
//                    p.setContent(content)
//                    p.setUin(uin)
                    logi("GetMsgHook : 收到消息")
//                    QScriptEventSender.broadcastFriendMessage(p)
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