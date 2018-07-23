package com.hotlcc.wechat4j;

import com.alibaba.fastjson.JSONObject;
import com.hotlcc.wechat4j.api.WebWeixinApi;
import com.hotlcc.wechat4j.enums.LoginTipEnum;
import com.hotlcc.wechat4j.util.QRCodeUtil;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 微信客户端
 *
 * @author Allen
 */
public class Wechat {
    private static Logger logger = LoggerFactory.getLogger(Wechat.class);

    private CookieStore cookieStore;
    private HttpClient httpClient;

    private WebWeixinApi webWeixinApi;

    public void setWebWeixinApi(WebWeixinApi webWeixinApi) {
        this.webWeixinApi = webWeixinApi;
    }

    public Wechat(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
        this.httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    }

    public Wechat() {
        this.cookieStore = new BasicCookieStore();
        this.httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    }

    /**
     * 自动登录
     */
    public void autoLogin() {

    }

    public void test() {
        JSONObject result = null;

        String redirectUri = null;
        login:
        while (true) {
            //1、获取uuid
            logger.info("开始获取uuid...");
            String uuid = null;
            while (uuid == null || "".equals(uuid)) {
                result = webWeixinApi.getWxUuid(httpClient);
                if (result != null) {
                    uuid = result.getString("uuid");
                }
                if (uuid == null || "".equals(uuid)) {
                    logger.info("获取uuid失败，将自动重试");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
            logger.info("获取uuid成功，值为：{}", uuid);

            //2、获取二维码
            logger.info("开始获取二维码...");
            byte[] data = webWeixinApi.getQR(httpClient, uuid);
            logger.info("获取二维码成功，请扫描二维码：\n{}", QRCodeUtil.toCharMatrix(data));
            QRCodeUtil.openQRCodeImage(data);

            //3、轮询
            String code = null;
            while (!"200".equals(code)) {
                result = webWeixinApi.getRedirectUri(httpClient, LoginTipEnum.TIP_0, uuid);
                code = result.getString("code");
                if ("408".equals(code)) {
                    continue;
                } else if ("400".equals(code)) {
                    logger.info("二维码失效，将自动获取新的二维码");
                    continue login;
                } else if ("201".equals(code)) {
                    logger.info("请在手机上确认");
                    continue;
                } else if ("200".equals(code)) {
                    redirectUri = result.getString("redirectUri");
                    logger.info("手机端认证成功");
                    break login;
                } else {
                    break login;
                }
            }
        }
        //4、获取登录认证码
        logger.info("开始获取登录认证码");
        result = webWeixinApi.getLoginCode(httpClient, redirectUri);
        String wxsid = result.getString("wxsid");
        String passTicket = result.getString("pass_ticket");
        String skey = result.getString("skey");
        String wxuin = result.getString("wxuin");
        logger.info("获取登录认证码成功");
        //5、初始化数据
        logger.info("开始初始化数据");
        result = webWeixinApi.webWeixinInit(httpClient, passTicket, wxsid, skey, wxuin);
        JSONObject loginUser = result.getJSONObject("User");
        logger.info("欢迎回来，{}", loginUser.getString("NickName"));
        JSONObject SyncKey = result.getJSONObject("SyncKey");
        logger.info("初始化数据完成");
        //6、开启消息状态通知
        logger.info("开始开启消息状态通知");
        result = webWeixinApi.statusNotify(httpClient, passTicket, wxsid, skey, wxuin, loginUser.getString("UserName"));
        logger.info("开启消息状态通知完成");
        //7、获取联系人信息
//        logger.info("开始获取全部联系人");
//        result = webWeixinApi.getContact(httpClient, passTicket, wxsid);
//        JSONArray MemberList = result.getJSONArray("MemberList");
//        logger.info("获取全部联系人完成，共{}条：", MemberList.size());
//        System.out.println("昵称\t备注名\tUserName");
//        for (int i = 0, len = MemberList.size(); i < len; i++) {
//            JSONObject Member = MemberList.getJSONObject(i);
//            System.out.println(Member.getString("NickName") + "\t" + Member.getString("RemarkName") + "\t" + Member.getString("UserName"));
//        }
//        logger.info("获取全部联系人完成，共{}条", MemberList.size());
        //7、服务端状态同步
//        logger.info("开始轮询服务端状态");
//        while (true) {
//            result = webWeixinApi.syncCheck(httpClient, wxsid, skey, wxuin, SyncKey.getJSONArray("List"));
//            System.out.println(result);
//            int retcode = result.getIntValue("retcode");
//            if (retcode != 0) {
//                logger.info("微信已退出登录：retcode={}", retcode);
//                break;
//            } else {
//                int selector = result.getIntValue("selector");
//                if (selector == 2) {
//                    logger.info("收到新消息");
//                    //8、获取新消息内容
//                    result = webWeixinApi.pullNewMsg(httpClient, passTicket, wxsid, skey, wxuin, SyncKey);
//                    SyncKey = result.getJSONObject("SyncKey");
//                    JSONArray AddMsgList = result.getJSONArray("AddMsgList");
//                    if (AddMsgList != null) {
//                        for (int i = 0, len = AddMsgList.size(); i < len; i++) {
//                            JSONObject Msg = AddMsgList.getJSONObject(i);
//                            String Content = Msg.getString("Content");
//                            int MsgType = Msg.getIntValue("MsgType");
//                            String FromUserName = Msg.getString("FromUserName");
//                            String ToUserName = Msg.getString("ToUserName");
//                            logger.info("消息类型：{}，消息内容：{}，发送方：{}，接收方：{}", MsgType, Content, FromUserName, ToUserName);
////                            result = webWeixinApi.sendMsg(httpClient, passTicket, wxsid, skey, wxuin, Content, MsgType, ToUserName, FromUserName);
////                            logger.info("自动回复消息完成，返回：{}", result);
//                        }
//                    }
//                }
//            }
//        }
        //测试给特定联系人发送文本信息
        result = webWeixinApi.sendMsg(httpClient, passTicket, wxsid, skey, wxuin,
                "测试消息",
                1,
                loginUser.getString("UserName"),
                "@493f6dbadb4ed2f471b8098fd7f6db1bbcc7294e829e8ecbdc6d2b32647bc2d2");
        System.out.println(result);
    }
}
