package tsihen.me.qscript

import android.content.Context

class MainHook {
    companion object {
        var SELF: MainHook? = null
        fun getInstance(): MainHook {
            if (SELF == null) SELF = MainHook()
            return SELF!!
        }
    }

    fun performHook(ctx: Context, step: Any) {
        TODO()
    }
}