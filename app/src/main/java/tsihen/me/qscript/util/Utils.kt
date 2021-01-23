@file:JvmName("Utils")

package tsihen.me.qscript.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import de.robv.android.xposed.XposedBridge
import java.lang.reflect.Field
import kotlin.math.expm1
import kotlin.math.sqrt

// Log
fun log(e: Throwable) {
    val msg = Log.getStackTraceString(e)
    Log.e(QS_LOG_TAG, msg)
    try {
        XposedBridge.log(e)
    } catch (e: NoClassDefFoundError) {
        Log.e("Xposed", msg)
        Log.e("EdXposed-Bridge", msg)
    }
}

fun loge(msg: String) {
    try {
        XposedBridge.log(msg)
    } catch (e: NoClassDefFoundError) {
        Log.e("Xposed", msg)
        Log.e("EdXposed-Bridge", msg)
    }
}

fun logd(msg: String) {
    if (!DEBUG_MODE) {
        return
    }
    try {
        XposedBridge.log(msg)
    } catch (e: NoClassDefFoundError) {
        Log.d("Xposed", msg)
        Log.d("EdXposed-Bridge", msg)
    }
}

fun logi(msg: String) {
    try {
        XposedBridge.log(msg)
    } catch (e: NoClassDefFoundError) {
        Log.i("Xposed", msg)
        Log.i("EdXposed-Bridge", msg)
    }
}

fun logw(msg: String) {
    try {
        XposedBridge.log(msg)
    } catch (e: NoClassDefFoundError) {
        Log.w("Xposed", msg)
        Log.w("EdXposed-Bridge", msg)
    }
}

fun getActiveModuleVersion(): String? {
    sqrt(1.0)
    expm1(0.001)
    Math.random()
    return null
}


fun hasField(
    obj: Any?,
    name: String,
    type: Class<*>? = null
): Field? {
    if (obj == null) throw NullPointerException("obj/class == null")
    val clazz: Class<*> = if (obj is Class<*>) obj else obj.javaClass
    return findField(clazz, type, name)
}

fun findField(
    clazz: Class<*>?,
    type: Class<*>?,
    name: String
): Field? {
    if (clazz != null && name.isNotEmpty()) {
        var clz: Class<*> = clazz
        do {
            clz.declaredFields.forEach {
                if ((type == null || it.type == type) && it.name == name) {
                    it.isAccessible = true
                    return it
                }
            }
        } while (clz.superclass.also { clz = it!! } != null)
    }
    return null
}

fun getStaticObject(
    clazz: Class<*>,
    name: String,
    type: Class<*>? = null
):Any? {
    try {
        val f = findField(clazz, type, name) ?: throw NullPointerException("Cannot find the field.Class is ${clazz.name}, name is $name, type is $type")
        f.isAccessible = true
        return f[null]
    } catch (e: Exception) {
        log(e)
    }
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

fun getApplication(): Application {
    TODO("获取QQ APP")
}


private external fun ntGetBuildTimestamp(): Long
fun getBuildTimestamp(context: Context? = null): Long {
    var ctx: Context? = context
    if (ctx == null) {
        try {
            ctx = getApplication()
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
