package tsihen.me.qscript

import android.content.Context
import android.os.Build
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tsihen.me.qscript.util.*
import java.io.File
import java.lang.reflect.Field
import java.lang.reflect.Method

class StartupHook : IXposedHookLoadPackage {
    private var firstInited = false
    private var secInited = false

    override fun handleLoadPackage(p0: XC_LoadPackage.LoadPackageParam) {
        // Hook 自己
        if (p0.packageName == PACKAGE_NAME_SELF) {
            XposedHelpers.findAndHookMethod(
                "${PACKAGE_NAME_SELF}.util.Utils",
                p0.classLoader,
                "getActiveModuleVersion",
                XC_MethodReplacement.returnConstant(QS_VERSION_NAME)
            )
            return
        }
        // 不是 QQ\TIM 不 Hook
        if (p0.packageName != PACKAGE_NAME_QQ && p0.packageName != PACKAGE_NAME_TIM) {
            return
        }
        this.doInit(p0.classLoader)
    }

    fun doInit(classLoader: ClassLoader) {
        if (firstInited) {
            return
        }
        val startup: XC_MethodHook = object : XC_MethodHook(51) {
            @Throws(Throwable::class)
            override fun afterHookedMethod(p0: MethodHookParam) {
                try {
                    if (secInited) return
                    val ctx: Context
                    val clz = p0.thisObject.javaClass.classLoader!!
                        .loadClass("com.tencent.common.app.BaseApplicationImpl")
                    val f: Field? = hasField(clz, "sApplication")
                    ctx =
                        if (f == null) getStaticObject(clz, "a", clz) as Context else f[null] as Context
                    val classLoader2 =
                        ctx.classLoader ?: throw AssertionError("ERROR: classLoader == null")
                    if ("true" == System.getProperty(QS_FULL_TAG)) {
                        logi("Error: It seems that QScript reloaded.")
                    }
                    System.setProperty(QS_FULL_TAG, "true")
                    Initiator.init(classLoader2)
                    try {
                        Natives.load(ctx)
                    } catch (e3: Throwable) {
                        log(e3)
                    }
                    if (getBuildTimestamp() < 0) return
                    MainHook.getInstance().performHook(ctx, p0.thisObject)
                    secInited = true
                    deleteDirIfNecessary(ctx)
                } catch (e: Throwable) {
                    log(e)
                    throw e
                }
            }
        }
        val loadDex: Class<*> = classLoader.loadClass("com.tencent.mobileqq.startup.step.LoadDex")
        val ms = loadDex.declaredMethods
        var m: Method? = null
        ms.forEach {
            if (it.returnType == Boolean::class.java && it.parameterTypes.isEmpty()) {
                m = it
                return@forEach
            }
        }
        XposedBridge.hookMethod(m,startup)
        firstInited = true
    }

    companion object {
        fun deleteDirIfNecessary(ctx: Context) {
            try {
                if (Build.VERSION.SDK_INT >= 24) {
                    deleteFile(File(ctx.dataDir, "app_qqprotect"))
                }
                if (File(ctx.filesDir, "qn_disable_hot_patch").exists()) {
                    deleteFile(ctx.getFileStreamPath("hotpatch"))
                }
            } catch (e: Throwable) {
                log(e)
            }
        }

        private fun deleteFile(file: File): Boolean {
            if (!file.exists()) {
                return false
            }
            if (file.isFile) {
                file.delete()
            } else if (file.isDirectory) {
                val listFiles = file.listFiles()
                if (listFiles != null) {
                    for (deleteFile in listFiles) {
                        deleteFile(deleteFile)
                    }
                }
                file.delete()
            }
            return !file.exists()
        }
    }
}