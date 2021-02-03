package tsihen.me.qscript.util

object ClassFinder {
    fun findClass(id: Int): Class<*>? {
        return when (id) {
            C_BASE_CHAT_PIE ->
                Initiator.load("com/tencent/mobileqq/activity/aio/core/BaseChatPie")
                    ?: Initiator.load("com.tencent.mobileqq.activity.BaseChatPie")
            C_CHAT_ACTIVITY_FACADE ->
                Initiator.load(".activity.ChatActivityFacade")
            C_APP_INTERFACE_FACTORY ->
                Initiator.load("com.tencent.common.app.AppInterfaceFactory")
            C_QQ_APP_INTERFACE ->
                Initiator.load(".app.QQAppInterface")
            C_SESSION_INFO ->
                Initiator.load(".activity.aio.SessionInfo")
            C_MESSAGE_FOR_ARK_APP ->
                Initiator.load(".data.MessageForArkApp")
            else -> null
        }
    }
}