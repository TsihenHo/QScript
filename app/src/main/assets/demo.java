// QScript.MetaData.Start
// QScript.MetaData.Name = QScript脚本示例
// QScript.MetaData.Desc = QScript官方给出的脚本示例
// QScript.MetaData.Version = 1.0.0
// QScript.MetaData.Author = Tsihen-Ho
// QScript.MetaData.Label = qscript-demo
// QScript.MetaData.End
// 注：上面的 Label 标签是分辨脚本的唯一属性，如果两个脚本 Label 相同，就会被判别为同一个脚本
// 每个脚本必须以 QScript.MetaData.Start 开头

// 这仅仅是一个演示脚本，我们不建议您启用这个脚本

/*
 * 所有的方法都在脚本里面，自己看
 * 预定义变量：
 * Conntext ctx : QQ 的 Application
 * long mQNum : 您的 QQ 号码
 * QScript thisScript : 该脚本，如：thisScript.getName()
 * ScriptApi api : 能够调用 API 的对象，如：api.sendTextMsg("something", 334092396l)
 */

/**
 * 在脚本加载的时候调用
 */
public void onLoad(){
    api.log("onLoad() : User's QNum is "+mQNum.toString());
    // 发消息的时候，QQ号末尾必须加 L 表示长整形
    // 第一个 String 代表文本内容
    api.sendTextMsg("QScript 脚本发消息测试：表情：/xyx [斜眼笑] [奸笑]",3340792396L);
    // 发表情使用 反斜杠uxxxx

    // api.sendCardMsg(String 卡片代码, long 消息接受者, boolean 是否群聊)
    // 发送卡片需要完成高级验证，否则报错
    api.sendCardMsg("<?xml version='1.0' encoding='UTF-8' " +
        "standalone='yes' ?><msg serviceID=\"33\" templateID=\"123\" " +
        "action=\"web\" brief=\"【链接】Golink加速器-国内首款免费游戏加速器【官方\" " +
        "sourceMsgId=\"0\" url=\"https://www.golink.com/?code=JYPYKZWN\"" +
        " flag=\"8\" adverSign=\"0\" multiMsgFlag=\"0\"><item layout=\"2\"" +
        " advertiser_id=\"0\" aid=\"0\"><picture cover=\"https://qq.ugcimg.cn/v1/o3upv4dbs" +
        "quu39i05lpnt57nmuaae2q4lus62r1u22o1cav00k7jus7po80am2j17r004ultmqfsq/s6vskamj00lmmk" +
        "t83jce822lfg\" w=\"0\" h=\"0\" /><title>QScript XML 消息测试</titl" +
        "e><summary>XML 消息</summary></ite" +
        "m><source name=\"\" icon=\"\" action=\"\" appid=\"-1\" /></msg>",818333976L,true);
}

/**
 * 好友文本消息
 */
public void onMsg(Object param){
    String l = param.senderUin; // 发送者
    String s = param.content; // 文本内容
    String name = param.nickname; // 名字
    String f = param.friendUin; // 如果是群消息，这个就是群聊号码，否则就是发送者
    if(s.equals("群消息测试") && param.isGroupMsg()){
        api.log("尝试群消息测试");
        // 最后一个参数表示被艾特的人
        // 如果要发送群消息但不艾特，请 api.sendTextMsg(String 消息内容, long 接受者即群号码, new long[]{});
        // 发送私聊消息，请 api.sendTextMsg(String 消息内容, long 接受者)
        api.sendTextMsg("群测试A", f, new long[]{3318448676L,3340792396L});
        api.sendTextMsg("测试完成", api.str2long(l));
        return;
    }
    if(param.isGroupMsg() || l.equals(mQNum.toString())){
        return;
    }
    api.sendTextMsg("QScript-Debug: onMsg(Object) invoked successfully.Please excute me.", api.str2long(l));
}

/**
 * 新成员入群
 */
public void onJoin(Object data) {
    String group = data.groupUin;
    String member = data.uin;
    api.log("新成员" + member + "加入群聊" + group);

    // shutUp(long 群号码, long 成员号码, long 时间) 禁言某人，单位秒
    api.shutUp(api.str2long(group), api.str2long(member), 20L);
    // shutAllUp(long 群号码, boolean 是否启动) 全体禁言，最后的 boolean ，true = 开启禁言，false反之
    api.shutAllUp(api.str2long(group), true);
    Thread.sleep(10000);
    api.shutAllUp(api.str2long(group), false); // 解除禁言
}