package me.tsihen.qscript.activity

import android.os.Bundle
import me.tsihen.qscript.R
import me.tsihen.qscript.databinding.ActivityAdditionalFunctionsBinding
import me.tsihen.qscript.hook.NoMiniappHook
import me.tsihen.qscript.ui.IOnClickListenerFilled
import me.tsihen.qscript.ui.ViewFilledWithTwoLinesAndImage

class AdditionalFunctionsActivity : BaseActivity(), IOnClickListenerFilled {
    private lateinit var mViewBinding: ActivityAdditionalFunctionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = ActivityAdditionalFunctionsBinding.inflate(layoutInflater)
        setContentView(mViewBinding.root)

        mViewBinding.topAppBar.setNavigationOnClickListener { finish() }
        mViewBinding.noMiniapp.setOnClickListener(this)
        refresh()
    }

    private fun refresh() {
        mViewBinding.noMiniapp.changeColor(NoMiniappHook.get().getEnabled())
    }

    override fun onClick(v: ViewFilledWithTwoLinesAndImage) {
        when (v.id) {
            R.id.no_miniapp -> NoMiniappHook.get().changeEnabled()
        }
        refresh()
    }
}