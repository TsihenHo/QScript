package tsihen.me.qscript.script.api;

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
     * @return 是否成功
     */
    public boolean sendTextMsg(String msg, long qNum) {
        return true;
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
     *
     * @return 是否成功
     */
    public boolean sendGroupTextMsg(String msg, long groupNum) {
        return true;
    }
}
