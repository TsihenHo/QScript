package tsihen.me.qscript.script.params

class FriendAddedParam {
    /**
     * 好友id
     */
    var uin: Long = 0
    fun setUin(uin: Long): FriendAddedParam {
        this.uin = uin
        return this
    }

    fun create(): FriendAddedParam {
        return this
    }
}