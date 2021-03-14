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

class DexMethod(methodPath: String) {
    private var clazzName: String
    private var methodName: String
    private var methodParamsName: String
    private var methodReturnTypeName: String

    init {
        if (methodPath.isEmpty()) throw NullPointerException("方法路径不能为空")
        // 示例输入：Lcom/tencent/mobileqq/activity/ChatActivityFacade;->a(Lcom/tencent/mobileqq/app/QQAppInterface;JJ)V
        clazzName = methodPath.substring(1, methodPath.indexOf(";->")).replace('/', '.')
        // clazzName = com.tencent...Facade
        methodName = methodPath.substring(clazzName.length + 4, methodPath.indexOf('('))
        // methodName = a
        methodParamsName =
            methodPath.substring(methodPath.indexOf('(') + 1, methodPath.indexOf(')'))
        // ()之间的部分
        methodReturnTypeName = methodPath.substring(methodPath.indexOf(')') + 1)
        // methodReturnTypeName = V
    }

    fun getMethod(): Method = getMethod(Initiator.getHostClassLoader()!!)

    fun getMethod(loader: ClassLoader): Method {
        var clz = loader.loadClass(clazzName)
        if (clz.superclass != null) {
            do {
                clz.declaredMethods.forEach {
                    if (it.name != methodName) return@forEach
                    if (it.returnType.getSign() != methodReturnTypeName) return@forEach
                    val paramsArray =
                        Array(it.parameterTypes.size) { index -> it.parameterTypes[index].getSign() }
                    if (paramsArray.joinToString("") != methodParamsName) return@forEach
                    return it
                }
                clz = clz.superclass
            } while (clz != Any::class.java)
            throw NoSuchMethodException("找不到方法 $methodName($methodParamsName): $methodReturnTypeName 在 $clazzName")
        }
        throw java.lang.IllegalArgumentException("不支持基本数据类型")
    }
}

class DexField(fieldPath: String) {
    private var clazzName: String
    private var fieldName: String
    private var fieldTypeName: String

    init {
        if (fieldPath.isEmpty()) throw NullPointerException("属性路径不能为空")
        // 示例输入：Lcom/tencent/mobileqq/activity/ChatActivityFacade;->a:Lcom/tencent/mobileqq/app/QQAppInterface;
        clazzName = fieldPath.substring(1, fieldPath.indexOf(";->")).replace('/', '.')
        fieldName = fieldPath.substring(clazzName.length + 4, fieldPath.indexOf(':'))
        fieldTypeName = fieldPath.substring(fieldPath.indexOf(':') + 1)
    }

    fun getField(): Field = getField(Initiator.getHostClassLoader()!!)

    fun getField(loader: ClassLoader): Field {
        var clz = loader.loadClass(clazzName)
        if (clz.superclass != null) {
            do {
                clz.declaredFields.forEach {
                    if (it.name != fieldName) return@forEach
                    if (it.type.getSign() != fieldTypeName) return@forEach
                    return it
                }
                clz = clz.superclass
            } while (clz != Any::class.java)
            throw NoSuchMethodException("找不到属性 $fieldName:$fieldTypeName 在 $clazzName")
        }
        throw java.lang.IllegalArgumentException("不支持基本数据类型")
    }
}

/**
 * 支持正则表达式
 */
fun findMethod(
    methodName: String,
    clz: Class<*>,
    argsTypes: Array<Class<*>>,
    returnType: Class<*>? = null,
): Method {
    var clazz = clz
    val regex = Regex("^$methodName\$")
    if (clz.superclass != null) {
        do {
            clazz.declaredMethods.forEach {
                if (!it.name.matches(regex)) return@forEach
                if (!it.parameterTypes.contentEquals(argsTypes)) return@forEach
                if (it.returnType != returnType && returnType != null) return@forEach
                it.isAccessible = true
                return it
            }
            clazz = clazz.superclass!!
        } while (clazz != Any::class.java)
        throw NoSuchMethodException("找不到方法$methodName${argsTypes.contentToString()}在${clz.simpleName}")
    }
    throw IllegalArgumentException("不支持基本数据类型")
}

fun findMethodBySignWithRegex(sign: String, clazz: Class<*>): Method {
    var clz = clazz
    val regex = Regex("^$sign\$")
    if (clz.superclass != null) {
        do {
            clz.declaredMethods.forEach {
                val itSign = StringBuilder()
                itSign.append(it.name)
                itSign.append('(')
                it.parameterTypes.forEach { type ->
                    itSign.append(type.getSign())
                }
                itSign.append(')')
                itSign.append(it.returnType.getSign())
                if (itSign.toString().matches(regex)) return it
                clz = clazz.superclass!!
            }
        } while (clz != Any::class.java)
        throw NoSuchMethodException("找不到方法 $sign 在 ${clazz.simpleName}")
    }
    throw IllegalArgumentException("不支持基本数据类型")
}

fun findMethodBySign(sign: String, clazz: Class<*>): Method =
    findMethodBySignWithRegex(sign.replace("(", "\\(").replace(")", "\\)"), clazz)

fun <T> Any.callMethod(
    returnType: Class<T>,
    vararg argsAndTypes: Array<out Any?>,
): T? {
    val args = argsAndTypes.slice(0 until (argsAndTypes.size / 2)).toTypedArray()
    val types: Array<Class<*>> =
        argsAndTypes.slice(argsAndTypes.size / 2 until argsAndTypes.size)
            .map {
                if (it !is Class<*>) throw IllegalArgumentException("参数类型不是 Class<?>")
                it
            }
            .toTypedArray()
    val m = findMethodBySignWithRegex(""".*?(${getClassesSign(types)})${
        returnType.name.replace('.',
            '/')
    }""", javaClass)
    return m.invoke(this, *args) as T?
}

fun <T> Class<*>.callStaticMethod(
    returnType: Class<T>,
    vararg argsAndTypes: Array<out Any?>,
): T? {
    val args = argsAndTypes.slice(0 until (argsAndTypes.size / 2)).toTypedArray()
    val types: Array<Class<*>> =
        argsAndTypes.slice(argsAndTypes.size / 2 until argsAndTypes.size)
            .map {
                if (it !is Class<*>) throw IllegalArgumentException("参数类型不是 Class<?>")
                it
            }
            .toTypedArray()
    val m = findMethodBySignWithRegex(""".*?(${getClassesSign(types)})${
        returnType.name.replace('.',
            '/')
    }""", this)
    return m.invoke(null, *args) as T?
}

private fun getClassesSign(classes: Array<Class<*>>): String {
    val sb = java.lang.StringBuilder()
    classes.forEach {
        sb.append(it.name.replace('.', '/'))
    }
    return sb.toString()
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
fun Any.callMethod(
    methodName: String,
    vararg argsTypesAndReturnType: Any?,
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
    vararg argsTypesAndReturnType: Any?,
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
    type: Class<T>? = null,
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
    type: Class<T>? = null,
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
    type: Class<*>? = null,
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
    type: Class<*>? = null,
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
    vararg argsAndTypes: Any?,
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
    type: Class<*>? = null,
) = findField(if (obj is Class<*>) obj else obj.javaClass, type, name)

fun findField(
    clz: Class<*>,
    type: Class<*>?,
    name: String,
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

fun Class<*>.getSign(): String {
    if (isPrimitive) {
        return when (this) {
            Int::class.java -> "I"
            Long::class.java -> "J"
            Byte::class.java -> "B"
            Boolean::class.java -> "Z"
            Float::class.java -> "F"
            Double::class.java -> "D"
            Char::class.java -> "C"
            Short::class.java -> "S"
            Void.TYPE -> "V"
            else -> throw RuntimeException("未知错误")
        }
    }
    if (isArray) {
        return "[${componentType!!.getSign()}"
    }
    return "L${name.replace('.', '/')};"
}