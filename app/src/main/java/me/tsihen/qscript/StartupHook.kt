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

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import me.tsihen.qscript.util.PACKAGE_NAME_QQ
import me.tsihen.qscript.util.PACKAGE_NAME_SELF
import me.tsihen.qscript.util.PACKAGE_NAME_TIM
import me.tsihen.qscript.util.QS_VERSION_NAME

class StartupHook : IXposedHookLoadPackage {
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
        MainHook.getInstance().doInit(p0.classLoader)
    }
}