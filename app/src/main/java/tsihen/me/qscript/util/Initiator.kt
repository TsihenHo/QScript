package tsihen.me.qscript.util

object Initiator {
    @JvmStatic
    private var sHostClassloader: ClassLoader? = null

    @JvmStatic
    fun init(rtLoader: ClassLoader) {
        try {
            sHostClassloader = rtLoader
        } catch (e: Exception) {
            log(e)
        }
    }

    @JvmStatic
    fun load(classPath: String, classLoader: ClassLoader? = null): Class<*>? {
        sHostClassloader = classLoader ?: sHostClassloader
        if (classPath.isEmpty() || sHostClassloader == null) {
            logw("Initiator : Didn't init.")
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
            sHostClassloader!!.loadClass(className)
        } catch (e: Throwable) {
            log(e)
            null
        }
    }

    @JvmStatic
    fun getHostClassLoader() = sHostClassloader
}