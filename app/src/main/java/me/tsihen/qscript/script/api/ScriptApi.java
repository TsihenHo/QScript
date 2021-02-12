/* QScript - An Xposed module to run scripts on QQ
 * Copyright (C) 2021-2022 chinese.he.amber@gmail.com
 * https://github.com/GoldenHuaji/QScript
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package me.tsihen.qscript.script.api;

import me.tsihen.qscript.config.ConfigManager;
import me.tsihen.qscript.hook.SendMsgHook;
import me.tsihen.qscript.script.QScript;
import me.tsihen.qscript.script.objects.MessageData;
import me.tsihen.qscript.util.ClassUtils;
import me.tsihen.qscript.util.Initiator;
import me.tsihen.qscript.util.QQFields;
import me.tsihen.qscript.util.Utils;

import static me.tsihen.qscript.util.Utils.loge;

@SuppressWarnings("unused")
public class ScriptApi {
    private final QScript script;

    private ScriptApi(QScript qs) {
        script = qs;
    }

    public static ScriptApi get(QScript qs) {
        return new ScriptApi(qs);
    }

    /**
     * String2Long
     */
    public long str2long(String str) {
        return Long.parseLong(str);
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
     * 根据 data 发送消息
     *
     * @param data 发送群消息时，必须加上这个群的 MessageData
     * @param msg  消息内容
     */
    public void sendTextMsg(Object data, String msg, long[] at) {
        if (!(data instanceof MessageData)) return;
        Long[] l = new Long[at.length];
        for (int i = 0; i < at.length; i++) {
            l[i] = at[i];
        }
        SendMsgHook.Companion.get().sendText((MessageData) data, msg, l);
    }

    /**
     * 给发送群聊文本消息（如果没有添加好友，请 createTempConversation 后再发送消息）
     * <br />
     * 如果不艾特但需要发送群消息，请 <code>
     * api.sendTextMsg("Something", 123456789L, new long[0]);
     * </code>
     * <br />
     * 如果艾特全体成员，请 <code>
     * api.sendTextMsg("Something", 123456789L, new long[1]{-1L});
     * </code>
     *
     * @param msg 消息内容
     */
    public void sendTextMsg(String msg, long qNum, long[] at) {
        Long[] l = new Long[at.length];
        for (int i = 0; i < at.length; i++) {
            l[i] = at[i];
        }
        SendMsgHook.Companion.get().sendText(msg, qNum, l);
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
     * 发送卡片消息
     *
     * @param msg     代码
     * @param qNum    QQ号码
     * @param isGroup 是否群聊
     */
    public void sendCardMsg(String msg, long qNum, boolean isGroup) {
        Boolean b = (Boolean) ConfigManager.Companion.getDefaultConfig().get("pass_by_exam");
        if (b == null || !b) {
            loge("ScriptApi : SendCardMsg : 未通过高级验证，禁止发送卡片");
            return;
        }
        if (msg.startsWith("{"))
            SendMsgHook.Companion.get().sendArkApp(msg, qNum, isGroup);
        else if (msg.startsWith("<?xml"))
            SendMsgHook.Companion.get().sendAbsStruct(msg, qNum, isGroup);
    }

    /**
     * 禁言某人
     *
     * @param group 群号
     * @param qNum  QQ号
     * @param time  时间，0=解除禁言
     */
    public void shutUp(long group, long qNum, long time) {
        ClassUtils.callVisualMethod(getGagManager(), "a", String.valueOf(group), String.valueOf(qNum), time, String.class, String.class, Long.TYPE);
    }

    /**
     * 全体禁言
     *
     * @param group 群号
     * @param time  状态，false=解除禁言，true反之
     */
    public void shutAllUp(long group, boolean time) {
        Object mgr = getGagManager();
        ClassUtils.callVisualMethod(mgr, "a", String.valueOf(group), time ? 268435455 : 0, String.class, Long.TYPE);
    }

    private Object getGagManager() {
        int i;
        try {
            i = (int) ClassUtils.getStaticObject(Initiator.load(".app.QQManagerFactory"), "TROOP_GAG_MANAGER", Integer.TYPE);
        } catch (Exception ignored) {
            i = 48;
        }
        return ClassUtils.callVisualMethod(QQFields.getQQAppInterface(), "getManager", i, Integer.TYPE);
    }
}
