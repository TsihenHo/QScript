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
 * ScriptApi api : 能够调用 API 的对象，如：api.sendTextMsg("something", 3318448676)
 */

/**
 * 在脚本加载的时候调用
 */
public void onLoad(){
        api.log("onLoad() : User's QNum is " + mQNum.toString());
        }

/**
 * 好友文本消息
 */
public void onFriendTextMessage(Object param){
        Long l=param.senderuin; // 发送者
        String s=param.content; // 文本内容
        api.sendTextMsg("消息处理完成",l);
        }