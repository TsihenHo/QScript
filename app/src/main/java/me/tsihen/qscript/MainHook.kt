/* QScript - An Xposed module to run scripts on QQ
 * Copyright (C) 2021-2022 chinese.he.amber@gmail.com
 * https://github.com/GoldenHuaji/QScript
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package me.tsihen.qscript

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import me.tsihen.qscript.config.ConfigManager
import me.tsihen.qscript.hook.AbsDelayableHook
import me.tsihen.qscript.hook.JumpActivityHook
import me.tsihen.qscript.util.*
import java.io.File
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
        // 如果已经初始化，直接跳过
        if (firstInited) return
        try {
            // 初始化 Initiator，方便以后使用
            Initiator.init(classLoader)
            val splashActivity: Class<*> =
                classLoader.loadClass("com.tencent.mobileqq.activity.SplashActivity")!!
            var m: Method? = null
            var clazz = splashActivity
            try {
                do {
                    try {
                        m = clazz.getDeclaredMethod("onCreate", Bundle::class.java)
                    } catch (ignored: NoSuchMethodException) {
                    }
                    clazz = clazz.superclass!!
                } while (m == null && clazz != Any::class.java)
            } catch (e: Exception) {
                log(e)
            }
            // m 是 hook 点，这个方法通常会在不同的进程里调用多次
            XposedBridge.hookMethod(
                m,
                object : XC_MethodHook(51) {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        try {
                            // Activity 权限更大
                            val ctx = param.thisObject as Activity
                            // 完成初始化就退出
                            if (secInited) return
                            if (System.getProperty(QS_FULL_TAG) == "true") {
                                loge("Error: QScript reload.Stop it.")
                                return
                            }
                            System.setProperty(QS_FULL_TAG, "true")

                            // 开始 hook
                            getInstance().performHook(ctx)
                        } catch (e: Exception) {
                            log(e)
                        }
                        secInited = true
                    }
                })

            // 完成
            firstInited = true
        } catch (e: Exception) {
            log(e)
            firstInited = false
        }
    }

    fun performHook(ctx: Context) {
        var failed = false
        try {
            if (thirdInited) return
            // 尽早替换 classLoader
            JumpActivityHook.loadDex(ctx)

            // 初始化 Native
            Natives.load(ctx, true)

            // 向下兼容
            val finalRun: (it: Context) -> Unit = { it ->
                // 检查调试模式
                initDebugMode()

                // 保存 QQAppInterface，方便以后调用
                XposedHelpers.findAndHookMethod(
                    Initiator.load(".app.QQAppInterface"),
                    "onCreate",
                    Bundle::class.java,
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            super.afterHookedMethod(param)
                            qqAppInterface = param.thisObject
                        }
                    })

                // 初始化每个 absHook
                AbsDelayableHook.queryDelayableHooks().forEach { if (!it.init()) failed = true }

                // 这个玩意是用来解决一些无法启动的 BUG，不知道有没有作用
                var m: Method? = null
                it.classLoader.loadClass("com.google.android.material.internal.ThemeEnforcement").declaredMethods.forEach {
                    if (it.name == "checkTheme") m = it
                }
                XposedBridge.hookMethod(m, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam?) {
                        super.beforeHookedMethod(param)
                        logd("hook check theme")
                        param?.result = null
                    }
                })

                // 测试块，用于检测是否成功替换 ClassLoader
                try {
                    // 检查 androidx
                    Initiator.load("androidx.lifecycle.ProcessLifecycleOwnerInitializer")
                } catch (e: Exception) {
                    log(e)
                    Toasts.error(it, "错误：测试失败，请带上QQ版本反馈")
                }
            }
            val oldData =
                File(getApplicationNonNull().filesDir.absolutePath + "/qscript_config.json")
            val oldScripsDir = File(ctx.filesDir.absolutePath.toString() + "/qs_scripts/")
            val newDataDir = ConfigManager.getFileDir(ctx)
            if (oldScripsDir.exists() || oldData.exists()) {
                val d = AlertDialog.Builder(ctx)
                    .setMessage("检测到旧版本的配置文件，是否转为该版本可用的配置文件？\n如果您选择“我不要”，那么代表着您曾经的配置文件都会被")
                    .setPositiveButton("好的") { d, _ ->
                        d.dismiss()
                        newDataDir.mkdirs()
                        oldData.copyTo(File(newDataDir.absolutePath + "/qscript_config.json"), true)
                        if (oldScripsDir.exists()) {
                            oldScripsDir.copyRecursively(File(newDataDir.absolutePath + "/qs_scripts"))
                            oldScripsDir.deleteRecursively()
                        }
                        oldData.delete()
                        finalRun(getApplicationNonNull())
                    }
                    .setNegativeButton("我不要") { _, _ ->
                        finalRun(getApplicationNonNull())
                    }
                    .setCancelable(false)
                    .create()
                d.show()
            } else {
                finalRun(getApplicationNonNull())
            }
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