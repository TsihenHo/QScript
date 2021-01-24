package tsihen.me.qscript

import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tsihen.me.qscript.util.*

class StartupHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(p0: XC_LoadPackage.LoadPackageParam) {
        // Hook 自己
        if (p0.packageName == PACKAGE_NAME_SELF) {
            XposedHelpers.findAndHookMethod(
                "${PACKAGE_NAME_SELF}.util.Utils",
                p0.classLoader,
                "getActiveModuleVersion",
                XC_MethodReplacement.returnConstant(QS_VERSION_NAME)
            )
            return
        }
        // 不是 QQ\TIM 不 Hook
        if (p0.packageName != PACKAGE_NAME_QQ && p0.packageName != PACKAGE_NAME_TIM) {
            return
        }
        MainHook.getInstance().doInit(p0.classLoader)
    }
}