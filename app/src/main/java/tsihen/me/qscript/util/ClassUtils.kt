@file:JvmName("ClassUtils")
package tsihen.me.qscript.util

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*

fun Any.callVisualMethod(
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

fun Class<*>.callStaticMethod(
    methodName: String,
    vararg argsTypesAndReturnType: Any?
):Any? {
    var clazz: Class<*> = this
    val argc: Int = argsTypesAndReturnType.size / 2
    val argt: Array<Class<*>?> = arrayOfNulls(argc)
    val argv = arrayOfNulls<Any>(argc)
    var returnType: Class<*>? = null
    if (argc * 2 + 1 == argsTypesAndReturnType.size)
        returnType = argsTypesAndReturnType[argsTypesAndReturnType.size - 1] as Class<*>
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
    if (method == null) throw NoSuchMethodException(methodName + paramsTypesToString(*argt) + " in " + this.name)
    method.isAccessible = true
    return method.invoke(null, *argv)
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

@JvmOverloads
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