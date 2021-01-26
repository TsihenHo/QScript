@file:Suppress("UNUSED")
package tsihen.me.qscript.util

import tsihen.me.qscript.BuildConfig

const val DEBUG_MODE = true

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
const val JUMP_ACTION_CMD = "qs_jump_action_cmd"
const val JUMP_ACTION_TARGET = "qs_jump_action_target"
const val JUMP_ACTION_START_ACTIVITY = "tsihen.me.qscript.START_ACTIVITY"
const val JUMP_ACTION_SETTING_ACTIVITY = "tsihen.me.qscript.SETTING_ACTIVITY"
const val JUMP_ACTION_CHECK_ACTIVITY = "tsihen.me.qscript.CHECK_ACTIVITY"
const val JUMP_ACTION_REQUEST_SKIP_DIALOG = "tsihen.me.qscript.REQUEST_SKIP_DIALOG"
const val QS_FULL_TAG = "qscript_full_tag"
const val QS_LOG_TAG = "QSDump"

// Sync
const val PROC_ERROR = 0
const val PROC_MAIN = 1
const val PROC_MSF = 1 shl 1
const val PROC_PEAK = 1 shl 2
const val PROC_TOOL = 1 shl 3
const val PROC_QZONE = 1 shl 4
const val PROC_VIDEO = 1 shl 5
const val PROC_MINI = 1 shl 6
const val PROC_LOLA = 1 shl 7
const val PROC_OTHERS = 1 shl 31
const val PROC_ANY = -0x1
//file=0
const val SYNC_FILE_CHANGED = "tsihen.me.qscript.SYNC_FILE_CHANGED"
//process=010001 hook=0011000
const val HOOK_DO_INIT = "tsihen.me.qscript.HOOK_DO_INIT"
const val ENUM_PROC_REQ = "tsihen.me.qscript.ENUM_PROC_REQ"
const val ENUM_PROC_RESP = "tsihen.me.qscript.ENUM_PROC_RESP"
const val GENERIC_WRAPPER = "tsihen.me.qscript.GENERIC_WRAPPER"
const val _REAL_INTENT = "__real_intent"
