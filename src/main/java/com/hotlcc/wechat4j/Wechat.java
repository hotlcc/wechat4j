package com.hotlcc.wechat4j;

import com.alibaba.fastjson.JSONArray;
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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 微信客户端
 *
 * @author Allen
 */
@SuppressWarnings("Duplicates")
public class Wechat {
    private static Logger logger = LoggerFactory.getLogger(Wechat.class);

    private WebWeixinApi webWeixinApi;

    private CookieStore cookieStore;
    private HttpClient httpClient;

    //在线状态
    private volatile boolean isOnline = false;
    //认证码
    private volatile String wxsid;
    private volatile String passTicket;
    private volatile String skey;
    private volatile String wxuin;
    //用户数据
    private volatile JSONObject loginUser;
    private final Lock loginUserLock = new ReentrantLock();
    private volatile JSONObject SyncKey;
    private final Lock SyncKeyLock = new ReentrantLock();
    private volatile JSONArray ContactList;
    private final Lock ContactListLock = new ReentrantLock();

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
     * 获取uuid（登录时）
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
     * 获取并显示qrcode（登录时）
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
     * 等待手机端确认登录（登录时）
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
     * 获取登录认证码（登录时）
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
     * push方式获取uuid（登录时）
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
     * 微信数据初始化
     *
     * @return
     */
    private boolean wxInit() {
        JSONObject result = webWeixinApi.webWeixinInit(httpClient, passTicket, wxsid, skey, wxuin);
        if (result == null) {
            return false;
        }

        JSONObject BaseResponse = result.getJSONObject("BaseResponse");
        if (result == null) {
            return false;
        }

        int Ret = BaseResponse.getIntValue("Ret");
        if (Ret != 0) {
            return false;
        }

        try {
            loginUserLock.lock();
            loginUser = result.getJSONObject("User");
        } finally {
            loginUserLock.unlock();
        }

        try {
            SyncKeyLock.lock();
            SyncKey = result.getJSONObject("SyncKey");
        } finally {
            SyncKeyLock.unlock();
        }

        return true;
    }

    /**
     * 微信数据初始化（登录时）
     *
     * @return
     */
    private boolean wxInit(PrintStream ps, int time) {
        ps.print("正在初始化数据...");
        ps.flush();

        for (int i = 0; i <= time; i++) {
            if (i > 0) {
                ps.print("\t第" + i + "次尝试...");
                ps.flush();
            }

            if (!wxInit()) {
                ps.print("\t失败");
                if (i == 0 && time > 0) {
                    ps.print("，将重复尝试" + time + "次");
                }
                ps.println();
                ps.flush();
                continue;
            }

            ps.println("\t成功");
            ps.flush();

            return true;
        }
        return false;
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

        // 3、初始化数据
        if (!wxInit(ps, time)) {
            ps.println("初始化数据失败");
            ps.flush();
        }

        ps.println("微信登录成功，欢迎你：" + getLoginUserNickName());
        ps.flush();

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
        isOnline = false;
    }

    /**
     * 获取登录用户对象
     *
     * @return
     */
    public JSONObject getLoginUser(boolean update) {
        if (loginUser == null || update) {
            try {
                loginUserLock.lock();
                if (loginUser == null || update) {
                    JSONObject result = webWeixinApi.webWeixinInit(httpClient, passTicket, wxsid, skey, wxuin);
                    if (result == null) {
                        return loginUser;
                    }

                    JSONObject BaseResponse = result.getJSONObject("BaseResponse");
                    if (result == null) {
                        return loginUser;
                    }

                    int Ret = BaseResponse.getIntValue("Ret");
                    if (Ret != 0) {
                        return loginUser;
                    }

                    loginUser = result.getJSONObject("User");
                    return loginUser;
                }
            } finally {
                loginUserLock.unlock();
            }
        }
        return loginUser;
    }

    /**
     * 获取登录用户的昵称
     *
     * @return
     */
    public String getLoginUserNickName() {
        JSONObject loginUser = getLoginUser(false);
        if (loginUser == null) {
            return null;
        }
        return loginUser.getString("NickName");
    }

    /**
     * 获取联系人列表
     *
     * @param update
     * @return
     */
    public JSONArray getContactList(boolean update) {
        if (ContactList == null || update) {
            try {
                ContactListLock.lock();
                if (ContactList == null || update) {
                    JSONObject result = webWeixinApi.getContact(httpClient, passTicket, skey);
                    if (result == null) {
                        return ContactList;
                    }
                    JSONObject BaseResponse = result.getJSONObject("BaseResponse");
                    if (BaseResponse == null) {
                        return ContactList;
                    }
                    String Ret = BaseResponse.getString("Ret");
                    if (!"0".equals(Ret)) {
                        return ContactList;
                    }
                    ContactList = result.getJSONArray("MemberList");
                    return ContactList;
                }
            } finally {
                ContactListLock.unlock();
            }
        }
        return ContactList;
    }

    public void test() {
        JSONObject result = webWeixinApi.getContact(httpClient, passTicket, skey);
        System.out.println(result);
    }
}
