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
    api.sendTextMsg("QScript 脚本发消息测试：表情：/xyx [斜眼笑] [奸笑]",3340792396L);
    api.sendCardMsg("<?xml version='1.0' encoding='UTF-8' " +
        "standalone='yes' ?><msg serviceID=\"33\" templateID=\"123\" " +
        "action=\"web\" brief=\"【链接】Golink加速器-国内首款免费游戏加速器【官方\" " +
        "sourceMsgId=\"0\" url=\"https://www.golink.com/?code=JYPYKZWN\"" +
        " flag=\"8\" adverSign=\"0\" multiMsgFlag=\"0\"><item layout=\"2\"" +
        " advertiser_id=\"0\" aid=\"0\"><picture cover=\"https://qq.ugcimg.cn/v1/o3upv4dbs" +
        "quu39i05lpnt57nmuaae2q4lus62r1u22o1cav00k7jus7po80am2j17r004ultmqfsq/s6vskamj00lmmk" +
        "t83jce822lfg\" w=\"0\" h=\"0\" /><title>QScript XML 消息测试</titl" +
        "e><summary>拥有智能加速、游戏高速下载技术,解决游戏登录不上,高延迟,掉线等问</summary></ite" +
        "m><source name=\"\" icon=\"\" action=\"\" appid=\"-1\" /></msg>",818333976L,true); // 最后的布尔值代表是否群聊

    api.sendCardMsg("{\"app\":\"com.tencent.miniapp_01\",\"" +
        "desc\":\"\",\"view\":\"notification\",\"ver\":\"1.0.0" +
        ".11\",\"prompt\":\"人机验证\",\"appID\":\"\",\"sourceNa" +
        "me\":\"\",\"actionData\":\"\",\"actionData_A\":\"\",\"" +
        "sourceUrl\":\"\",\"meta\":{\"notification\":{\"appInfo\"" +
        ":{\"appName\":\"请发送你的验证码.\",\"appType\":4,\"appid\"" +
        ":2174398127,\"iconUrl\":\"https:\\/\\/q.qlogo.cn\\/headimg" +
        "_dl?dst_uin=2414323534｜&spec=100\"},\"button\":[{\"action\"" +
        ":\"https:\\/\\/wpa.qq.com\\/msgrd?v=3&uin=3267478629｜&site=q" +
        "q&menu=yes\",\"name\":\"3267478629\"},{\"action\":\"1\",\"name" +
        "\":\"1248481\"}],\"data\":[{\"value\":\"QScript JSON 消息测试" +
        "。你的验证码：4120\"}],\"emphasis_keyword\"" +
        ":\"1\",\"title\":\"人机验证\"}},\"text\":\"\",\"sourceAd\":\"\",\"" +
        "extra\":\"\"}",818333976L,true);
}

/**
 * 好友文本消息
 */
public void onMsg(Object param){
    String l=param.senderUin; // 发送者
    String s=param.content; // 文本内容
    String name=param.nickname; // 名字
    String f=param.friendUin; // 如果是群消息，这个就是群聊号码，否则就是发送者
    if(s.equals("群消息测试") && param.isGroupMsg()){
        api.log("尝试群消息测试");
        api.sendTextMsg("群测试A",f,new long[]{3318448676L,3340792396L});
        // 这种发送方法正在制作
//        api.sendTextMsg(param,"群测试B",new long[]{3318448676L,3340792396L});
        api.sendTextMsg("测试完成",api.str2long(l));
        return;
    }
    if(param.isGroupMsg() || l.equals(mQNum.toString())){
        return;
    }
    api.sendTextMsg("这是一条消息",api.str2long(l));
}