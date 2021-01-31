@file:Suppress("UNUSED")
package tsihen.me.qscript.util

import tsihen.me.qscript.BuildConfig

// Version
const val QS_VERSION_NAME: String = BuildConfig.VERSION_NAME
const val QS_VERSION_CODE: Int = BuildConfig.VERSION_CODE
const val TAICHI_NOT_INSTALL = 0
const val TAICHI_NOT_ACTIVE = 1
const val TAICHI_ACTIVE = 2

// Packages
const val PACKAGE_NAME_QQ = "com.tencent.mobileqq"
const val PACKAGE_NAME_QQ_INTERNATIONAL = "com.tencent.mobileqqi"
const val PACKAGE_NAME_QQ_LITE = "com.tencent.qqlite"
const val PACKAGE_NAME_TIM = "com.tencent.tim"
const val PACKAGE_NAME_SELF = "tsihen.me.qscript"
const val PACKAGE_NAME_XPOSED_INSTALLER = "de.robv.android.xposed.installer"

// Commands
const val STUB_DEFAULT_ACTIVITY = "com.tencent.mobileqq.activity.photo.CameraPreviewActivity";
const val STUB_TRANSLUCENT_ACTIVITY = "cooperation.qlink.QlinkStandardDialogActivity";
const val ACTIVITY_PROXY_INTENT = "qs_act_proxy_intent"
const val JUMP_ACTION_CMD = "qs_jump_action_cmd"
const val JUMP_ACTION_TARGET = "qs_jump_action_target"
const val JUMP_ACTION_START_ACTIVITY = "tsihen.me.qscript.START_ACTIVITY"
const val JUMP_ACTION_SETTING_ACTIVITY = "tsihen.me.qscript.SETTING_ACTIVITY"
const val JUMP_ACTION_CHECK_ACTIVITY = "tsihen.me.qscript.CHECK_ACTIVITY"
const val QS_FULL_TAG = "qscript_full_tag"
const val QS_LOG_TAG = "QSDump"

// Files
const val FILE_DEFAULT_CONFIG = 1
const val FILE_CACHE = 2
const val FILE_UIN_DATA = 3
