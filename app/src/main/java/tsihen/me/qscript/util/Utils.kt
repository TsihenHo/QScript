@file:JvmName("Utils")

package tsihen.me.qscript.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import de.robv.android.xposed.XposedBridge
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.math.expm1
import kotlin.math.sqrt

var qqApplication: Application? = null
    @JvmName("getApplication")
    get() {
        if (field == null) {
            logw("The qqApplication is null.")
        }
        return field
    }
    @JvmName("setApplication")
    set(value) {
        if (value == null) {
            logw("The qqApplication is set to null.")
        }
        field = value
    }

fun getApplicationNonNull(): Application {
    return qqApplication ?: throw NullPointerException("QQApplication is null.")
}

// Log
fun log(e: Throwable) {
    val msg = Log.getStackTraceString(e)
    Log.e(QS_LOG_TAG, msg)
    try {
        XposedBridge.log(e)
    } catch (e: NoClassDefFoundError) {
        Log.e(QS_LOG_TAG, msg)
    }
}

fun loge(msg: String) {
    try {
        XposedBridge.log(msg)
    } catch (e: NoClassDefFoundError) {
    }
    Log.e(QS_LOG_TAG, msg)
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
}

fun logi(msg: String) {
    try {
        XposedBridge.log(msg)
    } catch (e: NoClassDefFoundError) {
    }
    Log.i(QS_LOG_TAG, msg)
}

fun logw(msg: String) {
    try {
        XposedBridge.log(msg)
    } catch (e: NoClassDefFoundError) {
    }
    Log.w(QS_LOG_TAG, msg)
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
): Any? {
    try {
        val f = findField(clazz, type, name)
            ?: throw NullPointerException("Cannot find the field.Class is ${clazz.name}, name is $name, type is $type")
        f.isAccessible = true
        return f[null]
    } catch (e: Exception) {
        log(e)
    }
    return null
}

@Suppress("UNCHECKED_CAST")
fun <T> getObject(
    obj: Any,
    name: String,
    type: Class<T>? = null
): T? {
    val clazz: Class<*> = obj.javaClass
    try {
        val f: Field = findField(clazz, type, name)!!
        f.isAccessible = true
        return f[obj] as T
    } catch (e: Exception) {
    }
    return null
}

fun setObject(
    obj: Any,
    name: String,
    value: Any?,
    type: Class<*>? = null
) {
    val clazz: Class<*> = obj.javaClass
    try {
        val f: Field = findField(clazz, type, name)!!
        f.isAccessible = true
        f[obj] = value
    } catch (e: Exception) {
        log(e)
    }
}

fun setStaticObject(
    clazz: Class<*>,
    name: String,
    value: Any?,
    type: Class<*>? = null
) {
    try {
        val f: Field = findField(clazz, type, name)!!
        f.isAccessible = true
        f[null] = value
    } catch (e: Exception) {
        log(e)
    }
}

/**
 * @param argsAndTypes 参数+参数类型，如：
 * <code>
 *     val i = newInstance(Intent::class.java, thisObject, MainActivity::class.java, Context::class.java, Class<*>::class.java)
 * </code>
 */
fun newInstance(
    clazz: Class<*>,
    vararg argsAndTypes: Any?
): Any {
    val argc: Int = argsAndTypes.size / 2
    val argt: Array<Class<*>?> = arrayOfNulls(argc)
    val argv = arrayOfNulls<Any>(argc)
    val m: Constructor<*>
    var i = 0
    while (i < argc) {
        argt[i] = argsAndTypes[argc + i] as Class<*>
        argv[i] = argsAndTypes[i]
        i++
    }
    m = clazz.getDeclaredConstructor(*argt)
    m.isAccessible = true
    return try {
        m.newInstance(*argv)
    } catch (e: IllegalAccessException) {
        log(e)
        throw RuntimeException(e)
    }
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
            ctx = qqApplication
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

fun Any.invokeVirtual(
    methodName: String,
    vararg argsTypesAndReturnType: Any?
): Any? {
    var clazz: Class<*> = this.javaClass
    val argc: Int = argsTypesAndReturnType.size / 2
    val argt: Array<Class<*>?> = arrayOfNulls(argc)
    val argv = arrayOfNulls<Any>(argc)
    var returnType: Class<*>? = null
    if (argc * 2 + 1 == argsTypesAndReturnType.size)
        returnType = argsTypesAndReturnType.get(argsTypesAndReturnType.size - 1) as Class<*>
    var i: Int
    var ii: Int
    var m: Array<Method>
    var method: Method? = null
    var _argt: Array<Class<*>>
    i = 0
    while (i < argc) {
        argt[i] = argsTypesAndReturnType[argc + i] as Class<*>
        argv[i] = argsTypesAndReturnType[i]
        i++
    }
    loop_main@ do {
        m = clazz.declaredMethods
        i = 0
        loop@ while (i < m.size) {
            if (m[i].name == methodName) {
                _argt = m[i].parameterTypes
                if (_argt.size == argt.size) {
                    ii = 0
                    while (ii < argt.size) {
                        if (argt[ii] != _argt[ii]) {
                            i++
                            continue@loop
                        }
                        ii++
                    }
                    if (returnType != null && returnType != m[i].returnType) {
                        i++
                        continue
                    }
                    method = m[i]
                    break@loop_main
                }
            }
            i++
        }
    } while (Any::class.java != clazz.superclass.also { clazz = it!! })
    if (method == null) throw NoSuchMethodException(methodName + paramsTypesToString(*argt) + " in " + this.javaClass.name)
    method.isAccessible = true
    return method.invoke(this, *argv)
}

inline fun <reified T : Activity> Context.startActivity() {
    val intent = Intent(this, Initiator.load(".activity.JumpActivity", T::class.java.classLoader))
    intent.putExtra(JUMP_ACTION_CMD, JUMP_ACTION_START_ACTIVITY)
        .putExtra(JUMP_ACTION_TARGET, T::class.java.name)

    this.startActivity(intent)
}