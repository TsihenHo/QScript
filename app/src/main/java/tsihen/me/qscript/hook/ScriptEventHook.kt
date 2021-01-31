package tsihen.me.qscript.hook

import tsihen.me.qscript.script.QScriptManager
import tsihen.me.qscript.util.log

class ScriptEventHook : AbsDelayableHook() {
    companion object {
        private val self = ScriptEventHook()
        fun get(): ScriptEventHook = self
    }
    private var inited = false

    override fun init(): Boolean {
        if (inited) return true
        QScriptManager.init()
        return try {
            inited = true
            true
        } catch (e: Throwable) {
            log(e)
            false
        }
    }

    override fun getEnabled(): Boolean = true

    override fun setEnabled(z: Boolean) {
        // nothing
    }
}