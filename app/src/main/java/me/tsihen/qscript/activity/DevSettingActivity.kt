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
package me.tsihen.qscript.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import me.tsihen.qscript.R
import me.tsihen.qscript.config.ConfigManager
import me.tsihen.qscript.databinding.ActivityDevSettingBinding
import me.tsihen.qscript.script.QScriptManager
import me.tsihen.qscript.ui.IOnClickListener
import me.tsihen.qscript.ui.IOnClickListenerFilled
import me.tsihen.qscript.ui.ViewFilledWithTwoLinesAndImage
import me.tsihen.qscript.ui.ViewWithTwoLinesAndImage
import me.tsihen.qscript.util.*
import java.io.File
import kotlin.system.exitProcess

class DevSettingActivity : BaseActivity(), IOnClickListener, IOnClickListenerFilled {
    private lateinit var mViewBinding: ActivityDevSettingBinding

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = ActivityDevSettingBinding.inflate(layoutInflater)
        setContentView(mViewBinding.root)

        mViewBinding.debugMode.changeColor(debugMode)

        mViewBinding.debugMode.setOnClickListener(this)
        mViewBinding.takeExam.setOnClickListener(this)
        mViewBinding.showLog.setOnClickListener(this)
        mViewBinding.removeAllLog.setOnClickListener(this)
        mViewBinding.removeAllData.setOnClickListener(this)
        mViewBinding.devSettingData.setOnClickListener(this)
        mViewBinding.devSettingShowSetting.setOnClickListener(this)
        mViewBinding.repairModule.setOnClickListener(this)
        mViewBinding.topAppBar.setNavigationOnClickListener { finish() }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n")
    override fun onClick(v: ViewWithTwoLinesAndImage) {
        when (v.id) {
            R.id.repair_module -> {
                val builder = AlertDialog.Builder(this)
                    .setNegativeButton("我反悔了") { dialog, _ -> dialog.dismiss() }
                    .setPositiveButton("继续") { _, _ ->
                        try {
                            ConfigManager.getCache().removeAll()
                            try {
                                QScriptManager.getScripts().forEach {
                                    it.setEnable(false)
                                    QScriptManager.delEnable()
                                }
                            } catch (e: java.lang.Exception) {
                                log(e)
                                Toasts.error(this, "停用脚本时出现错误")
                            }
                            startActivity(Intent(this, Initiator.load(".activity.SplashActivity")))
                            exitProcess(0)
                        } catch (e: java.lang.Exception) {
                            log(e)
                        }
                    }
                    .setTitle("您真的要继续吗？")
                    .setMessage("这将在尝试保留所有数据的情况下修复模块\n\n注：本选项不会移除任何模块数据")
                val dialog = builder.create()
                dialog.show()
                val btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                btn.isEnabled = false
                Thread {
                    var time = 3
                    do {
                        runOnUiThread { btn.text = "继续 ($time)" }
                        try {
                            Thread.sleep(1000)
                        } catch (e: Exception) {
                            log(e)
                            btn.isEnabled = true
                        }
                        time -= 1
                    } while (time > 0)
                    runOnUiThread {
                        btn.isEnabled = true
                        btn.text = "继续"
                    }
                }.start()
            }
            R.id.take_exam -> {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    Toasts.success(this, "您的设备不支持高级验证")
                    return
                }
                startActivity<ExamActivity>()
            }
            R.id.show_log -> {
                val file =
                    File(Environment.getExternalStorageDirectory().absolutePath + "/me.tsihen.qscript.log")
                if (!file.exists()) {
                    Toasts.info(this, "没有可用的日志文件")
                    return
                }
                AlertDialog.Builder(this)
                    .setTitle("成功")
                    .setMessage("日志位于：${file.absolutePath}")
                    .setNegativeButton("好") { d, _ -> d.dismiss() }
                    .show()
            }
            R.id.remove_all_log -> {
                if (File(Environment.getExternalStorageDirectory().absolutePath + "/me.tsihen.qscript.log").delete()) {
                    Toasts.success(this, "成功")
                } else {
                    Toasts.error(this, "失败")
                }
            }
            R.id.remove_all_data -> {
                val builder = AlertDialog.Builder(this)
                    .setNegativeButton("我反悔了") { dialog, _ -> dialog.dismiss() }
                    .setPositiveButton("继续") { _, _ ->
                        try {
                            ConfigManager.getDefaultConfig().removeAll()
                            ConfigManager.getCache().removeAll()
                            try {
                                QScriptManager.scriptsPath?.let {
                                    val dir = File(it)
                                    if (dir.isFile || !dir.exists()) return@let
                                    dir.listFiles()?.forEach { f ->
                                        if (!f.name.endsWith(".java")) return@forEach
                                        f.delete()
                                    }
                                }
                            } catch (e: java.lang.Exception) {
                                log(e)
                                Toasts.error(this, "移除脚本时出现错误")
                            }
                            startActivity(Intent(this, Initiator.load(".activity.SplashActivity")))
                            exitProcess(0)
                        } catch (e: java.lang.Exception) {
                            log(e)
                        }
                    }
                    .setTitle("您真的要继续吗？")
                    .setMessage("这将移除该模块的全部数据，请谨慎操作——虽然这有时候能够解决一些莫名其妙的问题。\n\n注：本选项不会清除模块的日志")
                val dialog = builder.create()
                dialog.show()
                val btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                btn.isEnabled = false
                Thread {
                    var time = 5
                    do {
                        runOnUiThread { btn.text = "继续 ($time)" }
                        try {
                            Thread.sleep(1000)
                        } catch (e: Exception) {
                            log(e)
                            btn.isEnabled = true
                        }
                        time -= 1
                    } while (time > 0)
                    runOnUiThread {
                        btn.isEnabled = true
                        btn.text = "继续"
                    }
                }.start()
            }
            R.id.dev_setting_data -> {
                val bool = true
                val str = "\\\\\\\\\"测试${'$'}"
                AlertDialog.Builder(this)
                    .setPositiveButton("存入") { _, _ ->
                        val mgr = ConfigManager.getDefaultConfig()
                        mgr["test_bool"] = bool
                        mgr["test_str"] = str
                        Toasts.success(this, "成功")
                    }
                    .setNegativeButton("取出") { _, _ ->
                        val mgr = ConfigManager.getDefaultConfig()
                        val boolGet = mgr["test_bool"]
                        val strGet = mgr["test_str"]
                        if (boolGet == bool && strGet == str) {
                            Toasts.success(this, "成功")
                        } else {
                            Toasts.error(this, "错误")
                        }
                    }
                    .setNeutralButton("清空测试数据") { _, _ ->
                        val mgr = ConfigManager.getDefaultConfig()
                        mgr.remove("test_bool")
                        mgr.remove("test_str")
                        Toasts.success(this, "成功")
                    }
                    .show()
            }
            R.id.dev_setting_show_setting -> AlertDialog.Builder(this)
                .setTitle(getApplicationNonNull().filesDir.absolutePath + "/qscript_config.json")
                .setNegativeButton("好") { d, _ -> d.dismiss() }
                .setMessage(ConfigManager.getDefaultConfig().getFileContent())
                .show()

        }
    }

    override fun onClick(v: ViewFilledWithTwoLinesAndImage) {
        when (v.id) {
            R.id.debug_mode -> {
                debugMode = !debugMode
                ConfigManager.getDefaultConfig()["debug_mode"] =
                    !((ConfigManager.getDefaultConfig()["debug_mode"] ?: false) as Boolean)
                v.changeColor(debugMode)
            }
        }
    }
}