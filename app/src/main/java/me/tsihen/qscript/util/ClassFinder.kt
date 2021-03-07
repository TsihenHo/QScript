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
package me.tsihen.qscript.util

object ClassFinder {
    /**
     * Just a link to ConstsKt
     * [C_BASE_CHAT_PIE]
     */
    fun findClass(id: Int): Class<*>? {
        return when (id) {
            C_BASE_CHAT_PIE ->
                Initiator.load("com/tencent/mobileqq/activity/aio/core/BaseChatPie")
                    ?: Initiator.load("com.tencent.mobileqq.activity.BaseChatPie")
            C_CHAT_ACTIVITY_FACADE ->
                Initiator.load(".activity.ChatActivityFacade")
            C_APP_INTERFACE_FACTORY ->
                Initiator.load("com.tencent.common.app.AppInterfaceFactory")
            C_QQ_APP_INTERFACE ->
                Initiator.load(".app.QQAppInterface")
            C_SESSION_INFO ->
                Initiator.load(".activity.aio.SessionInfo")
            C_MESSAGE_FOR_ARK_APP ->
                Initiator.load(".data.MessageForArkApp")
            C_TROOP_MEMBER_INFO ->
                Initiator.load(".data.troop.TroopMemberInfo")
                    ?: Initiator.load(".data.TroopMemberInfo")
            C_MESSAGE_RECORD ->
                Initiator.load(".data.MessageRecord")
            else -> null
        }
    }
}