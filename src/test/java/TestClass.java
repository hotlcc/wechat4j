import com.alibaba.fastjson.JSONObject;
import com.hotlcc.wechat4j.Wechat;
import com.hotlcc.wechat4j.api.WebWeixinApi;
import com.hotlcc.wechat4j.util.QRCodeUtil;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

public class TestClass {
    private static Logger logger = LoggerFactory.getLogger(TestClass.class);

    //@Test
//    public void test01() {
//        JSONObject result = null;
//
//        String redirectUri = null;
//        login:
//        while (true) {
//            //1、获取uuid
//            logger.info("开始获取uuid...");
//            String uuid = null;
//            while (uuid == null || "".equals(uuid)) {
//                result = webWeixinApi.getWxUuid();
//                if (result != null) {
//                    uuid = result.getString("uuid");
//                }
//                if (uuid == null || "".equals(uuid)) {
//                    logger.info("获取uuid失败，将自动重试");
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    break;
//                }
//            }
//            logger.info("获取uuid成功，值为：{}", uuid);
//
//            //2、获取二维码
//            logger.info("开始获取二维码...");
//            result = webWeixinApi.getQR(uuid);
//            logger.info("获取二维码成功，请扫描二维码：\n{}", QRCodeUtil.toCharMatrix(result.getBytes("data")));
//
//            //3、轮询
//            String code = null;
//            while (!"200".equals(code)) {
//                result = webWeixinApi.getRedirectUri(uuid);
//                code = result.getString("code");
//                if ("408".equals(code)) {
//                    continue;
//                } else if ("400".equals(code)) {
//                    logger.info("二维码失效，将自动获取新的二维码");
//                    continue login;
//                } else if ("201".equals(code)) {
//                    logger.info("请在手机上确认");
//                    continue;
//                } else if ("200".equals(code)) {
//                    redirectUri = result.getString("redirectUri");
//                    logger.info("手机端认证成功");
//                    break login;
//                } else {
//                    break login;
//                }
//            }
//        }
//        //4、获取登录认证码
//        logger.info("开始获取登录认证码");
//        result = webWeixinApi.getLoginCode(redirectUri);
//        String wxsid = result.getString("wxsid");
//        String passTicket = result.getString("pass_ticket");
//        String skey = result.getString("skey");
//        String wxuin = result.getString("wxuin");
//        logger.info("获取登录认证码成功");
//        //5、初始化数据
//        logger.info("开始初始化数据");
//        result = webWeixinApi.webWeixinInit(passTicket, wxsid, skey, wxuin);
//        JSONObject loginUser = result.getJSONObject("User");
//        logger.info("欢迎回来，{}", loginUser.getString("NickName"));
//        JSONObject SyncKey = result.getJSONObject("SyncKey");
//        logger.info("初始化数据完成");
//        //6、开启消息状态通知
//        logger.info("开始开启消息状态通知");
//        result = webWeixinApi.statusNotify(passTicket, wxsid, skey, wxuin, loginUser.getString("UserName"));
//        logger.info("开启消息状态通知完成");
//        //7、服务端状态同步
//        logger.info("开始轮询服务端状态");
//        while (true) {
//            result = webWeixinApi.syncCheck(wxsid, skey, wxuin, SyncKey.getJSONArray("List"));
//            int retcode = result.getIntValue("retcode");
//            if (retcode != 0) {
//                logger.info("微信已退出登录");
//                break;
//            } else {
//                int selector = result.getIntValue("selector");
//                if (selector == 2) {
//                    logger.info("收到新消息");
//                    //8、获取新消息内容
//                    result = webWeixinApi.pullNewMsg(passTicket, wxsid, skey, wxuin, SyncKey);
//                    SyncKey = result.getJSONObject("SyncKey");
//                    JSONObject Msg = result.getJSONArray("AddMsgList").getJSONObject(0);
//                    String Content = Msg.getString("Content");
//                    int MsgType = Msg.getIntValue("MsgType");
//                    String FromUserName = Msg.getString("FromUserName");
//                    String ToUserName = Msg.getString("ToUserName");
//                    logger.info("消息类型：{}，消息内容：{}，发送方：{}，接收方：{}", MsgType, Content, FromUserName, ToUserName);
//                    result = webWeixinApi.sendMsg(passTicket, wxsid, skey, wxuin, Content, MsgType, ToUserName, FromUserName);
//                    logger.info("自动回复消息完成，返回：{}", result);
//                }
//            }
//        }
//    }

    public void test03() throws IOException {
        BufferedImage image = ImageIO.read(new FileInputStream("D:/2.jpg"));
        System.out.println(QRCodeUtil.toCharMatrix(image));
    }

    @Test
    public void test04() {
        WebWeixinApi api = new WebWeixinApi();
        Wechat wechat = new Wechat();
        wechat.setWebWeixinApi(api);


    }
}
