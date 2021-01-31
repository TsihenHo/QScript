package tsihen.me.qscript.script.params

object ParamFactory {
    /**
     * 构建一个好友消息参数对象
     *
     * @return [FriendTextMessageParam]
     */
    fun friendMessage(): FriendTextMessageParam {
        return FriendTextMessageParam()
    }

    /**
     * 构建一个好友请求参数对象
     *
     * @return [FriendRequestParam]
     */
    fun friendRequest(): FriendRequestParam {
        return FriendRequestParam()
    }

    /**
     * 构建一个好友添加完毕参数对象
     *
     * @return [FriendAddedParam]
     */
    fun friendAdded(): FriendAddedParam {
        return FriendAddedParam()
    }

    /**
     * 构建一个群消息参数对象
     *
     * @return [GroupTextMessageParam]
     */
    fun groupMessage(): GroupTextMessageParam {
        return GroupTextMessageParam()
    }

    /**
     * 构建一个入群请求参数对象
     *
     * @return [GroupRequestParam]
     */
    fun groupRequest(): GroupRequestParam {
        return GroupRequestParam()
    }

    /**
     * 构建一个成员入群参数对象
     *
     * @return [GroupJoinedParam]
     */
    fun groupJoined(): GroupJoinedParam {
        return GroupJoinedParam()
    }
}