package tsihen.me.qscript.script

import tsihen.me.qscript.script.params.*
import tsihen.me.qscript.util.logd

object QScriptEventSender {
    /**
     * 广播群消息事件
     *
     * @param param 使用[ParamFactory]构建的param对象
     */
    fun broadcastGroupMessage(param: GroupMessageParam) {
        for (qs in QScriptManager.getScripts()) {
            if (!qs.isEnable()) continue
            qs.onGroupMessage(param)
        }
    }

    /**
     * 广播好友消息事件
     *
     * @param param 使用[ParamFactory]构建的param对象
     */
    fun broadcastFriendMessage(param: FriendMessageParam) {
        for (qs in QScriptManager.getScripts()) {
            if (!qs.isEnable()) continue
            qs.onFriendMessage(param)
        }
    }

    /**
     * 广播好友请求事件
     *
     * @param param 使用[ParamFactory]构建的param对象
     */
    fun broadcastFriendRequest(param: FriendRequestParam) {
        for (qs in QScriptManager.getScripts()) {
            if (!qs.isEnable()) continue
            qs.onFriendRequest(param)
        }
    }

    /**
     * 广播好友添加完毕事件
     *
     * @param param 使用[ParamFactory]构建的param对象
     */
    fun broadcastFriendAdded(param: FriendAddedParam) {
        for (qs in QScriptManager.getScripts()) {
            if (!qs.isEnable()) continue
            qs.onFriendAdded(param)
        }
    }

    /**
     * 广播入群请求事件
     *
     * @param param 使用[ParamFactory]构建的param对象
     */
    fun broadcastGroupRequest(param: GroupRequestParam) {
        for (qs in QScriptManager.getScripts()) {
            if (!qs.isEnable()) continue
            qs.onGroupRequest(param)
        }
    }

    /**
     * 广播成员入群事件
     *
     * @param param 使用[ParamFactory]构建的param对象
     */
    fun broadcastGroupJoined(param: GroupJoinedParam) {
        for (qs in QScriptManager.getScripts()) {
            if (!qs.isEnable()) continue
            qs.onGroupJoined(param)
        }
    }
}
