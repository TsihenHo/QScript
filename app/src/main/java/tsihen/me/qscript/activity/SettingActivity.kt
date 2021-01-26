package tsihen.me.qscript.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_setting.*
import tsihen.me.qscript.R
import tsihen.me.qscript.ui.IOnClickListener
import tsihen.me.qscript.ui.ViewWithTwoLinesAndImage
import tsihen.me.qscript.util.*
import java.util.*


class SettingActivity : BaseActivity(), IOnClickListener {
    @Suppress("DEPRECATION")
    @SuppressLint("InflateParams", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logi("启动设置，application is ${this.application.packageName}")
        setContentView(
            LayoutInflater.from(this).inflate(R.layout.activity_setting, null, false)
        )

        textViewVersion.text = QS_VERSION_NAME
        statusLinearLayout.background =
            ResourcesCompat.getDrawable(resources, R.drawable.bg_green_solid, theme)
        statusIcon.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_success_white,
                theme
            )
        )
        statusTitle.text = "一切正常"
        statusDesc.text = "看起来什么问题也没有\n如果出现问题，请进入高级设置，点击“修复模块”——但这通常没有效果（逃"

        script_manage.setOnClickListener(this)
        settings.setOnClickListener(this)
        bug.setOnClickListener(this)
        about_me.setOnClickListener(this)

        topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_item_debugInfo -> {
                    var dbgInfo = ""
                    var str: String? = ""
                    try {
                        str += """
                            系统类加载器:${ClassLoader.getSystemClassLoader()}
                            启用的版本:${getActiveModuleVersion()}
                            安装的版本:${QS_VERSION_NAME}
                            调试模式:$DEBUG_MODE
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
                                "\n页大小:" + Natives.getpagesize()
                    } catch (e: Throwable) {
                        dbgInfo += "\n" + e.toString()
                    }
                    androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("调试信息").setPositiveButton(android.R.string.ok, null)
                        .setMessage(dbgInfo).show()
                    return@setOnMenuItemClickListener true
                }
                R.id.menu_item_about -> {
                    about_me.performClick()
                    return@setOnMenuItemClickListener true
                }
                else -> {
                    return@setOnMenuItemClickListener super.onOptionsItemSelected(it)
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onClick(v: ViewWithTwoLinesAndImage) {
        logd("SettingActivity : 你点击了一些东西")
        when (v.id) {
            R.id.about_me -> AlertDialog.Builder(
                this,
                android.R.style.Theme_DeviceDefault_Light_Dialog_Alert
            )
                .setMessage(Html.fromHtml(this.getString(R.string.about_me)))
                .setTitle("关于")
                .setIcon(ContextCompat.getDrawable(this, R.drawable.ic_people))
                .setPositiveButton("关闭") { d, _ -> d.dismiss() }
                .setNeutralButton("开源地址") { d, _ ->
                    d.dismiss()
                    //调用内置动作
                    val intent = Intent(Intent.ACTION_VIEW)
                    //将url解析为Uri对象，再传递出去
                    intent.data = Uri.parse("https://github.com/GoldenHuaji/QScript")
                    //启动
                    startActivity(intent)
                }
                .show()
            R.id.script_manage -> startActivity<ScriptManageActivity>()
        }
    }
}