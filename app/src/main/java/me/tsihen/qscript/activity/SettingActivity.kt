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
package me.tsihen.qscript.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import me.tsihen.qscript.R
import me.tsihen.qscript.config.ConfigManager
import me.tsihen.qscript.databinding.ActivitySettingBinding
import me.tsihen.qscript.ui.IOnClickListener
import me.tsihen.qscript.ui.IOnClickListenerFilled
import me.tsihen.qscript.ui.ViewFilledWithTwoLinesAndImage
import me.tsihen.qscript.ui.ViewWithTwoLinesAndImage
import me.tsihen.qscript.util.*
import java.util.*


class SettingActivity : BaseActivity(), IOnClickListener {
    private lateinit var mViewBinding: ActivitySettingBinding

    @Suppress("DEPRECATION")
    @SuppressLint("InflateParams", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            mViewBinding = ActivitySettingBinding.inflate(layoutInflater)
        } catch (t: Throwable) {
            log(t)
            finish()
        }
        setContentView(mViewBinding.root)

        mViewBinding.textViewVersion.text = QS_VERSION_NAME

        mViewBinding.scriptManage.setOnClickListener(this)
        mViewBinding.settings.setOnClickListener(this)
        mViewBinding.bug.setOnClickListener(this)
        mViewBinding.aboutMe.setOnClickListener(this)

        mViewBinding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_item_debugInfo -> {
                    var dbgInfo = ""
                    var str: String? = ""
                    try {
                        str += """
                            系统类加载器:${ClassLoader.getSystemClassLoader()}
                            启用的版本:${getActiveModuleVersion()}
                            安装的版本:${QS_VERSION_NAME}
                            调试模式:$debugMode
                        """.trimIndent()
                    } catch (r: Throwable) {
                        str += r
                    }
                    dbgInfo += str
                    try {
                        var delta = System.currentTimeMillis()
                        val ts: Long = getBuildTimestamp()
                        delta = System.currentTimeMillis() - delta
                        dbgInfo += "\n构建时间:" + (if (ts > 0) Date(ts).toString() else "unknown") + ", " +
                                "δ(delta)=" + delta + "ms\n" +
                                "被支持的 ABI(S):" + Arrays.toString(Build.SUPPORTED_ABIS) +
                                "\n当前 ABI: ${Build.CPU_ABI}" + "\n是否支持？：${Build.CPU_ABI in Build.SUPPORTED_ABIS}" +
                                "\n页大小:" + Natives.ntGetPageSize()
                    } catch (e: Throwable) {
                        dbgInfo += "\n" + e.toString()
                    }
                    AlertDialog.Builder(this)
                        .setTitle("调试信息").setPositiveButton(android.R.string.ok, null)
                        .setMessage(dbgInfo).show()
                    return@setOnMenuItemClickListener true
                }
                R.id.menu_item_about -> {
                    mViewBinding.aboutMe.performClick()
                    return@setOnMenuItemClickListener true
                }
                else -> {
                    return@setOnMenuItemClickListener super.onOptionsItemSelected(it)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        refresh()
    }

    private fun refresh() {
        val mgr = ConfigManager.getDefaultConfig()
        if (!mgr.getOrDefault("has_error", false)) {
            mViewBinding.statusLinearLayout.color =
                ResourcesCompat.getDrawable(resources, R.drawable.bg_green_solid, theme)
            mViewBinding.statusLinearLayout.image = R.drawable.ic_success_white
            mViewBinding.statusLinearLayout.title = "一切正常"
            mViewBinding.statusLinearLayout.desc = "看起来什么问题也没有\n如果出现问题，请进入高级设置，点击“修复模块”——但这通常没有效果（逃"
            mViewBinding.statusLinearLayout.setOnClickListener(null)
        } else {
            mViewBinding.statusLinearLayout.color =
                ResourcesCompat.getDrawable(resources, R.drawable.bg_yellow_solid, theme)
            mViewBinding.statusLinearLayout.image = R.drawable.ic_check_circle
            mViewBinding.statusLinearLayout.title = "有未处理的错误"
            mViewBinding.statusLinearLayout.desc = "请点击此处查看"
            mViewBinding.statusLinearLayout.setOnClickListener(object : IOnClickListenerFilled {
                override fun onClick(v: ViewFilledWithTwoLinesAndImage) {
                    AlertDialog.Builder(this@SettingActivity)
                        .setPositiveButton("好") { d, _ -> d.dismiss() }
                        .setNegativeButton("反馈") { _, _ -> mViewBinding.bug.performClick() }
                        .setNeutralButton("清空错误警告") { _, _ ->
                            mgr["has_error"] = false
                            mgr["error_message"] = ""
                            refresh()
                            Toasts.success(this@SettingActivity, "成功")
                        }
                        .setTitle("错误：")
                        .setMessage(mgr["error_message"] as? String? ?: "无法读取错误")
                        .show()
                }
            })
        }
    }

    @Suppress("DEPRECATION")
    override fun onClick(v: ViewWithTwoLinesAndImage) {
        when (v.id) {
            R.id.about_me -> AlertDialog.Builder(this)
                .setMessage(Html.fromHtml(this.getString(R.string.about_me)))
                .setTitle("关于")
                .setIcon(ContextCompat.getDrawable(this, R.drawable.ic_people))
                .setPositiveButton("关闭") { d, _ -> d.dismiss() }
                .setNeutralButton("开源地址") { d, _ ->
                    d.dismiss()
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://github.com/GoldenHuaji/QScript")
                    startActivity(intent)
                }
                .show()
            R.id.script_manage -> startActivity<ScriptManageActivity>()
            R.id.settings -> startActivity<DevSettingActivity>()
            R.id.bug -> AlertDialog.Builder(this)
                .setTitle("问题反馈")
                .setMessage(
                    "这里有两种方式进行问题反馈，反馈错误时，请明确表明如何复现这个错误并且附上模块的日志。" +
                            "当然，您也可以提出建议。\n\n反馈方式：\n1. 向我发送邮件。\n2. 前往 Github，提交反馈。"
                )
                .setNeutralButton("Github") { d, _ ->
                    d.dismiss()
                    //调用内置动作
                    val intent = Intent(Intent.ACTION_VIEW)
                    //将url解析为Uri对象，再传递出去
                    intent.data = Uri.parse("https://github.com/GoldenHuaji/QScript/issues")
                    //启动
                    startActivity(intent)
                }
                .setNegativeButton("好") { d, _ -> d.dismiss() }
                .show()
        }
    }
}