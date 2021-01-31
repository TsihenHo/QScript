package tsihen.me.qscript.script.params

class FriendRequestParam {
    /**
     * 好友id
     */
    var uin: Long = 0

    /**
     * 验证消息
     */
    var content: String? = null
    fun setUin(uin: Long): FriendRequestParam {
        this.uin = uin
        return this
    }

    fun setContent(content: String?): FriendRequestParam {
        this.content = content
        return this
    }

    fun create(): FriendRequestParam {
        return this
    }

    /**
     * 接受请求
     */
    fun accept() {
        // to do
    }

    /**
     * 拒绝请求
     */
    fun refuse() {
        // to do
    }
}