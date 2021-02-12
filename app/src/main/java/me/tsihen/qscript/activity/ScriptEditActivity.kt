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
import me.tsihen.qscript.R
import me.tsihen.qscript.databinding.ActivityScriptEditBinding
import me.tsihen.qscript.script.QScript
import me.tsihen.qscript.script.QScriptManager
import me.tsihen.qscript.util.Toasts
import me.tsihen.qscript.util.log
import me.tsihen.qscript.util.loge


class ScriptEditActivity : BaseActivity() {
    private lateinit var mViewBinding: ActivityScriptEditBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = ActivityScriptEditBinding.inflate(layoutInflater)
        setContentView(mViewBinding.root)
        mViewBinding.topAppBar.setNavigationOnClickListener { finish() }
        var qs: QScript? = null
        try {
            qs = QScriptManager.execute(intent.getStringExtra("script_code"))
        } catch (e: Exception) {
            loge("ScriptEditActivity : 无效参数")
            log(e)
            finish()
        }
        qs!!
        val label = qs.getLabel()
        mViewBinding.editTextTextMultiLine.setText(qs.getCode())
        if (label == "me.tsihen.qscript-demo") {
            mViewBinding.editTextTextMultiLine.keyListener = null
            mViewBinding.topAppBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_item_save -> {
                        Toasts.error(this, "这是只读脚本")
                        true
                    }
                    else -> super.onOptionsItemSelected(it)
                }
            }
        } else {
            mViewBinding.tvReadOnly.text = "温馨提示：您不应该更改“MeteData”中的内容（Version 除外）"
            mViewBinding.topAppBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_item_save -> {
                        val code = mViewBinding.editTextTextMultiLine.text?.toString()
                        try {
                            val finalScript = QScriptManager.execute(code)
                            QScriptManager.replaceScript(qs, finalScript)
                            Toasts.success(this, "成功")
                        } catch (e: RuntimeException) {
                            Toasts.error(this, e.message ?: "未知错误")
                            log(e)
                        }
                        true
                    }
                    else -> super.onOptionsItemSelected(it)
                }
            }
        }
    }
}