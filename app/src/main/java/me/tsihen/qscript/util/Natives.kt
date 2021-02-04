/* QScript - An Xposed module to run scripts on QQ
 * Copyright (C) 2021-20222 chinese.he.amber@gmail.com
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
package me.tsihen.qscript.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import java.io.File
import java.io.FileOutputStream

object Natives {
    external fun getpagesize(): Int

    @Suppress("DEPRECATION", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("UnsafeDynamicallyLoadedCode")
    @JvmOverloads
    @Throws(Throwable::class)
    fun load(ctx: Context, must: Boolean = false) {
        if (!must) {
            try {
                getpagesize()
                return
            } catch (ignored: UnsatisfiedLinkError) {
            }
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