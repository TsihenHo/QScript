package tsihen.me.qscript.script

import bsh.EvalError
import bsh.Interpreter
import tsihen.me.qscript.config.ConfigManager
import tsihen.me.qscript.script.api.ScriptApi
import tsihen.me.qscript.script.params.*
import tsihen.me.qscript.util.log
import tsihen.me.qscript.util.qqApplication

class QScript(private val instance: Interpreter, private val code: String) {
    private val info: QScriptInfo = QScriptInfo.getInfo(code) ?: throw RuntimeException("无效脚本")
    private var enable = false
    private var init = false

    fun isEnable(): Boolean {
        enable = ConfigManager.getDefaultConfig().getOrDefault("script_enable_${getLabel()}", false)
        return enable
    }

    fun getName(): String = info!!.name
    fun getLabel(): String = info!!.label
    fun getVersion(): String = info!!.version
    fun getAuthor(): String = info!!.author
    fun getDesc(): String = info!!.desc
    fun getCode(): String = code

    fun setEnable(z: Boolean) {
        ConfigManager.getDefaultConfig()["script_enable_${getLabel()}"] = z
        enable = z
    }

    fun setInitToFalse() {
        init = false
    }

    // 事件处理器：
    fun onLoad() {
        try {
            if (!init) {
                instance.eval(code)
                init = true
            }
            instance.set("ctx", qqApplication)
            instance.set("thisScript", this)
            instance.set("api", ScriptApi.get(this))
            // TODO 设置 QNum
            instance.eval("onLoad()")
            QScriptManager.addEnable()
        } catch (evalError: EvalError) {
            log(evalError)
        }
    }

    fun onGroupMessage(param: GroupTextMessageParam) {
        if (!init) return
        try {
            instance.set("groupMessageParam", param)
            instance.eval("onGroupMessage(groupMessageParam)")
        } catch (evalError: EvalError) {
            log(evalError)
        }
    }

    fun onFriendMessage(param: FriendTextMessageParam) {
        if (!init) return
        try {
            instance.set("friendMessageParam", param)
            instance.eval("onFriendMessage(friendMessageParam)")
        } catch (evalError: EvalError) {
            log(evalError)
        }
    }

    fun onFriendRequest(param: FriendRequestParam) {
        if (!init) return
        try {
            instance.set("friendRequestParam", param)
            instance.eval("onFriendRequest(friendRequestParam)")
        } catch (evalError: EvalError) {
            log(evalError)
        }
    }

    fun onFriendAdded(param: FriendAddedParam) {
        if (!init) return
        try {
            instance.set("friendAddedParam", param)
            instance.eval("onFriendAdded(friendAddedParam)")
        } catch (evalError: EvalError) {
            log(evalError)
        }
    }

    fun onGroupRequest(param: GroupRequestParam) {
        if (!init) return
        try {
            instance.set("groupRequestParam", param)
            instance.eval("onGroupRequest(groupRequestParam)")
        } catch (evalError: EvalError) {
            log(evalError)
        }
    }

    fun onGroupJoined(param: GroupJoinedParam) {
        if (!init) return
        try {
            instance.set("groupJoinedParam", param)
            instance.eval("onGroupJoined(groupJoinedParam)")
        } catch (evalError: EvalError) {
            log(evalError)
        }
    }

    companion object {
        fun create(lp: Interpreter, code: String): QScript {
            return QScript(lp, code)
        }
    }
}