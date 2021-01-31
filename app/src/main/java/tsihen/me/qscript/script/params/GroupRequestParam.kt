package tsihen.me.qscript.script.params

class GroupRequestParam {
    /**
     * 请求id
     */
    var senderuin: Long = 0

    /**
     * 群id
     */
    var uin: Long = 0

    /**
     * 验证消息
     */
    var content: String? = null
    fun setUin(uin: Long): GroupRequestParam {
        this.uin = uin
        return this
    }

    fun setSenderUin(uin: Long): GroupRequestParam {
        senderuin = uin
        return this
    }

    fun setContent(content: String?): GroupRequestParam {
        this.content = content
        return this
    }

    fun create(): GroupRequestParam {
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