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
@file:JvmName("QQFields")

package me.tsihen.qscript.util

import android.app.Application
import androidx.annotation.NonNull
import java.lang.reflect.Field

var qqAppInterface: Any? = null
    @JvmName("setQQAppInterface")
    set
    @JvmName("getQQAppInterface")
    @NonNull
    get() {
        if (field == null) return getAppRuntime()
        return field
    }

fun getQQApplication(): Application? {
    val f: Field?
    return try {
        val clz: Class<*> = Initiator.load("com/tencent/common/app/BaseApplicationImpl")!!
        f = hasField(clz, "sApplication")
        if (f == null) getStaticObject(
            clz,
            "a",
            clz
        ) as? Application? else f[null] as? Application?
    } catch (e: java.lang.Exception) {
        log(e)
        throw (java.lang.RuntimeException("FATAL: Utils.getApplication() failure!")
            .initCause(e) as java.lang.RuntimeException)
    }
}

fun getApplicationNonNull(): Application {
    return getQQApplication() ?: throw NullPointerException("QQApplication is null.")
}

fun getAppRuntime(): Any {
    val ctx = getApplicationNonNull()
    return ctx.callVisualMethod("getRuntime")
        ?: throw java.lang.NullPointerException("Utils : GetAppRuntime : Runtime is null.")
}

fun getLongAccountUin(): Long = getAppRuntime().callVisualMethod("getLongAccountUin") as Long