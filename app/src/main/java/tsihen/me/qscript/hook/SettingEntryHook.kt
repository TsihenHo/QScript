package tsihen.me.qscript.hook

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import tsihen.me.qscript.activity.SettingActivity
import tsihen.me.qscript.util.*

class SettingEntryHook : AbsDelayableHook() {
    companion object {
        private val self = SettingEntryHook()
        fun get(): SettingEntryHook = self
    }

    private var inited: Boolean = false

    override fun init(): Boolean {
        if (inited) {
            return true
        }
        try {
            val qqSettingActivity = Initiator.load(".activity.QQSettingSettingActivity")
            XposedHelpers.findAndHookMethod(
                qqSettingActivity,
                "doOnCreate",
                Bundle::class.java,
                object : XC_MethodHook(52) {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        var itemClass: Class<*>? = null
                        var itemRef: View?
                        itemRef = getObject(
                            param.thisObject,
                            "a",
                            Initiator.load("com/tencent/mobileqq/widget/FormSimpleItem")
                        ) as? View?
                        if (itemRef == null
                            && Initiator.load("com/tencent/mobileqq/widget/FormCommonSingleLineItem")
                                .also { itemClass = it } != null
                        ) itemRef = getObject(param.thisObject, "a", itemClass) as View
                        val item: View = newInstance(
                            itemRef!!.javaClass,
                            param.thisObject,
                            Context::class.java
                        ) as View
                        item.callVisualMethod("setLeftText", "QScript", CharSequence::class.java)
                        item.callVisualMethod("setBgType", 2, Int::class.java)
                        item.callVisualMethod(
                            "setRightText",
                            QS_VERSION_NAME,
                            CharSequence::class.java
                        )
                        item.setOnLongClickListener {
                            Toasts.info(
                                param.thisObject as Activity,
                                "Nothing is here :)",
                                Toasts.LENGTH_SHORT
                            )
                            true
                        }
                        item.setOnClickListener {
                            (param.thisObject as Activity).startActivity<SettingActivity>()
//                            val intent = Intent(param.thisObject as Activity, Initiator.load(".activity.JumpActivity"))
//                            intent.putExtra(JUMP_ACTION_CMD, JUMP_ACTION_SETTING_ACTIVITY)
//                            (param.thisObject as Activity).startActivity(intent)
                        }
                        (itemRef.parent as ViewGroup).addView(
                            item,
                            0,
                            ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                        )
                    }
                })
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override fun getEnabled(): Boolean = true

    override fun setEnabled(z: Boolean) {
        throw RuntimeException("You cannot change it!")
    }
}