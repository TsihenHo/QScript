/* QScript - An Xposed module to run scripts on QQ
 * Copyright (C) 2021-2022 chinese.he.amber@gmail.com
 * https://github.com/GoldenHuaji/QScript
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
@file:Suppress("UNUSED")

package me.tsihen.qscript.util

import me.tsihen.qscript.BuildConfig

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
const val PACKAGE_NAME_SELF = "me.tsihen.qscript"
const val PACKAGE_NAME_XPOSED_INSTALLER = "de.robv.android.xposed.installer"

// Commands
const val STUB_DEFAULT_ACTIVITY = "com.tencent.mobileqq.activity.SplashActivity"
const val STUB_TRANSLUCENT_ACTIVITY = "cooperation.qlink.QlinkStandardDialogActivity"
const val ACTIVITY_PROXY_INTENT = "qs_act_proxy_intent"
const val JUMP_ACTION_CMD = "qs_jump_action_cmd"
const val JUMP_ACTION_TARGET = "qs_jump_action_target"
const val JUMP_ACTION_START_ACTIVITY = "tsihen.me.me.tsihen.qscript.START_ACTIVITY"
const val JUMP_ACTION_SETTING_ACTIVITY = "tsihen.me.me.tsihen.qscript.SETTING_ACTIVITY"
const val JUMP_ACTION_CHECK_ACTIVITY = "tsihen.me.me.tsihen.qscript.CHECK_ACTIVITY"
const val QS_FULL_TAG = "qscript_full_tag"
const val QS_LOG_TAG = "QSDump"

// Files
const val FILE_DEFAULT_CONFIG = 1
const val FILE_CACHE = 2
const val FILE_UIN_DATA = 3

/**
 * For [ClassFinder]
 */
const val C_BASE_CHAT_PIE = 0
const val C_CHAT_ACTIVITY_FACADE = 1
const val C_APP_INTERFACE_FACTORY = 2
const val C_QQ_APP_INTERFACE = 3
const val C_SESSION_INFO = 4
const val C_MESSAGE_FOR_ARK_APP = 5
const val C_TROOP_MEMBER_INFO = 6
const val C_SEND_MSG_PARAMS = 7
const val C_MESSAGE_RECORD = 8
const val C_MESSAGE_FACTORY = 9

// MsgTypes
const val MSG_TYPE_TIP = -1013
const val MSG_TYPE_SHAKE = -2020