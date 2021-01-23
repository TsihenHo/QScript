package tsihen.me.qscript.util

import android.content.Context
import java.net.URL

class HybridClassLoader(private val clPreload: ClassLoader?, private val clBase: ClassLoader?) :
    ClassLoader() {
    @Throws(ClassNotFoundException::class)
    override fun loadClass(
        name: String?,
        resolve: Boolean
    ): Class<*> {
        try {
            return sBootClassLoader!!.loadClass(name)
        } catch (ignored: ClassNotFoundException) {
        }
        if (name != null && (name.startsWith("androidx.") || name.startsWith("android.support.v4.")
                    || name.startsWith("kotlin.") || name.startsWith("kotlinx."))
        ) {
            //Nevertheless, this will not interfere with the host application,
            //classes in host application SHOULD find with their own ClassLoader, eg Class.forName()
            //use shipped androidx and kotlin lib.
            throw ClassNotFoundException(name)
        }
        //The ClassLoader for XPatch is terrible, XposedBridge.class.getClassLoader() == Context.getClassLoader(),
        //which mess up with my kotlin lib.
        if (clPreload != null) {
            try {
                return clPreload.loadClass(name)
            } catch (ignored: ClassNotFoundException) {
            }
        }
        if (clBase != null) {
            try {
                return clBase.loadClass(name)
            } catch (ignored: ClassNotFoundException) {
            }
        }
        throw ClassNotFoundException(name)
    }

    override fun getResource(name: String): URL {
        val ret = clPreload!!.getResource(name)
        return ret ?: clBase!!.getResource(name)
    }

    companion object {
        private val sBootClassLoader =
            Context::class.java.classLoader
    }
}
