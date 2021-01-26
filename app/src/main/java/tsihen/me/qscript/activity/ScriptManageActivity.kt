package tsihen.me.qscript.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.activity_setting.*
import tsihen.me.qscript.R
import tsihen.me.qscript.util.logd
import tsihen.me.qscript.util.logi

class ScriptManageActivity : BaseActivity() {
    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        logi("启动脚本管理，application is ${this.application.packageName}")
        setContentView(
            LayoutInflater.from(this).inflate(R.layout.activity_script_manage, null, false)
        )
        topAppBar.setNavigationOnClickListener { finish() }
    }
}