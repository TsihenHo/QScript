package me.tsihen.qscript.hook

import me.tsihen.qscript.util.*

class NoMiniappHook : AbsDelayableHook() {
    companion object {
        private val self = NoMiniappHook()
        fun get() = self
    }

    private var inited = false

    override fun init(): Boolean {
        if (!getEnabled()) return true
        if (inited) return true
        try {
            val clz = Initiator.load(".mini.entry.MiniAppDesktop")
                ?: {
                    logw("NoMiniappHook : 找不到下拉小程序")
                    Initiator::class.java
                }.invoke()
            if (clz == Initiator::class.java) return false
            findMethodBySignWithRegex("onTouchMoving\\(.+\\)V", clz).before {
//                val action = it.args[2].callVirtualMethod("getAction", Int::class.java) as Int
//                if (action and 255 == 2) it.result = Void.TYPE
                if (!getEnabled()) return@before
                it.result = Void.TYPE
            }
        } catch (e: Exception) {
            log(e)
            return false
        }
        return true
    }
}