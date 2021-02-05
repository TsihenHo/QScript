/* QScript - An Xposed module to run scripts on QQ
 * Copyright (C) 2021-20222 chinese.he.amber@gmail.com
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

import android.content.Context
import android.os.Bundle
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.tsihen.qscript.hook.AbsDelayableHook
import me.tsihen.qscript.hook.JumpActivityHook
import me.tsihen.qscript.util.*
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
            JumpActivityHook.loadDex()
            initDebugMode()
            Natives.load(ctx, true)
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