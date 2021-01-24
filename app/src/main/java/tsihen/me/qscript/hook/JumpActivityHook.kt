package tsihen.me.qscript.hook

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Instrumentation
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import dalvik.system.DexClassLoader
import dalvik.system.PathClassLoader
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import tsihen.me.qscript.activity.SettingActivity
import tsihen.me.qscript.util.*
import tsihen.me.qscript.util.Initiator.load
import java.lang.reflect.Method

class JumpActivityHook : AbsDelayableHook() {
    companion object {
        private val self = JumpActivityHook()
        fun get(): JumpActivityHook = self
    }

    override fun init(): Boolean {
        val jumpActivity = load(".activity.JumpActivity")
        if (jumpActivity == null) {
            loge("JumpActivity not found.")
            return false
        }
        val doOnCreate: Method = jumpActivity.getDeclaredMethod("doOnCreate", Bundle::class.java)
        XposedBridge.hookMethod(doOnCreate, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val thiz = param.thisObject as Activity
                initForActivity(thiz)
                val intent = thiz.intent
                val cmd: String? = intent.getStringExtra(JUMP_ACTION_CMD)
                if (intent == null || cmd == null) {
                    return
                }
                if (JUMP_ACTION_SETTING_ACTIVITY == cmd) {
                    val realIntent = Intent(intent)
                    realIntent.putExtra(JUMP_ACTION_CMD, JUMP_ACTION_SETTING_ACTIVITY)
                    realIntent.component = ComponentName(thiz, SettingActivity::class.java)
                    thiz.startActivity(realIntent)
                } else if (JUMP_ACTION_START_ACTIVITY == cmd) {
                    val target = intent.getStringExtra(JUMP_ACTION_TARGET)
                    if (!TextUtils.isEmpty(target)) {
                        try {
                            val activityClass = Class.forName(target!!)
                            val realIntent = Intent(intent)
                            realIntent.component = ComponentName(thiz, activityClass)
                            thiz.startActivity(realIntent)
                        } catch (e: Exception) {
                            logi("Unable to start Activity: $e")
                        }
                    }
                }
            }
        })
        return true
    }

    /**
     * 启动 Activity
     */
    @SuppressLint("PrivateApi")
    private fun initForActivity(ctx: Activity) {
        // 放入 activity
        try {
            val sCurrentActivityThreadThread = XposedHelpers.findField(
                Class.forName("android.app.ActivityThread"),
                "sCurrentActivityThread"
            )
            val activityThread = sCurrentActivityThreadThread[null]
            val mHField =
                XposedHelpers.findField(Class.forName("android.app.ActivityThread"), "mH")
            val mH = mHField[activityThread]
            val mCallbackField =
                XposedHelpers.findField(Class.forName("android.os.Handler"), "mCallback")
            mCallbackField[mH] = Handler.Callback { msg: Message ->
                when (msg.what) {
                    100 -> {
                        try {
                            val intentField =
                                XposedHelpers.findField(msg.obj.javaClass, "intent")
                            val proxyIntent = intentField[msg.obj] as Intent
                            val targetIntent =
                                proxyIntent.getParcelableExtra<Intent>("target_intent")
                            if (targetIntent != null) {
                                //                                    proxyIntent.setComponent(targetIntent.getComponent());
                                intentField[msg.obj] = targetIntent
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                    159 -> {
                        try {
                            val mActivityCallbacksField =
                                XposedHelpers.findField(msg.obj.javaClass, "mActivityCallbacks")
                            val mActivityCallbacks =
                                mActivityCallbacksField[msg.obj] as List<*>
                            var i = 0
                            while (i < mActivityCallbacks.size) {
                                if ("android.app.servertransaction.LaunchActivityItem"
                                    == mActivityCallbacks[i]?.javaClass?.name
                                ) {
                                    val launchActivityItem = mActivityCallbacks[i]!!
                                    val mIntentField =
                                        XposedHelpers.findField(
                                            launchActivityItem.javaClass,
                                            "mIntent"
                                        )
                                    val intent =
                                        mIntentField[launchActivityItem] as Intent
                                    // 获取插件的
                                    val proxyIntent: Intent? =
                                        intent.getParcelableExtra(JavaUtil.KEY_EXTRA_TARGET_INTENT)
                                    // 替换
                                    if (proxyIntent != null) {
                                        mIntentField[launchActivityItem] = proxyIntent
                                    }
                                }
                                i++
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                    else -> {
                    }
                }
                false
            } as Handler.Callback
        } catch (e: java.lang.Exception) {
            log(e)
        }

        val apkFile = JavaUtil.findApkFile(qqApplication, PACKAGE_NAME_SELF)
            ?: throw NullPointerException("ApkFile is null.")
        val loader = DexClassLoader(
            apkFile.absolutePath,
            ctx.getDir("dex", Context.MODE_PRIVATE).absolutePath,
            null,
            ctx.classLoader as PathClassLoader
        )
        JavaUtil.loadPlugin(loader, ctx)

        val instrumentation =
            MyInstrumentation(getObject(ctx, "mInstrumentation", Instrumentation::class.java)!!)
        setObject(ctx, "mInstrumentation", instrumentation)
    }

    override fun getEnabled(): Boolean = true

    override fun setEnabled(z: Boolean) {
        throw RuntimeException("You cannot change it!")
    }
}