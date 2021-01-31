package tsihen.me.qscript.activity

import android.annotation.SuppressLint
import android.os.Bundle
import tsihen.me.qscript.databinding.ActivityScriptHelpBinding

class ScriptHelpActivity : BaseActivity() {
    private lateinit var mViewBinding: ActivityScriptHelpBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = ActivityScriptHelpBinding.inflate(layoutInflater)
        setContentView(mViewBinding.root)
        mViewBinding.tv.text = """
            |1. 如何删除脚本？
            |   当脚本没有开启时，长按这个脚本。
            |2. 如何更改脚本？
            |   当脚本开启时，长按这个脚本。
            |3. 请查看“QScript 脚本示例”或“Github Wiki”
        """.trimMargin("|")
        mViewBinding.topAppBar.setNavigationOnClickListener { finish() }
    }
}