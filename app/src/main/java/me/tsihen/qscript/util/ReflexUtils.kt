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
@file:JvmName("ReflexUtils")

package me.tsihen.qscript.util

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*

private fun findMethod(
    methodName: String,
    clz: Class<*>,
    argsTypes: Array<Class<*>>,
    returnType: Class<*>? = null
): Method {
    var clazz = clz
    if (clz.superclass != null) {
        do {
            clazz.declaredMethods.forEach {
                if (!it.parameterTypes.contentEquals(argsTypes)) return@forEach
                if (it.returnType != returnType && returnType != null) return@forEach
                if (it.name != methodName) return@forEach
                it.isAccessible = true
                return it
            }
            clazz = clazz.superclass!!
        } while (clazz != Any::class.java)
        throw NoSuchMethodException("找不到方法$methodName${argsTypes.contentToString()}在${clz.simpleName}")
    }
    throw IllegalArgumentException("不支持基本数据类型")
}

/**
 * 调用某个非静态方法
 *
 * 如果您不想输入参数类型，请 `XposedHelpers.callMethod(thisObject, methodName, args)`
 *
 * @param methodName 方法名
 * @param argsTypesAndReturnType 方法参数+参数类型(+方法返回值)
 * @return 被调用的方法的返回值
 * @throws NoSuchMethodException 找不到方法
 */
fun Any.callVisualMethod(
    methodName: String,
    vararg argsTypesAndReturnType: Any?
): Any? {
    val clazz: Class<*> = this.javaClass
    var returnType =
        if (argsTypesAndReturnType.size % 2 == 1) argsTypesAndReturnType.last() else null
    val args = argsTypesAndReturnType.slice(0 until (argsTypesAndReturnType.size / 2))
    val types =
        argsTypesAndReturnType.slice((argsTypesAndReturnType.size / 2) until argsTypesAndReturnType.size)
            .toMutableList()
    if (returnType != null) types.removeAt(types.size - 1)
    val t = mutableListOf<Class<*>>()
    types.forEach { e ->
        if (e !is Class<*>) throw IllegalArgumentException("参数类型不是 Class<?>")
        t.add(e)
    }
    try {
        returnType = returnType as? Class<*>?
    } catch (e: Exception) {
        throw IllegalArgumentException("返回值类型不是 Class<?>")
    }
    return findMethod(methodName, clazz, t.toTypedArray(), returnType).invoke(
        this,
        *args.toTypedArray()
    )
}

fun Class<*>.callStaticMethod(
    methodName: String,
    vararg argsTypesAndReturnType: Any?
): Any? {
    val clazz: Class<*> = this
    var returnType =
        if (argsTypesAndReturnType.size % 2 == 1) argsTypesAndReturnType.last() else null
    val args = argsTypesAndReturnType.slice(0 until (argsTypesAndReturnType.size / 2))
    val types =
        argsTypesAndReturnType.slice((argsTypesAndReturnType.size / 2) until argsTypesAndReturnType.size)
            .toMutableList()
    if (returnType != null) types.removeAt(types.size - 1)
    val t = mutableListOf<Class<*>>()
    types.forEach { e ->
        if (e !is Class<*>) throw IllegalArgumentException("参数类型不是 Class<?>")
        t.add(e)
    }
    try {
        returnType = returnType as? Class<*>?
    } catch (e: Exception) {
        throw IllegalArgumentException("返回值类型不是 Class<?>")
    }
    return findMethod(methodName, clazz, t.toTypedArray(), returnType).invoke(
        null,
        *args.toTypedArray()
    )
}

@Suppress("UNCHECKED_CAST")
fun <T> getStaticObject(
    clazz: Class<*>,
    name: String,
    type: Class<T>? = null
): Any? {
    try {
        val f = findField(clazz, type, name)
            ?: throw NoSuchFieldException("Cannot find the field.Class is ${clazz.name}, name is $name, type is $type")
        f.isAccessible = true
        return f[null] as? T?
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
        val f: Field = findField(clazz, type, name)
            ?: throw NoSuchFieldException("Cannot find the field.Class is ${clazz.name}, name is $name, type is $type")
        f.isAccessible = true
        return f[obj] as? T?
    } catch (e: Exception) {
        log(e)
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
        val f: Field = findField(clazz, type, name)
            ?: throw NoSuchFieldException("Cannot find the field.Class is ${clazz.name}, name is $name, type is $type")
        f.isAccessible = true
        f[obj] = value
    } catch (e: Exception) {
        log(e)
    }
}

// From QNotified
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

// From QNotified
/**
 * @param argsAndTypes 参数+参数类型，如：`val i = newInstance(Intent::class.java, thisObject, MainActivity::class.java, Context::class.java, Class<*>::class.java)`
 */
fun newInstance(
    clazz: Class<*>,
    vararg argsAndTypes: Any?
): Any {
    val argc: Int = argsAndTypes.size / 2
    val argt: Array<Class<*>?> = arrayOfNulls(argc)
    val argv = arrayOfNulls<Any>(argc)
    var i = 0
    while (i < argc) {
        argt[i] = argsAndTypes[argc + i] as Class<*>
        argv[i] = argsAndTypes[i]
        i++
    }
    val m: Constructor<*> = clazz.getDeclaredConstructor(*argt)
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
    obj: Any,
    name: String,
    type: Class<*>? = null
) = findField(if (obj is Class<*>) obj else obj.javaClass, type, name)


fun findField(
    clz: Class<*>,
    type: Class<*>?,
    name: String
): Field? {
    var clazz = clz
    if (clz.superclass != null) {
        do {
            clazz.declaredFields.forEach {
                if (it.name != name) return@forEach
                if (it.type == type || type == null) return it
            }
            clazz = clazz.superclass!!
        } while (clazz.superclass != Any::class.java)
    }
    return null
}