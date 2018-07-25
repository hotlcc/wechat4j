import com.hotlcc.wechat4j.Wechat;
import com.hotlcc.wechat4j.api.WebWeixinApi;
import com.hotlcc.wechat4j.handler.ReceivedMsgHandler;
import com.hotlcc.wechat4j.model.ReceivedMsg;
import com.hotlcc.wechat4j.model.UserInfo;
import com.hotlcc.wechat4j.util.CommonUtil;

public class TestClass2 {
    public static void main(String[] args) {
        WebWeixinApi api = new WebWeixinApi();
        Wechat wechat = new Wechat();
        wechat.setWebWeixinApi(api);
        wechat.addReceivedMsgHandler(new ReceivedMsgHandler() {
            @Override
            public void handleAllType(Wechat wechat, ReceivedMsg msg) {
                System.out.println("===收到消息：" + msg.getContent());
                UserInfo contact = wechat.getContactByUserName(false, msg.getFromUserName());
                if ("李国栋".equals(contact.getRemarkName())) {
                }
            }
        });
        wechat.autoLogin();
        CommonUtil.threadSleep(1000 * 60 * 10);
        wechat.logout();
    }
}
