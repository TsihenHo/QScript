package tsihen.me.qscript.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.activity_setting.*
import tsihen.me.qscript.R
import tsihen.me.qscript.util.*

class SettingActivity : Activity() {
    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault_Light)
        title = "QScript"
        logd("启动设置")
        val thisContext = this.createPackageContext(
            PACKAGE_NAME_SELF,
            Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY
        )
        logd("thisContext: $thisContext")
        if (intent.getStringExtra(JUMP_ACTION_CMD) != JUMP_ACTION_SETTING_ACTIVITY) {
            logd("无效")
            finish()
        }
        setContentView(LayoutInflater.from(thisContext).inflate(R.layout.activity_setting, null, false))

        textViewVersion.text = QS_VERSION_NAME
    }
}