package com.hotlcc.wechat4j;

import com.hotlcc.wechat4j.api.WebWeixinApi;
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
}
