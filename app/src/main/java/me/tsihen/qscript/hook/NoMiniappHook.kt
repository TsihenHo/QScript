package me.tsihen.qscript.hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
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
            XposedBridge.hookAllConstructors(clz, object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    super.beforeHookedMethod(param)
                    param.throwable = UnsupportedOperationException("小程序已经关闭")
                }
            })
            findMethodBySignWithRegex("onTouchMoving\\(.+\\)V", clz).before {
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