package tsihen.me.qscript.util

import de.robv.android.xposed.XposedBridge

object Initiator {
    private var sHostClassLoader: ClassLoader? = null
    private var sPluginParentClassLoader: ClassLoader? = null

    fun init(rtLoader: ClassLoader) {
        try {
            sPluginParentClassLoader = rtLoader
//            val fParent = ClassLoader::class.java.getDeclaredField("parent")
//            fParent.isAccessible = true
//            val mine = Initiator::class.java.classLoader
//            var curr = fParent[mine] as? ClassLoader?
//            if (curr == null) {
//                curr = XposedBridge::class.java.classLoader!!
//            }
//            if (curr.javaClass.name != HybridClassLoader::class.java.getName()) {
//                fParent[mine] = HybridClassLoader(curr, rtLoader).also {
//                    sPluginParentClassLoader = it
//                }
//            }
        } catch (e: Exception) {
            log(e)
        }
    }

    @JvmStatic
    fun load(classPath: String): Class<*>? {
        if (classPath.isEmpty()) {
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
            sPluginParentClassLoader!!.loadClass(className)
        } catch (e: Throwable) {
            log(e)
            null
        }
    }

    fun _StartupDirector(): Class<*>? {
        var director =
            load("com/tencent/mobileqq/startup/director/StartupDirector")
        if (director == null) {
            try {
                director = load("com/tencent/mobileqq/startup/director/StartupDirector$1")!!
                    .getDeclaredField("this$0").type
            } catch (ignored: NoSuchFieldException) {
            }
        }
        return director
    }
}