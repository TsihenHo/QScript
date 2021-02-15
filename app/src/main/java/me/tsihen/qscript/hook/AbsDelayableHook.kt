/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
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
package me.tsihen.qscript.hook

import me.tsihen.qscript.util.FromQNotified

@FromQNotified
abstract class AbsDelayableHook {
    companion object {
        private var sAllHooks: Array<AbsDelayableHook>? = null
        fun queryDelayableHooks(): Array<AbsDelayableHook> {
            if (sAllHooks == null) {
                sAllHooks =
                    arrayOf(
                        SettingEntryHook.get(),
                        JumpActivityHook.get(),
                        SendMsgHook.get(),
                        GetMsgHook.get(),
                        OnJoinHook.get(),
                        ScriptEventHook.get()
                    )
            }
            return sAllHooks!!
        }

        fun getHookByType(hookId: Int): AbsDelayableHook? {
            return queryDelayableHooks()[hookId]
        }
    }

    abstract fun init(): Boolean
    abstract fun getEnabled(): Boolean
    abstract fun setEnabled(z: Boolean)
}