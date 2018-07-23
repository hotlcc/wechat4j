package com.hotlcc.wechat4j;

import com.alibaba.fastjson.JSONObject;
import com.hotlcc.wechat4j.api.WebWeixinApi;
import com.hotlcc.wechat4j.enums.LoginTipEnum;
import com.hotlcc.wechat4j.util.PropertiesUtil;
import com.hotlcc.wechat4j.util.QRCodeUtil;
import com.hotlcc.wechat4j.util.StringUtil;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * 微信客户端
 *
 * @author Allen
 */
public class Wechat {
    private static Logger logger = LoggerFactory.getLogger(Wechat.class);

    private WebWeixinApi webWeixinApi;

    private CookieStore cookieStore;
    private HttpClient httpClient;

    //在线状态
    private volatile boolean isOnline = false;

    private volatile String wxsid;
    private volatile String passTicket;
    private volatile String skey;
    private volatile String wxuin;

    public Wechat(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
        this.httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    }

    public Wechat() {
        this.cookieStore = new BasicCookieStore();
        this.httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    }

    public void setWebWeixinApi(WebWeixinApi webWeixinApi) {
        this.webWeixinApi = webWeixinApi;
    }

    /**
     * 获取uuid
     *
     * @param ps
     * @param time
     * @return
     */
    private String getWxUuid(PrintStream ps, int time) {
        ps.print("尝试正常方式获取uuid...");
        ps.flush();

        for (int i = 0; i <= time; i++) {
            if (i > 0) {
                ps.print("\t第" + i + "次尝试...");
                ps.flush();
            }
            JSONObject result = webWeixinApi.getWxUuid(httpClient);

            if (result == null) {
                ps.println("\t失败：出现异常");
                ps.flush();
                return null;
            }

            String code = result.getString("code");
            String uuid = result.getString("uuid");
            if (!"200".equals(code)) {
                String msg = result.getString("msg");
                ps.println("\t失败：" + msg);
                ps.flush();
                return null;
            }

            if (StringUtil.isEmpty(uuid)) {
                ps.print("\t失败");
                if (i == 0 && time > 0) {
                    ps.print("，将重复尝试" + time + "次");
                }
                ps.println();
                ps.flush();
                continue;
            }

            ps.println("\t成功，值为：" + uuid);
            ps.flush();
            return uuid;
        }
        return null;
    }

    /**
     * 获取并显示qrcode
     *
     * @return
     */
    private boolean getAndShowQRCode(PrintStream ps, String uuid, int time) {
        ps.print("获取二维码...");
        ps.flush();

        for (int i = 0; i <= time; i++) {
            if (i > 0) {
                ps.print("\t第" + i + "次尝试...");
                ps.flush();
            }

            byte[] data = webWeixinApi.getQR(httpClient, uuid);

            if (data == null || data.length <= 0) {
                ps.print("\t失败");
                if (i == 0 && time > 0) {
                    ps.print("，将重新获取uuid并重复尝试" + time + "次");
                }
                ps.println();
                ps.flush();
                getWxUuid(ps, 0);
                continue;
            }

            ps.println("\t成功，请扫描二维码：");
            ps.flush();
            ps.println(QRCodeUtil.toCharMatrix(data));
            ps.flush();
            QRCodeUtil.openQRCodeImage(data);
            return true;
        }

        return false;
    }

    /**
     * 等待手机端确认登录
     *
     * @return
     */
    private String waitForConfirm(PrintStream ps, String uuid) {
        ps.print("等待手机端扫码...");
        ps.flush();

        String code = null;
        boolean flag = false;
        while (!"200".equals(code)) {
            JSONObject result = webWeixinApi.getRedirectUri(httpClient, LoginTipEnum.TIP_0, uuid);
            if (result == null) {
                ps.println("\t失败：出现异常");
                ps.flush();
                return null;
            }

            code = result.getString("code");
            if ("408".equals(code)) {
                ps.print(".");
                ps.flush();
                continue;
            } else if ("400".equals(code)) {
                ps.println("\t失败，二维码失效");
                ps.flush();
                return null;
            } else if ("201".equals(code)) {
                if (!flag) {
                    ps.println();
                    ps.print("请确认登录...");
                    ps.flush();
                    flag = true;
                }
                continue;
            } else if ("200".equals(code)) {
                String redirectUri = result.getString("redirectUri");
                ps.println("\t成功，认证完成");
                ps.flush();
                return redirectUri;
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * 获取登录认证码
     */
    private boolean getLoginCode(PrintStream ps, String redirectUri) {
        ps.print("获取登录认证码...");
        ps.flush();

        JSONObject result = webWeixinApi.getLoginCode(httpClient, redirectUri);
        if (result == null) {
            ps.println("\t失败：出现异常");
            ps.flush();
            return false;
        }

        String ret = result.getString("ret");
        if (!"0".equals(ret)) {
            ps.println("\t失败：错误的返回码(" + ret + ")");
            ps.flush();
            return false;
        }

        wxsid = result.getString("wxsid");
        passTicket = result.getString("pass_ticket");
        skey = result.getString("skey");
        wxuin = result.getString("wxuin");
        isOnline = true;

        ps.println("\t成功");
        ps.flush();

        return true;
    }

    /**
     * push方式获取uuid
     *
     * @param ps
     * @param wxuin
     * @return
     */
    private String getWxUuid(PrintStream ps, String wxuin) {
        ps.print("尝试push方式获取uuid...");
        ps.flush();

        JSONObject result = webWeixinApi.pushLogin(httpClient, wxuin);
        if (result == null) {
            ps.println("\t失败：出现异常");
            ps.flush();
            return null;
        }

        String ret = result.getString("ret");
        if (!"0".equals(ret)) {
            ps.println("\t失败：错误的返回码(" + ret + ")");
            ps.flush();
            return null;
        }

        String uuid = result.getString("uuid");
        if (StringUtil.isEmpty(uuid)) {
            ps.println("\t失败：空值");
            ps.flush();
            return null;
        }

        ps.println("\t成功，值为：" + uuid);
        ps.flush();

        return uuid;
    }

    /**
     * 自动登录
     */
    public boolean autoLogin(OutputStream os) {
        // 0、获取消息打印流
        PrintStream ps = null;
        if (os != null) {
            ps = new PrintStream(os);
        } else {
            ps = System.out;
        }

        // 1、判断是否已经登录
        if (isOnline) {
            ps.println("当前已是登录状态，无需登录");
            return true;
        }

        JSONObject result = null;
        int time = PropertiesUtil.getIntValue("wechat4j.retry.time", 3);

        // 2、登录
        // 2.1、获取uuid
        String uuid = null;
        if (StringUtil.isNotEmpty(wxuin)) {
            uuid = getWxUuid(ps, wxuin);
        }
        if (StringUtil.isEmpty(uuid)) {
            uuid = getWxUuid(ps, time);
        }
        if (StringUtil.isEmpty(uuid)) {
            ps.println("无法获取uuid，登录不成功");
            ps.flush();
            return false;
        }
        // 2.2、获取并显示二维码
        if (!getAndShowQRCode(ps, uuid, time)) {
            ps.println("无法获取二维码，登录不成功");
            ps.flush();
            return false;
        }
        // 2.3、等待确认
        String redirectUri = waitForConfirm(ps, uuid);
        if (StringUtil.isEmpty(redirectUri)) {
            ps.println("手机端认证失败，登录不成功");
            ps.flush();
            return false;
        }
        // 2.4、获取登录认证码
        if (!getLoginCode(ps, redirectUri)) {
            ps.println("无法获取登录认证码，登录不成功");
            ps.flush();
            return false;
        }

        return true;
    }

    /**
     * 自动登录
     *
     * @return
     */
    public boolean autoLogin() {
        return autoLogin(null);
    }

    /**
     * 退出登录
     */
    public void logout() {
        webWeixinApi.logout(httpClient, wxsid, skey, wxuin);
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
//        logger.info("开始开启消息状态通知");
//        result = webWeixinApi.statusNotify(httpClient, passTicket, wxsid, skey, wxuin, loginUser.getString("UserName"));
//        logger.info("开启消息状态通知完成");
        //7、获取联系人信息
//        logger.info("开始获取全部联系人");
//        result = webWeixinApi.getContact(httpClient, passTicket, wxsid);
//        JSONArray MemberList = result.getJSONArray("MemberList");
//        logger.info("获取全部联系人完成，共{}条：", MemberList.size());
//        System.out.println("昵称\t备注名\tUserName");
//        for (int i = 0, len = MemberList.size(); i < len; i++) {
//            JSONObject Member = MemberList.getJSONObject(i);
//            if ("BYCX-IT".equals(Member.getString("NickName"))) {
//                System.out.println(Member);
//                JSONArray params = new JSONArray();
//                JSONObject param = new JSONObject();
//                param.put("EncryChatRoomId", "");
//                param.put("UserName", Member.getString("UserName"));
//                params.add(param);
//                result = webWeixinApi.batchGetContact(httpClient, passTicket, wxsid, skey, wxuin, params);
//                System.out.println(result);
//                break;
//            }
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
//        result = webWeixinApi.sendMsg(httpClient, passTicket, wxsid, skey, wxuin,
//                "测试消息",
//                1,
//                loginUser.getString("UserName"),
//                "@493f6dbadb4ed2f471b8098fd7f6db1bbcc7294e829e8ecbdc6d2b32647bc2d2");
//        System.out.println(result);
    }
}
