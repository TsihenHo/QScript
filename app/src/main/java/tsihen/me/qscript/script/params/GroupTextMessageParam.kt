package tsihen.me.qscript.script.params

class GroupTextMessageParam {
    /**
     * 发送者id
     */
    var senderuin: String? = null

    /**
     * 群id
     */
    var uin: String? = null

    /**
     * 消息内容
     */
    var content: String? = null
    fun setSenderUin(uin: String?): GroupTextMessageParam {
        senderuin = uin
        return this
    }

    fun setSenderUin(uin: Long): GroupTextMessageParam {
        senderuin = uin.toString() + ""
        return this
    }

    fun setUin(uin: String?): GroupTextMessageParam {
        this.uin = uin
        return this
    }

    fun setUin(uin: Long): GroupTextMessageParam {
        this.uin = uin.toString() + ""
        return this
    }

    fun setContent(content: String?): GroupTextMessageParam {
        this.content = content
        return this
    }

    fun create(): GroupTextMessageParam {
        return this
    }
}