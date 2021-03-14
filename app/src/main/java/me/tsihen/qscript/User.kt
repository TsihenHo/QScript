package me.tsihen.qscript

import me.tsihen.qscript.util.getLongAccountUin

data class User(
    val uin: Long,
) {
    var blocked = false

    fun init() = null

    companion object {
        val thisUser: User by lazy { User(getLongAccountUin()) }
    }
}
