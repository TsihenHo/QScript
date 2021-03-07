package me.tsihen.qscript.hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import me.tsihen.qscript.script.qscript.QScriptEventSender
import me.tsihen.qscript.script.qscript.objects.MemberJoinData
import me.tsihen.qscript.util.C_QQ_APP_INTERFACE
import me.tsihen.qscript.util.ClassFinder
import me.tsihen.qscript.util.Initiator

class OnJoinHook : AbsDelayableHook() {
    companion object {
        private val self = OnJoinHook()
        fun get() = self
    }

    override fun init(): Boolean {
        XposedHelpers.findAndHookMethod(
            Initiator.load(".service.message.codec.decoder.TroopAddMemberBroadcastDecoder"),
            "a",
            ClassFinder.findClass(C_QQ_APP_INTERFACE),
            Int::class.java,
            String::class.java,
            String::class.java,
            Long::class.java,
            Long::class.java,
            Long::class.java,
            "msf.msgcomm.msg_comm\$MsgHead",
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    super.beforeHookedMethod(param)
                    // args[3] : 群主
                    // args[5] : 入群时间戳，单位：s
                    QScriptEventSender.doOnJoin(
                        MemberJoinData.build(
                            param.args[2] as String,
                            param.args[4].toString()
                        )
                    )
                }
            }
        )
        return true
    }

    override fun getEnabled(): Boolean = true

    override fun setEnabled(z: Boolean) {
    }
}