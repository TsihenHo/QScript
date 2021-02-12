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
package me.tsihen.qscript.activity

import android.annotation.SuppressLint
import android.os.Bundle
import me.tsihen.qscript.databinding.ActivityScriptHelpBinding

class ScriptHelpActivity : BaseActivity() {
    private lateinit var mViewBinding: ActivityScriptHelpBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = ActivityScriptHelpBinding.inflate(layoutInflater)
        setContentView(mViewBinding.root)
        mViewBinding.tv.text = """
            |1. 如何删除脚本？
            |    当脚本没有开启时，长按这个脚本。
            |2. 如何更改脚本？
            |    当脚本开启时，长按这个脚本。
            |3. 请查看“QScript 脚本示例”或“Github Wiki”
        """.trimMargin("|")
        mViewBinding.topAppBar.setNavigationOnClickListener { finish() }
    }
}