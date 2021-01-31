package tsihen.me.qscript

import android.content.Context
import android.os.Bundle
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import tsihen.me.qscript.hook.AbsDelayableHook
import tsihen.me.qscript.hook.JumpActivityHook
import tsihen.me.qscript.util.*
import java.lang.reflect.Method

class MainHook {
    private var firstInited = false
    private var secInited = false
    private var thirdInited = false

    companion object {
        var SELF: MainHook? = null
        fun getInstance(): MainHook {
            if (SELF == null) SELF = MainHook()
            return SELF!!
        }
    }

    fun doInit(classLoader: ClassLoader) {
        if (firstInited) {
            return
        }
        Initiator.init(classLoader)
        val splashActivity: Class<*> =
            classLoader.loadClass("com.tencent.mobileqq.activity.SplashActivity")!!
        var onCreate: Method? = null
        var clazz = splashActivity
        try {
            do {
                try {
                    onCreate = clazz.getDeclaredMethod("onCreate", Bundle::class.java)
                } catch (ignored: NoSuchMethodException) {
                }
                clazz = clazz.superclass!!
            } while (onCreate == null && clazz.superclass != Any::class.java)
        } catch (e: Exception) {
            log(e)
        }
        XposedBridge.hookMethod(
            onCreate,
            object : XC_MethodHook(51) {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val ctx = getQQApplication()!!
                    if (secInited) return
                    if (System.getProperty(QS_FULL_TAG) == "true") {
                        loge("Error: QScript reload.Stop it.")
                        return
                    }
                    System.setProperty(QS_FULL_TAG, "true")
                    try {
                        getInstance().performHook(ctx)
                    } catch (e: Exception) {
                        log(e)
                    }
                    secInited = true
                }
            })
        firstInited = true
    }

    fun performHook(ctx: Context) {
        var failed = false
        try {
            if (thirdInited) return
            JumpActivityHook.loadDex(ctx)
            initDebugMode()
            Natives.load(ctx)
            AbsDelayableHook.queryDelayableHooks().forEach { if (!it.init()) failed = true }
        } catch (e: Throwable) {
            log(e)
            failed = true
        }
        if (failed) {
            Toasts.error(ctx, "错误：QScript 初始化失败")
            return
        }
        thirdInited = true
    }
}