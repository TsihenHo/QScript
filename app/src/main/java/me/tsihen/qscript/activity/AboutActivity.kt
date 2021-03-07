package me.tsihen.qscript.activity

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import me.tsihen.qscript.BuildConfig
import me.tsihen.qscript.R
import me.tsihen.qscript.config.ConfigManager
import me.tsihen.qscript.config.StatusChecker.getCheckNum
import me.tsihen.qscript.databinding.ActivityAboutBinding
import me.tsihen.qscript.ui.IOnClickListener
import me.tsihen.qscript.ui.ViewWithTwoLinesAndImage
import me.tsihen.qscript.util.Toasts
import me.tsihen.qscript.util.debugMode
import me.tsihen.qscript.util.startActivity


class AboutActivity : AbsActivity(), IOnClickListener {
    private lateinit var mViewBinding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(mViewBinding.root)

        mViewBinding.topAppBar.setNavigationOnClickListener { finish() }

        mViewBinding.license.setOnClickListener(this)
        mViewBinding.openGithub.setOnClickListener(this)
        mViewBinding.addSelfQq.setOnClickListener(this)
        mViewBinding.selfMail.setOnClickListener(this)
        mViewBinding.addQqGroup.setOnClickListener(this)

        mViewBinding.icon.setOnLongClickListener {
            if (ConfigManager.getDefaultConfig().getOrDefault("pass_by_exam", false) &&
                debugMode || BuildConfig.DEBUG
            ) {
                AlertDialog.Builder(this)
                    .setTitle("警告")
                    .setMessage("您发现了实验性功能。这将尝试使本模块兼容QQ复读机的脚本。您真的要继续吗？")
                    .setPositiveButton("继续") { d, _ ->
                        d.dismiss()
                        val checkInt = getCheckNum()
                        ConfigManager.getDefaultConfig()["run_repeater_script_$checkInt"] = true
                        Toasts.info(this, "制作中...")
                    }
                    .setNegativeButton("算了", null)
                    .show()
            }
            true
        }
    }

    override fun onClick(v: ViewWithTwoLinesAndImage) {
        when (v.id) {
            R.id.license -> startActivity<OpenSourceLicenseActivity>()
            R.id.open_github -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://github.com/GoldenHuaji/QScript")
                startActivity(intent)
            }
            R.id.self_mail -> {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:") // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, "3318448676@qq.com")
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }
            R.id.add_self_qq -> {
                try {
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip: ClipData = ClipData.newPlainText("simple text", "3318448676")
                    clipboard.setPrimaryClip(clip)
                    Toasts.success(this, "QQ号码已经复制到您的剪切板")
                } catch (e: Exception) {
                    Toasts.error(this, "执行时遇到错误")
                }
            }
            R.id.add_qq_group -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://t.me/QScript")
                startActivity(intent)
            }
        }
    }
}