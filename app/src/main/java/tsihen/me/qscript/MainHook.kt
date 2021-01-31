package tsihen.me.qscript

import android.app.Activity
import android.content.Context
import android.os.Bundle
import dalvik.system.DexClassLoader
import dalvik.system.PathClassLoader
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import tsihen.me.qscript.hook.AbsDelayableHook
import tsihen.me.qscript.hook.JumpActivityHook
import tsihen.me.qscript.util.*

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
        XposedHelpers.findAndHookMethod(
            splashActivity,
            "doOnCreate",
            Bundle::class.java,
            object : XC_MethodHook(51) {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val ctx = param.thisObject as Activity
                    if (secInited) return
                    if (System.getProperty(QS_FULL_TAG) == "true") {
                        loge("Error: QScript reload.Stop it.")
                        return
                    }
                    System.setProperty(QS_FULL_TAG, "true")
                    try {
                        // 注入模块
//                        val apkFile = JavaUtil.findApkFile(qqApplication, PACKAGE_NAME_SELF)
//                            ?: throw NullPointerException("ApkFile is null.")
//                        val loader = DexClassLoader(
//                            apkFile.absolutePath,
//                            ctx.getDir("dex", Context.MODE_PRIVATE).absolutePath,
//                            null,
//                            ctx.classLoader as PathClassLoader
//                        )
//                        JavaUtil.loadPlugin(loader, ctx)
                        getInstance().performHook(ctx)
                    } catch (e: Exception) {
                        log(e)
                    }
                    secInited = true
                }
            })
        firstInited = true
    }

    fun performHook(ctx: Activity) {
        var failed = false
        try {
            logd("进入主Hook")
            Initiator.init(ctx.classLoader)
            if (thirdInited) return
            logi("MainHook : AppId = ${android.os.Process.myPid()}")
            JumpActivityHook.loadDex(ctx)
            initDebugMode()
            AbsDelayableHook.queryDelayableHooks().forEach { if (!it.init()) failed = true }
        } catch (e: Throwable) {
            log(e)
            failed = true
        }
        if (failed) {
            Toasts.error(ctx, "错误：QScript 初始化失败")
            return
        }
        Toasts.success(ctx, "QScript 完成初始化")
        thirdInited = true
    }
}