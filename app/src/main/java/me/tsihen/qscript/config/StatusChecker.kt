package me.tsihen.qscript.config

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import me.tsihen.qscript.util.getApplicationNonNull


object StatusChecker {
    private lateinit var mgr: ConfigManager
    private lateinit var pref: SharedPreferences
    private lateinit var checkNumber: String

    init {
        try {
            mgr = ConfigManager.tryGetDefaultConfig()!!
            pref = getApplicationNonNull().getSharedPreferences("data", MODE_PRIVATE)

            checkNumber = if (!pref.contains("qs__check_int")) {
                val checkInt = Math.random().toString()
                val editor = pref.edit()
                editor.putString("qs__check_int", checkInt)
                editor.apply()
                checkInt
            } else {
                pref.getString("qs__check_int", "not happen").toString()
            }

        } catch (ignored: Exception) {
        }
    }

    fun getCheckNum() = checkNumber
    fun passByExam(): Boolean = mgr.getOrDefault("pass_by_exam", false)
    fun enableRepeaterScript(): Boolean {
        val checkInt = pref.getString("qs__check_int", "nothing") ?: "nothing"
        return mgr.getOrDefault("run_repeater_script_$checkInt", false)
    }
}