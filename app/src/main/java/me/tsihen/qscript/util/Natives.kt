/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
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
    external fun ntGetPageSize(): Int
    lateinit var soFilePath: String

    /**
     * 加载 Native
     *
     * @param ctx 上下文
     * @param must 是否强制加载
     */
    @FromQNotified
    @Suppress("DEPRECATION", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("UnsafeDynamicallyLoadedCode")
    @JvmOverloads
    @Throws(Throwable::class)
    fun load(ctx: Context, must: Boolean = false) {
        // 如果没有要求强制加载，那么检验模块是否已经成功加载
        if (!must) {
            try {
                ntGetPageSize()
                return
            } catch (ignored: UnsatisfiedLinkError) {
            }
        }
        try {
            val abi = Build.CPU_ABI
            val soName = "libnatives_" + abi + "_" + QS_VERSION_NAME + ".so"
            val dir = File(ctx.filesDir, "qscript_dyn_libs")
            if (!dir.isDirectory) {
                if (dir.isFile) {
                    dir.delete()
                }
                dir.mkdir()
            }
            val soFile = File(dir, soName)
            // 如果 so 不存在或者要求强制加载
            if (!soFile.exists() || must) {
                // delete 仅仅针对强制加载
                soFile.delete()
                val inputStream = Natives::class.java.classLoader!!
                    .getResourceAsStream("lib/$abi/libnative-lib.so")
                    ?: throw UnsatisfiedLinkError("Unsupported ABI: $abi")

                // 清空旧文件
                for (name in dir.list()) {
                    if (name.startsWith("libnatives_")) {
                        File(dir, name).delete()
                    }
                }

                // 提取 so
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
            soFilePath = soFile.absolutePath

            // 检验是否加载成功
            ntGetPageSize()
        } catch (e: Exception) {
            log(e)
        }
    }
}