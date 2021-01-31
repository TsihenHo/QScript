package tsihen.me.qscript

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import tsihen.me.qscript.databinding.ActivityMainBinding
import tsihen.me.qscript.util.*
import tsihen.me.qscript.util.HookStatue.getStatue
import tsihen.me.qscript.util.HookStatue.getStatueName
import tsihen.me.qscript.util.HookStatue.isActive
import java.util.*

class MainActivity : AppCompatActivity() {
    private var dbgInfo = ""
    private lateinit var mViewBinding: ActivityMainBinding

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding  = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mViewBinding.root)

        var str: String? = ""
        try {
            str += """
                系统类加载器:${ClassLoader.getSystemClassLoader()}
                启用的版本:${getActiveModuleVersion()}
                安装的版本:${QS_VERSION_NAME}
                """.trimIndent()
        } catch (r: Throwable) {
            str += r
        }
        dbgInfo += str
        try {
            var delta = System.currentTimeMillis()
            Natives.load(this)
            val ts: Long = getBuildTimestamp(this)
            delta = System.currentTimeMillis() - delta
            dbgInfo += "\n构建时间:" + (if (ts > 0) Date(ts).toString() else "unknown") + ", " +
                    "δ(delta)=" + delta + "ms\n" +
                    "被支持的 ABI(S):" + Arrays.toString(Build.SUPPORTED_ABIS) +
                    "\n当前 ABI: ${Build.CPU_ABI}" + "\n是否支持？：${Build.CPU_ABI in Build.SUPPORTED_ABIS}" +
                    "\n页大小:" + Natives.getpagesize()
        } catch (e: Throwable) {
            dbgInfo += "\n" + e.toString()
        }
        val statue = this.getStatue(false)
        mViewBinding.mainActivationStatusLinearLayout.background = ResourcesCompat.getDrawable(
            resources,
            if (statue.isActive()) R.drawable.bg_green_solid else R.drawable.bg_red_solid,
            theme
        )
        mViewBinding.mainActivationStatusIcon.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                if (statue.isActive()) R.drawable.ic_success_white else R.drawable.ic_failure_white,
                theme
            )
        )
        mViewBinding.mainActivationStatusTitle.text = if (getActiveModuleVersion() != null) "已激活" else "未激活"
        mViewBinding.mainActivationStatusDesc.text =
            getString(statue.getStatueName()).split(" ".toRegex()).toTypedArray()[0]
        mViewBinding.mainTextViewVersion.text = QS_VERSION_NAME
        mViewBinding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_item_debugInfo -> {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("调试信息").setPositiveButton(android.R.string.ok, null)
                        .setMessage(dbgInfo).show()
                    return@setOnMenuItemClickListener true
                }
                R.id.menu_item_about -> {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("关于").setPositiveButton(android.R.string.ok, null)
                        .setMessage("作者：Tsihen Ho").show()
                    return@setOnMenuItemClickListener true
                }
                else -> {
                    return@setOnMenuItemClickListener super@MainActivity.onOptionsItemSelected(it)
                }
            }
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
//    private external fun stringFromJNI(): String

//    companion object {
//        // Used to load the 'native-lib' library on application startup.
//        init {
//            System.loadLibrary("native-lib")
//        }
//    }

    fun openModuleSettingForHost(view: View) {
        val pkg: String = when (view.id) {
            R.id.main_relativeLayoutButtonOpenQQ -> PACKAGE_NAME_QQ
            R.id.main_relativeLayoutButtonOpenTIM -> PACKAGE_NAME_TIM
            else -> "@string/nothing"
        }
        if (pkg == "@string/nothing") {
            return
        }
        val intent = Intent()
        intent.component = ComponentName(pkg, "com.tencent.mobileqq.activity.JumpActivity")
        intent.action = Intent.ACTION_VIEW
        intent.putExtra(JUMP_ACTION_CMD, JUMP_ACTION_SETTING_ACTIVITY)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            AlertDialog.Builder(this).setTitle("错误")
                .setMessage("拉起模块设置失败, 请确认 $pkg 已安装并启用。错误信息如下：\n$e")
                .setCancelable(true).setPositiveButton(android.R.string.ok, null).show()
        }
    }

    fun handleClickEvent(view: View) {
        when (view.id) {
            R.id.main_githubRepo -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://github.com/GoldenHuaji/QScript")
                startActivity(intent)
            }
            R.id.main_help -> {
                AlertDialog.Builder(this)
                    .setMessage("如模块无法使用，EdXp可尝试取消优化+开启兼容模式, 太极尝试取消优化")
                    .setCancelable(true).setPositiveButton(android.R.string.ok, null).show()
            }
            else -> {
            }
        }
    }
}