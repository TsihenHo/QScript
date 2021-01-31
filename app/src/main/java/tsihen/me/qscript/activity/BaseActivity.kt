package tsihen.me.qscript.activity

import android.app.Activity
import android.content.ComponentName
import android.os.Bundle
import dalvik.system.PathClassLoader
import tsihen.me.qscript.R
import tsihen.me.qscript.util.*
import tsihen.me.qscript.util.JUMP_ACTION_SETTING_ACTIVITY

open class BaseActivity : Activity() {
    override fun getComponentName(): ComponentName =
        ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.SplashActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDebugMode()
        setTheme(R.style.Theme_MyTheme)
        Initiator.init(this.classLoader)
        if (intent.getStringExtra(JUMP_ACTION_CMD) != JUMP_ACTION_CHECK_ACTIVITY) {
            logd("无效启动")
            finish()
        }
    }
}