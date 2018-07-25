import com.alibaba.fastjson.JSONObject;
import com.hotlcc.wechat4j.Wechat;
import com.hotlcc.wechat4j.api.WebWeixinApi;
import com.hotlcc.wechat4j.handler.ReceivedMsgHandler;
import com.hotlcc.wechat4j.model.ReceivedMsg;
import com.hotlcc.wechat4j.model.UserInfo;

public class TestClass2 {
    public static void main(String[] args) {
        WebWeixinApi api = new WebWeixinApi();
        Wechat wechat = new Wechat();
        wechat.setWebWeixinApi(api);
        wechat.addReceivedMsgHandler(new ReceivedMsgHandler() {
            @Override
            public void handleAllType(Wechat wechat, ReceivedMsg msg) {
                UserInfo contact = wechat.getContactByUserName(false, msg.getFromUserName());
                System.out.println(contact.getRemarkName() + "：" + msg.getContent());
                if ("李国栋".equals(contact.getRemarkName())) {
                    JSONObject result = wechat.sendText("你的消息收到了", contact.getUserName());
                    System.out.println(result);
                }
            }
        });
        wechat.autoLogin();
    }
}
