package me.tsihen.qscript.script.bugscript

import me.tsihen.qscript.script.qscript.objects.MessageData

class BugMessageData : MessageData() {
    var nickName: String = ""
    var sendUin: String = ""

    companion object {
        @JvmStatic
        fun from(data: MessageData) = BugMessageData().apply {
            nickName = data.nickname
            sendUin = data.senderUin
        }
    }
}