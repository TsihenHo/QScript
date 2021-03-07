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
package me.tsihen.qscript.script.bugscript

data class TextData(
    override val type: Int,
    override val atMe: Boolean,
    val content: String,
    override val friendUin: String,
    override val isGroup: Boolean,
    override val nickName: String,
    override val senderUin: String,
    override val time: Long,
    override val atList: Array<String>,
) : Data(type, atMe, friendUin, isGroup, nickName, senderUin, time, atList) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TextData

        if (type != other.type) return false
        if (atMe != other.atMe) return false
        if (content != other.content) return false
        if (friendUin != other.friendUin) return false
        if (isGroup != other.isGroup) return false
        if (nickName != other.nickName) return false
        if (senderUin != other.senderUin) return false
        if (time != other.time) return false
        if (!atList.contentEquals(other.atList)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + atMe.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + friendUin.hashCode()
        result = 31 * result + isGroup.hashCode()
        result = 31 * result + nickName.hashCode()
        result = 31 * result + senderUin.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + atList.contentHashCode()
        return result
    }
}