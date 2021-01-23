package tsihen.me.qscript.util

import de.robv.android.xposed.XposedBridge

object Initiator {
    private var sHostClassLoader: ClassLoader? = null
    private var sPluginParentClassLoader: ClassLoader? = null

    fun init(rtLoader: ClassLoader) {
        try {
            sHostClassLoader = rtLoader
            val fParent = ClassLoader::class.java.getDeclaredField("parent")
            fParent.isAccessible = true
            val mine = Initiator::class.java.classLoader
            var curr = fParent[mine] as? ClassLoader?
            if (curr == null) {
                curr = XposedBridge::class.java.classLoader!!
            }
            if (curr.javaClass.name != HybridClassLoader::class.java.getName()) {
                fParent[mine] = HybridClassLoader(curr, rtLoader).also {
                    sPluginParentClassLoader = it
                }
            }
        } catch (e: Exception) {
            log(e)
        }
    }
}