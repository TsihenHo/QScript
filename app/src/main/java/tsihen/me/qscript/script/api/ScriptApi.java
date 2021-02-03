package tsihen.me.qscript.script.api;

import tsihen.me.qscript.hook.SendMsgHook;
import tsihen.me.qscript.script.QScript;
import tsihen.me.qscript.util.Utils;

public class ScriptApi {
    private final QScript script;
    private ScriptApi(QScript qs) {
        // TODO 完成 API
        script = qs;
    }

    public static ScriptApi get(QScript qs) {
        return new ScriptApi(qs);
    }

    /**
     * 发送日志
     *
     * @param msg 日志内容
     */
    public void log(String msg) {
        Utils.logi("Script : " + script.getName() + " : " + msg);
    }

    /**
     * 给某人发送纯文本消息（如果没有添加好友，请 createTempConversation 后再发送消息）
     *
     * @param msg  消息内容
     * @param qNum 接受者的 QQ 号
     */
    public void sendTextMsg(String msg, long qNum, long ... at) {
        SendMsgHook.Companion.get().sendText(msg, qNum, at);
    }

    /**
     * 给某人发送纯文本消息（如果没有添加好友，请 createTempConversation 后再发送消息）
     *
     * @param msg  消息内容
     * @param qNum 接受者的 QQ 号
     */
    public void sendTextMsg(String msg, long qNum) {
        SendMsgHook.Companion.get().sendText(msg, qNum);
    }

    /**
     * 从群聊中创建临时会话
     *
     * @param qNum     对方的 QQ 号
     * @param groupNum 群聊的 QQ 号
     * @return 创建是否成功
     */
    public boolean createTempConversation(long qNum, long groupNum) {
        return true;
    }

    /**
     * 发送群文本消息
     * @param atAll 是否艾特全体成员（如果您没有管理员权限，将发送失败）
     * @return 是否成功
     */
    public boolean sendGroupTextMsg(String msg, long groupNum, boolean atAll) {
        return true;
    }

    /**
     * 发送群文本消息并@一些人
     */
    public boolean sendGroupTextMsgAndAt(String msg, long groupNum, long ... at) {
        return true;
    }
}
