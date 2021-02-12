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
package me.tsihen.qscript.activity

import android.app.Activity
import android.content.ComponentName
import android.os.Bundle
import me.tsihen.qscript.R
import me.tsihen.qscript.util.*

open class BaseActivity : Activity() {
    override fun getComponentName(): ComponentName =
        ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.SplashActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDebugMode()
        setTheme(R.style.Theme_MyTheme)
        Initiator.init(this.classLoader)
        if (intent.getStringExtra(JUMP_ACTION_CMD) != JUMP_ACTION_CHECK_ACTIVITY) {
            logd("无效启动")
            finish()
        }
    }
}