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
@file:JvmName("Utils")
@file:Suppress("DEPRECATION")

package me.tsihen.qscript.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Process.myPid
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.robv.android.xposed.XposedBridge
import me.tsihen.qscript.config.ConfigManager
import java.io.*
import java.lang.reflect.Field
import java.text.DateFormat
import java.text.DateFormat.getDateTimeInstance
import java.util.*
import kotlin.math.expm1
import kotlin.math.sqrt

var DEBUG_MODE: Boolean = false
private var mHandler: Handler? = null

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

fun initDebugMode() {
    val mgr = ConfigManager.tryGetDefaultConfig()
    DEBUG_MODE = mgr?.getOrDefault("debug_mode", false) ?: false
}

// Log
fun appendToFile(fileName: String?, content: String?) {
    var writer: FileWriter? = null
    try {
        writer = FileWriter(fileName, true)
        writer.write(content)
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            writer?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

fun log(e: Throwable) {
    loge(Log.getStackTraceString(e))
}

fun loge(msg: String) {
    try {
        XposedBridge.log(msg)
    } catch (e: NoClassDefFoundError) {
    }
    Log.e(QS_LOG_TAG, msg)
    try {
        val path = Environment.getExternalStorageDirectory().absolutePath + "/me.tsihen.qscript.log"
        val f = File(path)
        try {
            if (!f.exists()) f.createNewFile()
            appendToFile(
                path,
                "[" +
                        getDateTimeInstance(
                            DateFormat.MEDIUM,
                            DateFormat.MEDIUM
                        ).format(Date(System.currentTimeMillis())) + " " + myPid() + "]E/ " + msg + "\n"
            )
        } catch (e: IOException) {
        }
    } catch (e: Exception) {
    }
}

fun logd(msg: String) {
    if (!DEBUG_MODE) {
        return
    }
    try {
        XposedBridge.log(msg)
    } catch (e: NoClassDefFoundError) {
    }
    Log.d(QS_LOG_TAG, msg)
    try {
        val path = Environment.getExternalStorageDirectory().absolutePath + "/me.tsihen.qscript.log"
        val f = File(path)
        try {
            if (!f.exists()) f.createNewFile()
            appendToFile(
                path,
                "[" +
                        getDateTimeInstance(
                            DateFormat.MEDIUM,
                            DateFormat.MEDIUM
                        ).format(Date(System.currentTimeMillis())) + " " + myPid() + "]D/ " + msg + "\n"
            )
        } catch (e: IOException) {
        }
    } catch (e: Exception) {
    }
}

fun logi(msg: String) {
    try {
        XposedBridge.log(msg)
    } catch (e: NoClassDefFoundError) {
    }
    Log.i(QS_LOG_TAG, msg)
    try {
        val path = Environment.getExternalStorageDirectory().absolutePath + "/me.tsihen.qscript.log"
        val f = File(path)
        try {
            if (!f.exists()) f.createNewFile()
            appendToFile(
                path,
                "[" +
                        getDateTimeInstance(
                            DateFormat.MEDIUM,
                            DateFormat.MEDIUM
                        ).format(Date(System.currentTimeMillis())) + " " + myPid() + "]I/ " + msg + "\n"
            )
        } catch (e: IOException) {
        }
    } catch (e: Exception) {
    }
}

fun logw(msg: String) {
    try {
        XposedBridge.log(msg)
    } catch (e: NoClassDefFoundError) {
    }
    Log.w(QS_LOG_TAG, msg)
    try {
        val path = Environment.getExternalStorageDirectory().absolutePath + "/me.tsihen.qscript.log"
        val f = File(path)
        try {
            if (!f.exists()) f.createNewFile()
            appendToFile(
                path,
                "[" +
                        getDateTimeInstance(
                            DateFormat.MEDIUM,
                            DateFormat.MEDIUM
                        ).format(Date(System.currentTimeMillis())) + " " + myPid() + "]W/ " + msg + "\n"
            )
        } catch (e: IOException) {
        }
    } catch (e: Exception) {
    }
}

fun logv(msg: String) {
    if (DEBUG_MODE) {
        try {
            XposedBridge.log(msg)
        } catch (ignored: NoClassDefFoundError) {
        }
    }
    Log.v(QS_LOG_TAG, msg)
}

fun getActiveModuleVersion(): String? {
    sqrt(1.0)
    expm1(0.001)
    Math.random()
    return null
}

@SuppressLint("PrivateApi")
fun getCurrentActivity(): Activity? {
    try {
        val activityThreadClass =
            Class.forName("android.app.ActivityThread")
        val activityThread =
            activityThreadClass.getMethod("currentActivityThread").invoke(null)
        val activitiesField =
            activityThreadClass.getDeclaredField("mActivities")
        activitiesField.isAccessible = true
        val activities =
            activitiesField[activityThread] as Map<*, *>
        activities.values.forEach {
            val activityRecordClass: Class<*> = it!!.javaClass
            val pausedField =
                activityRecordClass.getDeclaredField("paused")
            pausedField.isAccessible = true
            if (!pausedField.getBoolean(it)) {
                val activityField =
                    activityRecordClass.getDeclaredField("activity")
                activityField.isAccessible = true
                return activityField[it] as Activity
            }
        }
    } catch (e: java.lang.Exception) {
        log(e)
    }
    return null
}

private external fun ntGetBuildTimestamp(): Long
fun getBuildTimestamp(context: Context? = null): Long {
    var ctx: Context? = context
    if (ctx == null) {
        try {
            ctx = getQQApplication()
        } catch (ignored: Throwable) {
        }
        if (ctx == null) {
            ctx = getCurrentActivity()
        }
    }
    if (ctx == null) {
        logd("Context is null.")
    }
    return try {
        Natives.load(ctx!!)
        ntGetBuildTimestamp()
    } catch (throwable: Throwable) {
        log(throwable)
        -3
    }
}

fun paramsTypesToString(vararg c: Class<*>?): String {
    if (c.isEmpty()) return "()"
    val sb = StringBuilder("(")
    for (i in c.indices) {
        sb.append(if (c[i] == null) "[null]" else c[i]!!.name)
        if (i != c.size - 1) {
            sb.append(",")
        }
    }
    sb.append(")")
    return sb.toString()
}

fun runOnUiThread(r: Runnable) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        r.run()
    } else {
        if (mHandler == null) {
            mHandler = Handler(Looper.getMainLooper())
        }
        mHandler!!.post(r)
    }
}

@Throws(java.lang.Exception::class)
fun copy(s: File, f: File) {
    if (!s.exists()) throw FileNotFoundException("源文件不存在")
    if (!f.exists()) f.createNewFile()
    val fr = FileReader(s)
    val fw = FileWriter(f)
    val buff = CharArray(1024)
    var len = 0
    while (len != -1) {
        fw.write(buff, 0, len)
        len = fr.read(buff)
    }
    fw.close()
    fr.close()
}

inline fun <reified T : Activity> Context.startActivity() {
    val intent = Intent(this, Initiator.load(".activity.JumpActivity", T::class.java.classLoader))
    intent.putExtra(JUMP_ACTION_CMD, JUMP_ACTION_START_ACTIVITY)
        .putExtra(JUMP_ACTION_TARGET, T::class.java.name)

    this.startActivity(intent)
}

inline fun <reified T> Gson.fromJson(json: String): T? = this.fromJson<T>(
    json,
    object : TypeToken<T>() {}.type
)