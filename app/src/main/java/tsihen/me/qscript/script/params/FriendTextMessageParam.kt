package tsihen.me.qscript.script.params

class FriendTextMessageParam {
    /**
     * 好友id
     */
    var uin: String? = null

    /**
     * 消息内容
     */
    var content: String? = null
    fun setUin(uin: String?): FriendTextMessageParam {
        this.uin = uin
        return this
    }

    fun setUin(uin: Long): FriendTextMessageParam {
        this.uin = uin.toString() + ""
        return this
    }

    fun setContent(content: String?): FriendTextMessageParam {
        this.content = content
        return this
    }

    fun create(): FriendTextMessageParam {
        return this
    }
}