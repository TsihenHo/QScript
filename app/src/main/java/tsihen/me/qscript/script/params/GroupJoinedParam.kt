package tsihen.me.qscript.script.params

class GroupJoinedParam {
    /**
     * 群id
     */
    var uin: Long = 0

    /**
     * 群员id
     */
    var senderuin: Long = 0
    fun setUin(uin: Long): GroupJoinedParam {
        this.uin = uin
        return this
    }

    fun setSenderUin(uin: Long): GroupJoinedParam {
        senderuin = uin
        return this
    }

    fun create(): GroupJoinedParam {
        return this
    }
}