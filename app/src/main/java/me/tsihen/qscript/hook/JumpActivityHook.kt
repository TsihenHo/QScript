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
package me.tsihen.qscript.hook

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Instrumentation
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.tsihen.qscript.activity.SettingActivity
import me.tsihen.qscript.util.*
import me.tsihen.qscript.util.Initiator.load
import me.tsihen.qscript.util.JavaUtil.replaceClassLoader
import java.lang.reflect.Method

/**
 * 这个类写的太糟糕了，我想我再也不会打开这个类了
 */
class JumpActivityHook : AbsDelayableHook() {
    companion object {
        private val self = JumpActivityHook()
        fun get(): JumpActivityHook = self

        fun loadDex() {
            replaceClassLoader(Initiator::class.java.classLoader, Initiator.getHostClassLoader())
        }
    }

    override fun init(): Boolean {
        val jumpActivity = load(".activity.JumpActivity")
        if (jumpActivity == null) {
            loge("JumpActivity not found.")
            return false
        }
        val doOnCreate: Method = jumpActivity.getDeclaredMethod("doOnCreate", Bundle::class.java)
        XposedBridge.hookMethod(doOnCreate, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val thiz = param.thisObject as Activity
                initForActivity(thiz)
                val intent = thiz.intent
                val cmd: String? = intent.getStringExtra(JUMP_ACTION_CMD)
                if (intent == null || cmd == null) {
                    return
                }
                if (JUMP_ACTION_SETTING_ACTIVITY == cmd) {
                    val realIntent = Intent(intent)
                    realIntent.putExtra(JUMP_ACTION_CMD, JUMP_ACTION_SETTING_ACTIVITY)
                    realIntent.component = ComponentName(thiz, SettingActivity::class.java)
                    thiz.startActivity(realIntent)
                } else if (JUMP_ACTION_START_ACTIVITY == cmd) {
                    val target = intent.getStringExtra(JUMP_ACTION_TARGET) ?: ""
                    logi("JumpActivity : Target = $target")
                    if (target.isNotEmpty()) {
                        try {
                            val activityClass = Class.forName(target)
                            val realIntent = Intent(intent)
                            realIntent.putExtra(JUMP_ACTION_CMD, JUMP_ACTION_SETTING_ACTIVITY)
                            realIntent.component = ComponentName(thiz, activityClass)
                            thiz.startActivity(realIntent)
                        } catch (e: Exception) {
                            logi("Unable to start Activity: $e")
                        }
                    }
                }
            }
        })
        return true
    }

    /**
     * 启动 Activity
     */
    @SuppressLint("PrivateApi")
    fun initForActivity(ctx: Activity) {
        JavaUtil.initForStubActivity(ctx)
        JavaUtil.injectModuleResources(ctx.resources)
        val instrumentation =
            MyInstrumentation(getObject(ctx, "mInstrumentation", Instrumentation::class.java)!!)
        setObject(ctx, "mInstrumentation", instrumentation)
    }

    override fun getEnabled(): Boolean = true

    override fun setEnabled(z: Boolean) {
        throw RuntimeException("You cannot change it!")
    }
}