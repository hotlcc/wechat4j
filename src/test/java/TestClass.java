import com.alibaba.fastjson.JSONObject;
import com.hotlcc.wechat4j.Wechat;
import com.hotlcc.wechat4j.api.WebWeixinApi;
import com.hotlcc.wechat4j.handler.ReceivedMsgHandler;
import com.hotlcc.wechat4j.model.ReceivedMsg;
import com.hotlcc.wechat4j.model.UserInfo;
import com.hotlcc.wechat4j.util.StringUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class TestClass {
    private Wechat wechat;

    @Before
    public void initAndLogin() {
        wechat = new Wechat();
        WebWeixinApi api = new WebWeixinApi();
        wechat.setWebWeixinApi(api);
        wechat.addReceivedMsgHandler(new ReceivedMsgHandler() {
            @Override
            public void handleAllType(Wechat wechat, ReceivedMsg msg) {
                UserInfo contact = wechat.getContactByUserName(false, msg.getFromUserName());
                String name = StringUtil.isEmpty(contact.getRemarkName()) ? contact.getNickName() : contact.getRemarkName();
                System.out.println(name + ": " + msg.getContent());
            }
        });

        wechat.autoLogin();
    }

    public void testSendText() {
        JSONObject result = wechat.sendText(null, "这是消息内容");
        System.out.println(result);
    }

    @Test
    public void testSendImage() {
        File file = new File("D:\\Downloads\\images\\6600e90b8b0ce2037a5291a7147ffd2b.jpeg");

        JSONObject result = wechat.sendImage(null, file);
        System.out.println(result);
    }
}
