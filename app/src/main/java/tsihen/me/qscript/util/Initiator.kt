package tsihen.me.qscript.util

object Initiator {
    private var sHostClassLoader: ClassLoader? = null
    private var sPluginParentClassLoader: ClassLoader? = null

    fun init(rtLoader: ClassLoader) {
        try {
            sPluginParentClassLoader = rtLoader
        } catch (e: Exception) {
            log(e)
        }
    }

    @JvmStatic
    fun load(classPath: String, classLoader: ClassLoader? = null): Class<*>? {
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
            sPluginParentClassLoader = classLoader ?: sPluginParentClassLoader
            sPluginParentClassLoader!!.loadClass(className)
        } catch (e: Throwable) {
            log(e)
            null
        }
    }
}