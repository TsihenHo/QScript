package tsihen.me.qscript.script.params

class FriendMessageParam {
    /**
     * 好友id
     */
    var uin: String? = null

    /**
     * 消息内容
     */
    var content: String? = null
    fun setUin(uin: String?): FriendMessageParam {
        this.uin = uin
        return this
    }

    fun setUin(uin: Long): FriendMessageParam {
        this.uin = uin.toString() + ""
        return this
    }

    fun setContent(content: String?): FriendMessageParam {
        this.content = content
        return this
    }

    fun create(): FriendMessageParam {
        return this
    }
}