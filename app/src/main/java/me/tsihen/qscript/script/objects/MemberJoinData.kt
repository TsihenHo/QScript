package me.tsihen.qscript.script.objects

class MemberJoinData {
    var uin: String = ""

    var groupUin: String = ""

    companion object {
        fun build(uin: String, groupUin: String): MemberJoinData {
            val res = MemberJoinData()
            res.uin = uin
            res.groupUin = groupUin
            return res
        }
    }
}