package tsihen.me.qscript.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import java.io.File
import java.io.FileOutputStream

object Natives {
    external fun getpagesize(): Int

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    @Throws(Throwable::class)
    fun load(ctx: Context) {
        try {
            getpagesize()
            return
        } catch (ignored: UnsatisfiedLinkError) {
        }
        val abi = Build.CPU_ABI
        val soName = "libnatives_" + abi + "_" + QS_VERSION_NAME + ".so"
        val dir = File(ctx.filesDir, "qs_dyn_lib")
        if (!dir.isDirectory) {
            if (dir.isFile) {
                dir.delete()
            }
            dir.mkdir()
        }
        val soFile = File(dir, soName)
        if (!soFile.exists()) {
            val inputStream = Natives::class.java.classLoader!!
                .getResourceAsStream("lib/$abi/libnative-lib.so")
                ?: throw UnsatisfiedLinkError("Unsupported ABI: $abi")
            //clean up old files
            for (name in dir.list()) {
                if (name.startsWith("libnatives_")) {
                    File(dir, name).delete()
                }
            }
            //extract so file
            soFile.createNewFile()
            val fileOutputStream = FileOutputStream(soFile)
            val buf = ByteArray(1024)
            var i: Int
            while (inputStream.read(buf).also { i = it } > 0) {
                fileOutputStream.write(buf, 0, i)
            }
            inputStream.close()
            fileOutputStream.flush()
            fileOutputStream.close()
        }
        System.load(soFile.absolutePath)
    }
}