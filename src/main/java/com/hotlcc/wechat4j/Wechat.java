package com.hotlcc.wechat4j;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hotlcc.wechat4j.api.WebWeixinApi;
import com.hotlcc.wechat4j.enums.*;
import com.hotlcc.wechat4j.handler.ExitEventHandler;
import com.hotlcc.wechat4j.handler.ReceivedMsgHandler;
import com.hotlcc.wechat4j.model.BaseRequest;
import com.hotlcc.wechat4j.model.ReceivedMsg;
import com.hotlcc.wechat4j.model.UserInfo;
import com.hotlcc.wechat4j.model.WxMessage;
import com.hotlcc.wechat4j.util.*;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

    //认证码
    private volatile String wxsid;
    private volatile String passTicket;
    private volatile String skey;
    private volatile String wxuin;
    //用户数据
    private volatile UserInfo loginUser;
    private final Lock loginUserLock = new ReentrantLock();
    private volatile JSONObject SyncKey;
    private final Lock SyncKeyLock = new ReentrantLock();
    private volatile List<UserInfo> ContactList;
    private final Lock ContactListLock = new ReentrantLock();
    //在线状态
    private volatile boolean isOnline = false;
    private final Lock isOnlineLock = new ReentrantLock();
    //同步监听器
    private volatile SyncMonitor syncMonitor;

    //退出事件处理器
    private List<ExitEventHandler> exitEventHandlers;
    //接收消息处理器
    private List<ReceivedMsgHandler> receivedMsgHandlers;

    public Wechat(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
        this.httpClient = buildHttpClient(cookieStore);
    }

    public Wechat() {
        this(new BasicCookieStore());
    }

    public void setWebWeixinApi(WebWeixinApi webWeixinApi) {
        this.webWeixinApi = webWeixinApi;
    }

    public void addExitEventHandler(ExitEventHandler handler) {
        if (handler == null) {
            return;
        }
        if (exitEventHandlers == null) {
            exitEventHandlers = new ArrayList<>();
        }

        exitEventHandlers.add(handler);
    }

    public void addExitEventHandler(Collection<ExitEventHandler> handlers) {
        if (handlers == null || handlers.isEmpty()) {
            return;
        }
        if (exitEventHandlers == null) {
            exitEventHandlers = new ArrayList<>();
        }
        for (ExitEventHandler handler : handlers) {
            addExitEventHandler(handler);
        }
    }

    public void addReceivedMsgHandler(ReceivedMsgHandler handler) {
        if (handler == null) {
            return;
        }
        if (receivedMsgHandlers == null) {
            receivedMsgHandlers = new ArrayList<>();
        }
        receivedMsgHandlers.add(handler);
    }

    public void addReceivedMsgHandler(Collection<ReceivedMsgHandler> handlers) {
        if (handlers == null || handlers.isEmpty()) {
            return;
        }
        if (receivedMsgHandlers == null) {
            receivedMsgHandlers = new ArrayList<>();
        }
        for (ReceivedMsgHandler handler : handlers) {
            addReceivedMsgHandler(handler);
        }
    }

    private HttpClient buildHttpClient(CookieStore cookieStore) {
        ConnectionKeepAliveStrategy keepAliveStrategy = new DefaultConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                long keepAlive = super.getKeepAliveDuration(response, context);
                if (keepAlive == -1) {
                    //如果服务器没有设置keep-alive这个参数，我们就把它设置成1分钟
                    keepAlive = 60000;
                }
                return keepAlive;
            }
        };
        HttpRequestInterceptor interceptor = new HttpRequestInterceptor() {
            @Override
            public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {
                httpRequest.addHeader("User-Agent", PropertiesUtil.getProperty("wechat4j.userAgent"));
            }
        };
        HttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
//                .setKeepAliveStrategy(keepAliveStrategy)
                .addInterceptorFirst(interceptor)
                .build();
        return httpClient;
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

                CommonUtil.threadSleep(2000);
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
        JSONObject result = webWeixinApi.webWeixinInit(httpClient, passTicket, new BaseRequest(wxsid, skey, wxuin));
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

        loginUser = UserInfo.valueOf(result.getJSONObject("User"));
        SyncKey = result.getJSONObject("SyncKey");

        return true;
    }

    /**
     * 微信数据初始化（登录时）
     *
     * @return
     */
    private boolean wxInitWithRetry(PrintStream ps, int time) {
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

                CommonUtil.threadSleep(2000);
                continue;
            }

            ps.println("\t成功");
            ps.flush();

            return true;
        }
        return false;
    }

    /**
     * 开启状态通知
     *
     * @param time
     * @return
     */
    private boolean statusNotify(int time) {
        for (int i = 0; i < time; i++) {
            JSONObject result = webWeixinApi.statusNotify(httpClient, passTicket, new BaseRequest(wxsid, skey, wxuin), getLoginUserName(false));
            if (result == null) {
                continue;
            }

            JSONObject BaseResponse = result.getJSONObject("BaseResponse");
            if (result == null) {
                continue;
            }

            int Ret = BaseResponse.getIntValue("Ret");
            if (Ret != 0) {
                continue;
            }

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
        if (!wxInitWithRetry(ps, time)) {
            ps.println("初始化数据失败，请重新登录");
            ps.flush();
            return false;
        }
        ps.println("微信登录成功，欢迎你：" + getLoginUserNickName(false));
        ps.flush();

        try {
            isOnlineLock.lock();

            statusNotify(time);
            isOnline = true;
            syncMonitor = new SyncMonitor(this);
            syncMonitor.start();
        } finally {
            isOnlineLock.unlock();
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
        try {
            isOnlineLock.lock();

            webWeixinApi.logout(httpClient, new BaseRequest(wxsid, skey, wxuin));
            isOnline = false;
        } finally {
            isOnlineLock.unlock();
        }
    }

    /**
     * 判断在线状态
     *
     * @return
     */
    public boolean isOnline() {
        return isOnline;
    }

    /**
     * 微信同步监听器（心跳）
     */
    private class SyncMonitor extends Thread {
        private Wechat wechat;

        public SyncMonitor(Wechat wechat) {
            this.wechat = wechat;
        }

        @Override
        public void run() {
            int time = PropertiesUtil.getIntValue("wechat4j.syncCheck.retry.time", 5);
            int i = 0;
            while (isOnline) {
                long start = System.currentTimeMillis();

                try {
                    //API调用异常导致退出
                    JSONObject result = webWeixinApi.syncCheck(httpClient, new BaseRequest(wxsid, skey, wxuin), getSyncKeyList(false));
                    logger.debug("微信同步监听心跳返回数据：{}", result);
                    if (result == null) {
                        throw new RuntimeException("微信API调用异常");
                    } else {
                        i = 0;
                    }

                    //人为退出
                    int retcode = result.getIntValue("retcode");
                    if (retcode != RetcodeEnum.RECODE_0.getCode()) {
                        logger.info("微信退出或从其它设备登录");
                        logout();
                        processExitEvent(ExitTypeEnum.REMOTE_EXIT, null);
                        return;
                    }

                    int selector = result.getIntValue("selector");
                    processSelector(selector);
                } catch (Exception e) {
                    logger.error("同步监听心跳异常", e);

                    if (i == 0) {
                        logger.info("同步监听请求失败，正在重试...");
                    } else if (i > 0) {
                        logger.info("第{}次重试失败" + i);
                    }

                    if (i >= time) {
                        logger.info("重复{}次仍然失败，退出微信", i);
                        logout();
                        processExitEvent(ExitTypeEnum.ERROR_EXIT, e);
                        return;
                    }

                    i++;
                }

                //如果时间太短则阻塞2秒
                long end = System.currentTimeMillis();
                if (end - start < 2000) {
                    CommonUtil.threadSleep(2000);
                }
            }

            processExitEvent(ExitTypeEnum.LOCAL_EXIT, null);
        }

        /**
         * 处理退出事件
         */
        private void processExitEvent(ExitTypeEnum type, Throwable t) {
            try {
                if (exitEventHandlers == null) {
                    return;
                }

                for (ExitEventHandler handler : exitEventHandlers) {
                    if (handler != null) {
                        processExitEvent(type, t, handler);
                    }
                }
            } catch (Exception e) {
                logger.error("Exit event process error.", e);
            }
        }

        private void processExitEvent(ExitTypeEnum type, Throwable t, ExitEventHandler handler) {
            try {
                switch (type) {
                    case ERROR_EXIT:
                        handler.handleErrorExitEvent(wechat);
                        break;
                    case REMOTE_EXIT:
                        handler.handleRemoteExitEvent(wechat);
                        break;
                    case LOCAL_EXIT:
                        handler.handleLocalExitEvent(wechat);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                logger.error("Exit event process error.", e);
            }

            try {
                handler.handleAllType(wechat, type, t);
            } catch (Exception e) {
                logger.error("Exit event process error.", e);
            }
        }

        /**
         * 处理selector值
         *
         * @param selector
         */
        private void processSelector(int selector) {
            try {
                SelectorEnum e = SelectorEnum.valueOf(selector);
                if (e == null) {
                    logger.warn("Cannot process selector for error selector {}", selector);
                    return;
                }

                switch (e) {
                    case SELECTOR_0:
                        break;
                    case SELECTOR_2:
                        webWxSync();
                        break;
                    case SELECTOR_4:
                        break;
                    case SELECTOR_7:
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                logger.error("Execute processSelector error.", e);
            }
        }

        /**
         * 同步数据
         *
         * @return
         */
        private void webWxSync() {
            try {
                JSONObject result = webWeixinApi.webWxSync(httpClient, passTicket, new BaseRequest(wxsid, skey, wxuin), SyncKey);
                if (result == null) {
                    logger.error("从服务端同步新数据异常");
                    return;
                }

                JSONObject BaseResponse = result.getJSONObject("BaseResponse");
                if (BaseResponse == null) {
                    logger.warn("同步接口返回数据格式错误");
                    return;
                }

                int Ret = BaseResponse.getIntValue("Ret");
                if (Ret != RetcodeEnum.RECODE_0.getCode()) {
                    logger.warn("同步接口返回错误代码:{}", Ret);
                    return;
                }

                //新消息处理
                JSONArray AddMsgList = result.getJSONArray("AddMsgList");
                processNewMsg(AddMsgList);

                //更新SyncKey
                try {
                    SyncKeyLock.lock();
                    SyncKey = result.getJSONObject("SyncKey");
                } finally {
                    SyncKeyLock.unlock();
                }
            } catch (Exception e) {
                logger.error("Execute webWxSync error.", e);
            }
        }

        /**
         * 处理新消息
         *
         * @param AddMsgList
         */
        private void processNewMsg(JSONArray AddMsgList) {
            try {
                if (AddMsgList != null && !AddMsgList.isEmpty()) {
                    int len = AddMsgList.size();
                    logger.debug("收到{}条新消息", len);
                    for (int i = 0; i < len; i++) {
                        JSONObject AddMsg = AddMsgList.getJSONObject(i);
                        processNewMsg(AddMsg);
                    }
                }
            } catch (Exception e) {
                logger.error("Execute processNewMsg error.", e);
            }
        }

        private void processNewMsg(JSONObject AddMsg) {
            try {
                ReceivedMsg msg = ReceivedMsg.valueOf(AddMsg);
                processNewMsg(msg);
            } catch (Exception e) {
                logger.error("Execute processNewMsg error.", e);
            }
        }

        private void processNewMsg(ReceivedMsg msg) {
            try {
                if (receivedMsgHandlers == null) {
                    return;
                }
                for (ReceivedMsgHandler handler : receivedMsgHandlers) {
                    if (handler != null) {
                        processNewMsg(msg, handler);
                    }
                }
            } catch (Exception e) {
                logger.error("Execute processNewMsg error.", e);
            }
        }

        private void processNewMsg(ReceivedMsg msg, ReceivedMsgHandler handler) {
            try {
                if (handler != null) {
                    handler.handleAllType(wechat, msg);
                }
            } catch (Exception e) {

            }
        }
    }

    /**
     * 获取登录用户对象
     *
     * @return
     */
    public UserInfo getLoginUser(boolean update) {
        if (loginUser == null || update) {
            JSONObject result = webWeixinApi.webWeixinInit(httpClient, passTicket, new BaseRequest(wxsid, skey, wxuin));
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

            try {
                loginUserLock.lock();
                if (loginUser == null || update) {
                    loginUser = UserInfo.valueOf(result.getJSONObject("User"));
                }
            } finally {
                loginUserLock.unlock();
            }

            return loginUser;
        }
        return loginUser;
    }

    /**
     * 获取登录用户名
     *
     * @return
     */
    public String getLoginUserName(boolean update) {
        UserInfo loginUser = getLoginUser(update);
        if (loginUser == null) {
            return null;
        }
        return loginUser.getUserName();
    }

    /**
     * 获取登录用户的昵称
     *
     * @return
     */
    public String getLoginUserNickName(boolean update) {
        UserInfo loginUser = getLoginUser(update);
        if (loginUser == null) {
            return null;
        }
        return loginUser.getNickName();
    }

    /**
     * 获取SyncKey
     *
     * @param update
     * @return
     */
    private JSONObject getSyncKey(boolean update) {
        if (SyncKey == null || update) {
            JSONObject result = webWeixinApi.webWeixinInit(httpClient, passTicket, new BaseRequest(wxsid, skey, wxuin));
            if (result == null) {
                return SyncKey;
            }

            JSONObject BaseResponse = result.getJSONObject("BaseResponse");
            if (result == null) {
                return SyncKey;
            }

            int Ret = BaseResponse.getIntValue("Ret");
            if (Ret != 0) {
                return SyncKey;
            }

            try {
                SyncKeyLock.lock();
                if (SyncKey == null || update) {
                    SyncKey = result.getJSONObject("SyncKey");
                }
            } finally {
                SyncKeyLock.unlock();
            }

            return SyncKey;
        }
        return SyncKey;
    }

    /**
     * 获取SyncKey的List
     *
     * @param update
     * @return
     */
    private JSONArray getSyncKeyList(boolean update) {
        JSONObject SyncKey = getSyncKey(update);
        if (SyncKey == null) {
            return null;
        }
        return SyncKey.getJSONArray("List");
    }

    /**
     * 获取联系人列表
     *
     * @param update
     * @return
     */
    public List<UserInfo> getContactList(boolean update) {
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

            try {
                ContactListLock.lock();
                if (ContactList == null || update) {
                    ContactList = UserInfo.valueOf(result.getJSONArray("MemberList"));
                }
            } finally {
                ContactListLock.unlock();
            }

            return ContactList;
        }
        return ContactList;
    }

    /**
     * 根据UserName获取联系人信息
     *
     * @param update
     * @param UserName
     * @return
     */
    public UserInfo getContactByUserName(boolean update, String UserName) {
        if (StringUtil.isEmpty(UserName)) {
            return null;
        }

        List<UserInfo> list = getContactList(update);
        if (list == null) {
            return null;
        }

        for (UserInfo userInfo : list) {
            if (userInfo == null) {
                continue;
            }

            if (UserName.equals(userInfo.getUserName())) {
                return userInfo;
            }
        }

        return null;
    }

    /**
     * 根据NickName获取联系人信息
     *
     * @param update
     * @param NickName
     * @return
     */
    public UserInfo getContactByNickName(boolean update, String NickName) {
        if (StringUtil.isEmpty(NickName)) {
            return null;
        }

        List<UserInfo> list = getContactList(update);
        if (list == null) {
            return null;
        }

        for (UserInfo userInfo : list) {
            if (userInfo == null) {
                continue;
            }

            if (NickName.equals(userInfo.getNickName())) {
                return userInfo;
            }
        }

        return null;
    }

    /**
     * 根据RemarkName获取联系人信息
     *
     * @param update
     * @param RemarkName
     * @return
     */
    public UserInfo getContactByRemarkName(boolean update, String RemarkName) {
        if (StringUtil.isEmpty(RemarkName)) {
            return null;
        }

        List<UserInfo> list = getContactList(update);
        if (list == null) {
            return null;
        }

        for (UserInfo userInfo : list) {
            if (userInfo == null) {
                continue;
            }

            if (RemarkName.equals(userInfo.getRemarkName())) {
                return userInfo;
            }
        }

        return null;
    }

    /**
     * 发送文本消息
     *
     * @return
     */
    public JSONObject sendText(String content, String toUserName) {
        BaseRequest baseRequest = new BaseRequest(wxsid, skey, wxuin);

        String msgId = WechatUtil.createMsgId();
        String loginUserName = getLoginUserName(false);
        WxMessage message = new WxMessage();
        message.setClientMsgId(msgId);
        message.setContent(content);
        message.setFromUserName(loginUserName);
        message.setLocalID(msgId);
        if (StringUtil.isEmpty(toUserName)) {
            message.setToUserName(loginUserName);
        } else {
            message.setToUserName(toUserName);
        }
        message.setType(MsgTypeEnum.TEXT_MSG.getCode());

        JSONObject result = webWeixinApi.sendMsg(httpClient, passTicket, baseRequest, message);

        return result;
    }

    /**
     * 发送文本消息（根据昵称）
     *
     * @param content
     * @param nickName
     * @return
     */
    public JSONObject sendTextToNickName(String content, String nickName) {
        if (StringUtil.isEmpty(nickName)) {
            return sendText(content, null);
        }

        UserInfo userInfo = getContactByNickName(false, nickName);
        if (userInfo == null) {
            return null;
        }

        String userName = userInfo.getUserName();
        if (StringUtil.isEmpty(userName)) {
            return null;
        }

        return sendText(content, userName);
    }

    /**
     * 发送文本消息（根据备注名）
     *
     * @param content
     * @param remarkName
     * @return
     */
    public JSONObject sendTextToRemarkName(String content, String remarkName) {
        if (StringUtil.isEmpty(remarkName)) {
            return sendText(content, null);
        }

        UserInfo userInfo = getContactByRemarkName(false, remarkName);
        if (userInfo == null) {
            return null;
        }

        String userName = userInfo.getUserName();
        if (StringUtil.isEmpty(userName)) {
            return null;
        }

        return sendText(content, userName);
    }
}
