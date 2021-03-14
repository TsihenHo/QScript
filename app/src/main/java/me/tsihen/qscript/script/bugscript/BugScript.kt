package me.tsihen.qscript.script.bugscript

import bsh.EvalError
import bsh.Interpreter
import me.tsihen.qscript.script.qscript.QScript
import me.tsihen.qscript.script.qscript.QScriptInfo
import me.tsihen.qscript.script.qscript.api.NewScriptApi
import me.tsihen.qscript.script.qscript.api.ScriptApi
import me.tsihen.qscript.script.qscript.objects.MessageData
import me.tsihen.qscript.util.getLongAccountUin
import me.tsihen.qscript.util.getQQApplication
import me.tsihen.qscript.util.scriptLog

class BugScript(instance: Interpreter, code: String, private val fileName: String) :
    QScript(instance, code) {
    override val info: QScriptInfo = QScriptInfo(fileName, fileName, "未知", "未知", "无", true)

    override fun onLoad() {
        try {
            val uin = getLongAccountUin().toString()
            instance.set("context", getQQApplication())
            instance.set("api", NewScriptApi(this))
            instance.set("mQQ", uin)
            instance.set("mName", ScriptApi.getName(uin, uin))
            if (!init) {
                instance.eval("""
                    public void send(Object o, String s) { api.sendTextMsg(o, s); }
                    public Object createData(String a, Boolean b) {
                        return api.createData(a, b); 
                    }
                    
                """.trimIndent())
                instance.eval(code)
                init = true
            }
        } catch (evalError: EvalError) {
            if ((evalError.message ?: "d").contains("Command not found")) return // ignore
            scriptLog(evalError)
        }
    }

    @Throws(IllegalArgumentException::class)
    override fun onMsg(data: MessageData) {
        if (data !is BugMessageData) throw IllegalArgumentException("参数必须为 BugMessageData")
        super.onMsg(data)
    }
}