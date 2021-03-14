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
package me.tsihen.qscript.script.qscript.api;

import me.tsihen.qscript.config.ConfigManager;
import me.tsihen.qscript.hook.SendMsgHook;
import me.tsihen.qscript.script.qscript.QScript;
import me.tsihen.qscript.script.qscript.objects.MessageData;
import me.tsihen.qscript.util.ClassFinder;
import me.tsihen.qscript.util.Initiator;
import me.tsihen.qscript.util.QQFields;
import me.tsihen.qscript.util.ReflexUtils;
import me.tsihen.qscript.util.Utils;

import static me.tsihen.qscript.util.ConstsKt.C_QQ_APP_INTERFACE;
import static me.tsihen.qscript.util.Utils.scriptLog;

@SuppressWarnings("unused")
public class ScriptApi {
    private final QScript script;

    protected ScriptApi(QScript qs) {
        script = qs;
    }

    public static ScriptApi get(QScript qs) {
        return new ScriptApi(qs);
    }

    /**
     * 获取网络
     */
    public ScriptNetwork getNetwork() {
        if (!script.getPermissionNetwork()) return null;
        return new ScriptNetwork(script);
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
        Utils.scriptLog(script.getName() + " : " + msg);
    }

    /**
     * 获取昵称
     *
     * @return 昵称
     */
    public static String getName(String senderUin, String friendUin) {
        return (String) ReflexUtils.callStaticMethod(
                Initiator.load(".utils.ContactUtils"),
                "a",
                QQFields.getQQAppInterface(),
                senderUin,
                friendUin,
                1,
                0,
                ClassFinder.INSTANCE.findClass(C_QQ_APP_INTERFACE),
                String.class,
                String.class,
                int.class,
                int.class
        );
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
            scriptLog(script.getName() + " : SendCardMsg : 未通过高级验证，禁止发送卡片");
            return;
        }
        if (msg.startsWith("{"))
            SendMsgHook.Companion.get().sendArkApp(msg, qNum, isGroup);
        else if (msg.startsWith("<?xml"))
            SendMsgHook.Companion.get().sendAbsStruct(msg, qNum, isGroup);
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
     * 禁言某人
     *
     * @param group 群号
     * @param qNum  QQ号
     * @param time  时间，0=解除禁言
     */
    public void shutUp(long group, long qNum, long time) {
        ReflexUtils.callMethod(getGagManager(), "a", String.valueOf(group), String.valueOf(qNum), time, String.class, String.class, Long.TYPE);
    }

    /**
     * 发送图片
     *
     * @param path 路径
     */
    public void sendPicMsg(String path, long qNum, boolean isGroup) {
        SendMsgHook.Companion.get().sendPic(path, qNum, isGroup);
    }

    /**
     * 全体禁言
     *
     * @param group 群号
     * @param time  状态，false=解除禁言，true反之
     */
    public void shutAllUp(long group, boolean time) {
        Object mgr = getGagManager();
        ReflexUtils.callMethod(mgr, "a", String.valueOf(group), time ? 268435455 : 0, String.class, Long.TYPE);
    }

    public String getNickname(Object data) {
        if (!(data instanceof MessageData)) return "";
        return ScriptApi.getName(((MessageData) data).getSenderUin(), ((MessageData) data).getFriendUin());
    }

    public String getNickname(String senderUin, String friendUin) {
        return ScriptApi.getName(senderUin, friendUin);
    }

    protected Object getGagManager() {
        int i;
        try {
            i = (int) ReflexUtils.getStaticObject(Initiator.load(".app.QQManagerFactory"), "TROOP_GAG_MANAGER", Integer.TYPE);
        } catch (Exception ignored) {
            i = 48;
        }
        return ReflexUtils.callMethod(QQFields.getQQAppInterface(), "getManager", i, Integer.TYPE);
    }
}
