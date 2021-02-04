/* QScript - An Xposed module to run scripts on QQ
 * Copyright (C) 2021-20222 chinese.he.amber@gmail.com
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
package me.tsihen.qscript.hook

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