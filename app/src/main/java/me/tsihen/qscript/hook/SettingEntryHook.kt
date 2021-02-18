/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
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

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import me.tsihen.qscript.activity.SettingActivity
import me.tsihen.qscript.config.ConfigManager
import me.tsihen.qscript.util.*
class SettingEntryHook : AbsDelayableHook() {
    companion object {
        private val self = SettingEntryHook()
        fun get(): SettingEntryHook = self
    }

    private var inited: Boolean = false

    @FromQNotified
    override fun init(): Boolean {
        if (inited) {
            return true
        }
        try {
            val qqSettingActivity = Initiator.load(".activity.QQSettingSettingActivity")
            XposedHelpers.findAndHookMethod(
                qqSettingActivity,
                "doOnCreate",
                Bundle::class.java,
                object : XC_MethodHook(52) {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        var itemClass: Class<*>? = null
                        var itemRef: View?
                        itemRef = getObject(
                            param.thisObject,
                            "a",
                            Initiator.load("com/tencent/mobileqq/widget/FormSimpleItem")
                        ) as? View?
                        if (itemRef == null
                            && Initiator.load("com/tencent/mobileqq/widget/FormCommonSingleLineItem")
                                .also { itemClass = it } != null
                        ) itemRef = getObject(param.thisObject, "a", itemClass) as View
                        val item: View = newInstance(
                            itemRef!!.javaClass,
                            param.thisObject,
                            Context::class.java
                        ) as View
                        item.callVirtualMethod("setLeftText", "QScript", CharSequence::class.java)
                        item.callVirtualMethod("setBgType", 2, Int::class.java)
                        item.callVirtualMethod(
                            "setRightText",
                            QS_VERSION_NAME,
                            CharSequence::class.java
                        )
                        item.setOnLongClickListener {
                            AlertDialog.Builder(it.context)
                                .setPositiveButton("打开调试模式") { _, _ ->
                                    ConfigManager.getDefaultConfig()["debug_mode"] = true
                                    debugMode = true
                                    Toasts.info(
                                        param.thisObject as Activity,
                                        "已开启调试模式",
                                        Toasts.LENGTH_SHORT
                                    )
                                }
                                .setNeutralButton("清除日志缓存") { _, _ ->
                                    ConfigManager.getDefaultConfig()["error_message"] = ""
                                    ConfigManager.getDefaultConfig()["has_error"] = false
                                    Toasts.info(
                                        param.thisObject as Activity,
                                        "已清除",
                                        Toasts.LENGTH_SHORT
                                    )
                                }
                                .show()
                            true
                        }
                        item.setOnClickListener {
                            (param.thisObject as Activity).startActivity<SettingActivity>()
                        }
                        (itemRef.parent as ViewGroup).addView(
                            item,
                            0,
                            ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                        )
                    }
                })
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override fun getEnabled(): Boolean = true

    override fun setEnabled(z: Boolean) {
        throw RuntimeException("You cannot change it!")
    }
}