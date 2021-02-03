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
        }

/**
 * 好友文本消息
 */
public void onFriendMessage(Object param){
        api.log("on friend msg");
        String l=param.uin; // 发送者
        String s=param.content; // 文本内容
        api.sendTextMsg("QScript：自动回复\n消息内容："+s+"\n消息发送人："+l,3318448676L);
        }