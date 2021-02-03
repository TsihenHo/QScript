package tsihen.me.qscript.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import tsihen.me.qscript.R
import tsihen.me.qscript.databinding.ActivityScriptManageBinding
import tsihen.me.qscript.script.QScriptManager
import tsihen.me.qscript.ui.IOnClickListenerFilled
import tsihen.me.qscript.ui.IOnLongClickListenerFilled
import tsihen.me.qscript.ui.ViewFilledWithTwoLinesAndImage
import tsihen.me.qscript.util.*


class ScriptManageActivity : BaseActivity() {
    private lateinit var mViewBinding: ActivityScriptManageBinding
    private val codeAddScript = 100
    private val codeEditScript = 101

    @SuppressLint("InflateParams", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = ActivityScriptManageBinding.inflate(layoutInflater)
        setContentView(mViewBinding.root)
        mViewBinding.topAppBar.setNavigationOnClickListener { finish() }
        mViewBinding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_item_newScript -> {
                    Toasts.info(this, "请选择脚本（*.java）以导入")
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.type = "text/x-java"
                    startActivityForResult(intent, codeAddScript)
                }
                R.id.menu_item_help -> startActivity<ScriptHelpActivity>()
                else -> return@setOnMenuItemClickListener super.onOptionsItemSelected(it)
            }
            return@setOnMenuItemClickListener true
        }
        refresh()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == codeEditScript) {
            refresh()
            return
        }
        if (resultCode != RESULT_OK) return
        if (requestCode == codeAddScript) {
            if (data == null) {
                Toasts.error(this, "错误：没有选择文件")
            }
            if (data?.data == null) {
                Toasts.error(this, "错误：内部错误")
            }
            val uri = data?.data ?: return

            val resolver = this.contentResolver
            val c = resolver.query(uri, null, null, null, null)
            if (c == null) {
                val path = uri.path!!
                try {
                    QScriptManager.addScript(path)
                    refresh()
                    Toasts.success(this, "添加完毕")
                } catch (e: java.lang.Exception) {
                    log(e)
                    Toasts.error(this, "错误: " + e.message)
                }
            } else {
                if (c.moveToFirst()) {
                    val scriptName = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    try {
                        val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
                        if (parcelFileDescriptor != null) {
                            val fileDescriptor = parcelFileDescriptor.fileDescriptor
                            val err: String = QScriptManager.addScriptFD(fileDescriptor, scriptName)
                            if (err.isEmpty()) {
                                Toasts.success(this, "添加完毕")
                                refresh()
                            } else {
                                Toasts.error(this, err)
                            }
                        }
                    } catch (e: Throwable) {
                        log(e)
                        Toasts.error(this, "错误：" + e.message)
                    }
                }
                c.close()
            }
        }
    }

    private fun refresh() {
        mViewBinding.scriptRoot.removeAllViews()
        val lp = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        lp.setMargins(0, 0, 0, 16)
        QScriptManager.getScripts().forEach {
            try {
                val view = ViewFilledWithTwoLinesAndImage(this, null)
                view.title = it.getName()
                view.desc = "作者：${it.getAuthor()}\n版本：${it.getVersion()}\n${it.getDesc()}"
                view.changeColor(it.isEnable())
                view.image = R.drawable.ic_file
                view.setOnClickListener(object : IOnClickListenerFilled {
                    override fun onClick(v: ViewFilledWithTwoLinesAndImage) {
                        try {
                            it.setEnable(!it.isEnable())
                            QScriptManager.addEnable()
                            view.changeColor(it.isEnable())
                        } catch (t: Throwable) {
                            log(t)
                            Toasts.error(this@ScriptManageActivity, "错误")
                        }
                    }
                })
                view.setOnLongClickListener(object : IOnLongClickListenerFilled {
                    override fun onLongClick(v: ViewFilledWithTwoLinesAndImage): Boolean {
                        if (!it.isEnable()) {
                            if (it.getLabel() == "qscript-demo") {
                                Toasts.error(this@ScriptManageActivity, "您不能删除自带脚本")
                            } else {
                                AlertDialog.Builder(this@ScriptManageActivity)
                                    .setTitle("确定删除这个脚本吗？")
                                    .setPositiveButton("确定") { _, _ ->
                                        if (!QScriptManager.delScript(it)) {
                                            Toasts.error(this@ScriptManageActivity, "错误")
                                            return@setPositiveButton
                                        }
                                        Toasts.success(this@ScriptManageActivity, "成功")
                                        refresh()
                                    }
                                    .setNegativeButton("取消") { d, _ -> d.dismiss() }
                                    .show()
                            }
                        } else {
                            val intent = Intent(
                                this@ScriptManageActivity,
                                Initiator.load(".activity.JumpActivity")
                            )
                            intent.putExtra(JUMP_ACTION_CMD, JUMP_ACTION_START_ACTIVITY)
                                .putExtra(JUMP_ACTION_TARGET, ScriptEditActivity::class.java.name)
                                .putExtra("script_code", it.getCode())

                            this@ScriptManageActivity.startActivityForResult(intent, codeEditScript)
                        }
                        return true
                    }
                })
                view.layoutParams = lp
                mViewBinding.scriptRoot.addView(view)
            } catch (t: Throwable) {
                log(t)
            }
        }
    }
}