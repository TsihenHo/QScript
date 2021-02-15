/* QScript - An Xposed module to run scripts on QQ
 * Copyright (C) 2021-2022 chinese.he.amber@gmail.com
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
package me.tsihen.qscript.script

import bsh.EvalError
import bsh.Interpreter
import me.tsihen.qscript.util.*
import java.io.*
import java.util.*
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
object QScriptManager {
    var enables = 0
    private val scripts: MutableList<QScript> = mutableListOf()
    var scriptsPath: String? = null
    private var init = false

    @FromQNotified
    fun init() {
        if (init) return
        scriptsPath =
            getApplicationNonNull().filesDir.absolutePath.toString() + "/qs_scripts/"
        for (code in getScriptCodes()) {
            try {
                val qs: QScript = execute(code)
                scripts.add(qs)
                if (qs.isEnable()) {
                    qs.onLoad()
                }
            } catch (e: EvalError) {
                log(e)
            }
        }
        init = true
    }

    /**
     * 添加一个脚本
     *
     * @param file 文件
     * @return
     */
    @FromQNotified
    @Throws(java.lang.Exception::class)
    fun addScript(file: String): String {
        if (file.isEmpty()) return "file is null"
        if (hasScript(file)) return "脚本已存在"
        // 操作: 将文件移动到软件数据文件夹下
        val s = File(file)
        val dir = File(scriptsPath!!)
        if (!dir.exists()) dir.mkdirs()
        val f = File(dir, s.name)
        copy(s, f)
        val code: String = f.readText()
        if (code.isNotEmpty()) scripts.add(execute(code))
        return ""
    }

    @Throws(Throwable::class)
    @FromQNotified
    fun addScriptFD(fileDescriptor: FileDescriptor?, scriptName: String): String {
        val dir = File(scriptsPath!!)
        if (!dir.exists()) dir.mkdirs()
        var fileInputStream: FileInputStream? = null
        var fileOutputStream: FileOutputStream? = null
        try {
            fileInputStream = FileInputStream(fileDescriptor)
            val stringBuffer = StringBuffer()
            val buf = ByteArray(1024)
            var len: Int
            while (fileInputStream.read(buf).also { len = it } > 0) {
                stringBuffer.append(String(buf, 0, len))
            }
            if (hasScriptStr(stringBuffer.toString())) return "脚本已存在"
            fileOutputStream =
                FileOutputStream(scriptsPath + scriptName)
            val outputStreamWriter = OutputStreamWriter(fileOutputStream)
            outputStreamWriter.write(stringBuffer.toString())
            outputStreamWriter.close()
            fileOutputStream.flush()
        } finally {
            fileInputStream?.close()
            fileOutputStream?.close()
        }
        val code = File(scriptsPath + scriptName).readText()
        if (code.isNotEmpty()) {
            scripts.add(execute(code))
        }
        return ""
    }

    /**
     * 删除脚本
     *
     * @param script
     * @return
     */
    @FromQNotified
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun delScript(script: QScript): Boolean {
        // 删除文件
        val dir = File(scriptsPath!!)
        if (!dir.exists()) dir.mkdirs()
        if (!dir.isDirectory) {
            log(java.lang.RuntimeException("脚本文件夹不应为一个文件"))
            return false
        }
        for (f in dir.listFiles()) {
            if (f.isDirectory) continue
            try {
                val info: QScriptInfo = QScriptInfo.getInfo(f.readText()) ?: continue
                if (info.label == script.getLabel()) {
                    f.delete()
                    for (q in scripts) {
                        if (q.getLabel()  == script.getLabel()) {
                            scripts.remove(q)
                        }
                    }
                    return true
                }
            } catch (e: java.lang.Exception) {
                log(e)
            }
        }
        return false
    }

    /**
     * 更改脚本
     */
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun replaceScript(oldScript: QScript, newScript: QScript): Boolean {
        // 更改文件
        if (oldScript.getLabel() != newScript.getLabel()) {
            throw java.lang.RuntimeException("新旧脚本标签不一致")
        }
        val dir = File(scriptsPath!!)
        if (!dir.exists()) dir.mkdirs()
        if (!dir.isDirectory) {
            log(java.lang.RuntimeException("脚本文件夹不应为一个文件"))
            return false
        }
        scripts.forEachIndexed { index, qScript ->
            if (qScript.getLabel() == oldScript.getLabel()) {
                scripts[index] = newScript
                scripts[index].setEnable(oldScript.isEnable())
            }
        }
        dir.listFiles().forEach {
            if (it.isDirectory) return@forEach
            try {
                val info = QScriptInfo.getInfo(it.readText()) ?: return@forEach
                if (info.label != oldScript.getLabel()) return@forEach
                it.writeText(newScript.getCode())
                return true
            } catch (e: java.lang.Exception) {
                log(e)
            }
        }
        return false
    }

    @Throws(EvalError::class)
    @FromQNotified
    fun execute(code: String?): QScript {
        val lp = Interpreter()
        lp.setClassLoader(Initiator::class.java.classLoader)
        return QScript.create(lp, code ?: throw java.lang.RuntimeException("无效脚本"))
    }

    @FromQNotified
    fun getScripts() = scripts

    @FromQNotified
    fun addEnable() {
        enables++
        if (enables > scripts.size - 1) enables = scripts.size
    }

    @FromQNotified
    fun delEnable() {
        enables--
        if (enables < 0) enables = 0
    }

    /**
     * 判断脚本是否存在
     *
     * @param file 文件
     * @return 是否存在
     */
    @FromQNotified
    @Throws(Exception::class)
    fun hasScript(file: String?): Boolean {
        if (file.isNullOrEmpty()) return false
        // 判断文件
        val info: QScriptInfo =
            QScriptInfo.getInfo((File(file).readText())) ?: throw RuntimeException("不是有效的脚本文件")
        getScripts().forEach { q ->
            if (info.label == q.getLabel()) {
                return true
            }
        }
        return false
    }

    @FromQNotified
    @Throws(java.lang.Exception::class)
    fun hasScriptStr(code: String): Boolean {
        val info: QScriptInfo = QScriptInfo.getInfo(code)
            ?: throw java.lang.RuntimeException("不是有效的脚本文件")
        getScripts().forEach { q ->
            if (info.label == q.getLabel()) {
                return true
            }
        }
        return false
    }

    /**
     * 获取所有的脚本代码
     *
     * @return
     */
    @FromQNotified
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun getScriptCodes(): List<String?> {
        // to do
        // 返回全部脚本代码
        val codes: MutableList<String?> = object : ArrayList<String?>() {
            init {
                try {
                    add(
                        BufferedReader(
                            InputStreamReader(
                                Initiator::class.java.classLoader!!.getResourceAsStream(
                                    "assets/demo.java"
                                )
                            )
                        ).readText()
                    )
                } catch (e: IOException) {
                    log(e)
                }
            }
        }
        val dir = File(scriptsPath!!)
        if (!dir.exists()) dir.mkdirs()
        if (!dir.isDirectory) {
            log(java.lang.RuntimeException("脚本文件夹不应为一个文件"))
            return codes
        }
        for (f in dir.listFiles()) {
            if (f.isDirectory) continue
            try {
                val code: String = f.readText()
                if (code.isNotEmpty()) codes.add(code)
            } catch (e: java.lang.Exception) {
                log(e)
            }
        }
        return codes
    }
}