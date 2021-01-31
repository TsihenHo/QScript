package tsihen.me.qscript.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import tsihen.me.qscript.R
import tsihen.me.qscript.databinding.ActivityScriptEditBinding
import tsihen.me.qscript.script.QScript
import tsihen.me.qscript.script.QScriptManager
import tsihen.me.qscript.util.Toasts
import tsihen.me.qscript.util.log
import tsihen.me.qscript.util.loge


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
            mViewBinding.tvReadOnly.text = "温馨提示：您不应该更改“MeteData”中的内容（Version 除外）"
            mViewBinding.topAppBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_item_save -> {
                        val code = mViewBinding.editTextTextMultiLine.text?.toString()
                        try {
                            val finalScript = QScriptManager.execute(code)
                            QScriptManager.replaceScript(
                                qs, finalScript
                            )
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

    override fun finish() {
        setResult(Activity.RESULT_OK)
        super.finish()
    }

    override fun onDestroy() {
        setResult(Activity.RESULT_OK)
        super.onDestroy()
    }
}