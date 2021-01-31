package tsihen.me.qscript.hook

abstract class AbsDelayableHook {
    companion object {
        private var sAllHooks: Array<AbsDelayableHook>? = null
        fun queryDelayableHooks(): Array<AbsDelayableHook> {
            if (sAllHooks == null) {
                sAllHooks =
                    arrayOf(SettingEntryHook.get(), JumpActivityHook.get(), ScriptEventHook.get())
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