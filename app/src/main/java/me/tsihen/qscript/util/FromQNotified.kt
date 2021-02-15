package me.tsihen.qscript.util

// 使用这个注解标明部分逻辑来自 QN
@Retention(AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.EXPRESSION,
    AnnotationTarget.LOCAL_VARIABLE
)
annotation class FromQNotified
