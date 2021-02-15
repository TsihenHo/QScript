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
package me.tsihen.qscript.util
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
object Initiator {
    @JvmStatic
    private var sHostClassloader: ClassLoader? = null

    @JvmStatic
    fun init(rtLoader: ClassLoader) {
        try {
            sHostClassloader = rtLoader
        } catch (e: Exception) {
            log(e)
        }
    }

    @JvmStatic
    @JvmOverloads
    @FromQNotified
    fun load(classPath: String, classLoader: ClassLoader? = null): Class<*>? {
        sHostClassloader = classLoader ?: sHostClassloader
        if (classPath.isEmpty() || sHostClassloader == null) {
            logw("Initiator : Didn't init.")
            return null
        }
        var className = classPath.replace('/', '.')
        if (className.endsWith(";")) {
            className =
                if (className[0] == 'L')
                    className.substring(1, className.length - 1)
                else
                    className.substring(0, className.length - 1)
        }
        if (className.startsWith('.')) {
            className = PACKAGE_NAME_QQ + className
        }
        return try {
            sHostClassloader!!.loadClass(className)
        } catch (e: Throwable) {
            log(e)
            null
        }
    }

    @JvmStatic
    fun getHostClassLoader() = sHostClassloader
}