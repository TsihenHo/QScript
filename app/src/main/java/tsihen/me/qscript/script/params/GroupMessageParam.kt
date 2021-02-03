package tsihen.me.qscript.script.params

class GroupMessageParam {
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

    /**
     * 是否艾特自己（atAll时也会包括atMe）
     */
    var atMe = false

    /**
     * 是否艾特ALL
     */
    var atAll = false

    fun setSenderUin(uin: String?): GroupMessageParam {
        senderuin = uin
        return this
    }

    fun setSenderUin(uin: Long): GroupMessageParam {
        senderuin = uin.toString() + ""
        return this
    }

    fun setUin(uin: String?): GroupMessageParam {
        this.uin = uin
        return this
    }

    fun setUin(uin: Long): GroupMessageParam {
        this.uin = uin.toString() + ""
        return this
    }

    fun setContent(content: String?): GroupMessageParam {
        this.content = content
        return this
    }

    fun setAtMe(z: Boolean): GroupMessageParam {
        this.atMe = z
        return this
    }

    fun setAtAll(z: Boolean): GroupMessageParam {
        this.atAll = z
        return this
    }
    fun create(): GroupMessageParam {
        return this
    }
}