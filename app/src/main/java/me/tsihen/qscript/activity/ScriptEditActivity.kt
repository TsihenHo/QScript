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
import android.app.AlertDialog
import android.os.Bundle
import me.tsihen.qscript.R
import me.tsihen.qscript.config.ConfigManager
import me.tsihen.qscript.databinding.ActivityScriptEditBinding
import me.tsihen.qscript.script.qscript.QScript
import me.tsihen.qscript.script.qscript.QScriptManager
import me.tsihen.qscript.util.Toasts
import me.tsihen.qscript.util.log
import me.tsihen.qscript.util.loge


class ScriptEditActivity : AbsActivity() {
    private lateinit var mViewBinding: ActivityScriptEditBinding
    private lateinit var script: QScript

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = ActivityScriptEditBinding.inflate(layoutInflater)
        setContentView(mViewBinding.root)
        mViewBinding.topAppBar.setNavigationOnClickListener { finish() }
        if (!intent.hasExtra("script_label")) {
            loge("ScriptEditActivity : 无效参数")
            finish()
        }
        val label = intent.getStringExtra("script_label")!!
        var qs: QScript? = null
        for (it in QScriptManager.getScripts()) {
            if (it.getLabel() != label) continue
            qs = it
            break
        }
        if (qs == null) {
            loge("ScriptEditActivity : 无效参数")
            finish()
            return
        }
        if (label == "qscript-demo") {
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
            mViewBinding.tvReadOnly.text =
                "温馨提示：您不应该更改“MeteData”中的内容（Version 除外）\n警告：这个编辑器做的像坨狗屎一样。建议您使用外部文本编辑器编辑。"
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
        if (qs.getCode().length > 1_0000_0000) {
            Toasts.error(this, "脚本过大")
        } else {
            mViewBinding.editTextTextMultiLine.setText(qs.getCode())
        }
        script = qs
    }

    override fun onStart() {
        super.onStart()
        if (script.getCode().length > 1_0000_0000) {
            AlertDialog.Builder(this)
                .setMessage("脚本文件过大，使用内置文本编辑器很可能造成卡顿甚至闪退。您真的要继续吗？我们建议您使用外部文本编辑器编辑。脚本的路径：" +
                        "${ConfigManager.getFileDirPath(this)}/files/QScript/qs_scripts/...")
                .setPositiveButton("关闭") { d, _ ->
                    d.dismiss()
                    finish()
                }
                .setNeutralButton("仍然继续") { _, _ ->
                    mViewBinding.editTextTextMultiLine.setText(script.getCode())
                }
                .show()
        }
    }
}