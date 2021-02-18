package me.tsihen.qscript.util

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import java.lang.reflect.Field
import java.lang.reflect.Method

inline fun Method.before(crossinline action: (param: XC_MethodHook.MethodHookParam) -> Unit): Method {
    XposedBridge.hookMethod(this, object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            action(param)
        }
    })
    return this
}

inline fun Method.after(crossinline action: (param: XC_MethodHook.MethodHookParam) -> Unit): Method {
    XposedBridge.hookMethod(this, object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            action(param)
        }
    })
    return this
}

val String.method: Method
    get() = DexMethod(this).getMethod()

val String.field: Field
    get() = DexField(this).getField()