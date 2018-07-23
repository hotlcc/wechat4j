package com.hotlcc.wechat4j.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hotlcc.wechat4j.enums.LoginTipEnum;
import com.hotlcc.wechat4j.util.PropertiesUtil;
import com.hotlcc.wechat4j.util.StringUtil;
import com.hotlcc.wechat4j.util.WechatUtil;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * web微信接口封装
 *
 * @author Allen
 */
@SuppressWarnings("Duplicates")
public class WebWeixinApi {
    private static Logger logger = LoggerFactory.getLogger(WebWeixinApi.class);

    //预编译正则匹配
    private static Pattern PATTERN_UUID_1 = Pattern.compile("window.QRLogin.code = (\\d+);");
    private static Pattern PATTERN_UUID_2 = Pattern.compile("window.QRLogin.code = (\\d+); window.QRLogin.uuid = \"(\\S+?)\";");
    private static Pattern PATTERN_REDIRECT_URI_1 = Pattern.compile("window.code=(\\d+);");
    private static Pattern PATTERN_REDIRECT_URI_2 = Pattern.compile("window.code=(\\d+);\\s*window.redirect_uri=\"(\\S+?)\";");

    /**
     * 获取微信uuid
     */
    public JSONObject getWxUuid(HttpClient httpClient) {
        try {
            String url = new ST(PropertiesUtil.getProperty("webwx-url.uuid_url"))
                    .add("appid", PropertiesUtil.getProperty("webwx.appid"))
                    .add("_", System.currentTimeMillis())
                    .render();

            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("User-Agent", PropertiesUtil.getProperty("wechat4j.userAgent"));
            httpGet.setConfig(RequestConfig.custom().setRedirectsEnabled(false).build());

            HttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK != statusCode) {
                throw new RuntimeException("响应失败(" + statusCode + ")");
            }

            HttpEntity entity = response.getEntity();
            String res = EntityUtils.toString(entity, Consts.UTF_8);

            Matcher matcher = PATTERN_UUID_1.matcher(res);
            if (!matcher.find()) {
                throw new RuntimeException("返回数据错误");
            }

            String code = matcher.group(1);
            JSONObject result = new JSONObject();
            result.put("code", code);
            if (!"200".equals(code)) {
                result.put("msg", "返回code错误，请确认appid是否有效");
                return result;
            }

            matcher = PATTERN_UUID_2.matcher(res);
            if (!matcher.find()) {
                throw new RuntimeException("没有匹配到uuid");
            }

            String uuid = matcher.group(2);
            result.put("uuid", uuid);
            if (StringUtil.isEmpty(uuid)) {
                throw new RuntimeException("获取的uuid为空");
            }

            return result;
        } catch (Exception e) {
            logger.error("获取uuid异常", e);
            return null;
        }
    }

    /**
     * 获取二维码
     *
     * @param uuid
     */
    public byte[] getQR(HttpClient httpClient,
                        String uuid) {
        try {
            String url = new ST(PropertiesUtil.getProperty("webwx-url.qrcode_url"))
                    .add("uuid", uuid)
                    .render();

            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("User-Agent", PropertiesUtil.getProperty("wechat4j.userAgent"));
            httpGet.setConfig(RequestConfig.custom().setRedirectsEnabled(false).build());

            HttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK != statusCode) {
                throw new RuntimeException("响应失败(" + statusCode + ")");
            }

            HttpEntity entity = response.getEntity();
            byte[] data = EntityUtils.toByteArray(entity);
            if (data == null || data.length <= 0) {
                throw new RuntimeException("二维码数据为空");
            }

            return data;
        } catch (Exception e) {
            logger.error("获取二维码异常", e);
            return null;
        }
    }

    /**
     * 获取跳转uri（等待扫码认证）
     *
     * @return
     */
    public JSONObject getRedirectUri(HttpClient httpClient,
                                     LoginTipEnum tip,
                                     String uuid) {
        try {
            long millis = System.currentTimeMillis();
            String url = new ST(PropertiesUtil.getProperty("webwx-url.redirect_uri"))
                    .add("tip", tip.getCode())
                    .add("uuid", uuid)
                    .add("r", millis / 1252L)
                    .add("_", millis)
                    .render();

            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("User-Agent", PropertiesUtil.getProperty("wechat4j.userAgent"));
            httpGet.setConfig(RequestConfig.custom().setRedirectsEnabled(false).build());

            HttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK != statusCode) {
                throw new RuntimeException("响应失败(" + statusCode + ")");
            }

            HttpEntity entity = response.getEntity();
            String res = EntityUtils.toString(entity, Consts.UTF_8);

            Matcher matcher = PATTERN_REDIRECT_URI_1.matcher(res);
            if (!matcher.find()) {
                throw new RuntimeException("返回数据错误");
            }

            String code = matcher.group(1);
            JSONObject result = new JSONObject();
            result.put("code", code);
            if ("408".equals(code)) {
                result.put("msg", "请扫描二维码");
            } else if ("400".equals(code)) {
                result.put("msg", "二维码失效");
            } else if ("201".equals(code)) {
                result.put("msg", "请在手机上点击确认");
            } else if ("200".equals(code)) {
                matcher = PATTERN_REDIRECT_URI_2.matcher(res);
                if (!matcher.find()) {
                    throw new RuntimeException("没有匹配到跳转uri");
                }
                result.put("msg", "手机确认成功");
                result.put("redirectUri", matcher.group(2));
            } else {
                throw new RuntimeException("返回code错误");
            }

            return result;
        } catch (Exception e) {
            logger.error("获取跳转uri异常", e);
            return null;
        }
    }

    /**
     * 获取登录认证码
     * 此方法执行后，其它web端微信、pc端都会下线
     */
    public JSONObject getLoginCode(HttpClient httpClient,
                                   String redirectUri) {
        try {
            String url = new ST(PropertiesUtil.getProperty("webwx-url.newlogin_url"))
                    .add("redirectUri", redirectUri)
                    .render();

            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("User-Agent", PropertiesUtil.getProperty("wechat4j.userAgent"));
            httpGet.setConfig(RequestConfig.custom().setRedirectsEnabled(false).build());

            HttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK != statusCode) {
                throw new RuntimeException("响应失败(" + statusCode + ")");
            }

            HttpEntity entity = response.getEntity();
            String res = EntityUtils.toString(entity, Consts.UTF_8);

            JSONObject result = JSONObject.parseObject(XML.toJSONObject(res).toString()).getJSONObject("error");

            return result;
        } catch (Exception e) {
            logger.error("获取登录认证码异常", e);
            return null;
        }
    }

    /**
     * 退出登录
     */
    public void logout(HttpClient httpClient,
                       String wxsid,
                       String skey,
                       String wxuin) {
        try {
            List<NameValuePair> pairList = new ArrayList<>();
            pairList.add(new BasicNameValuePair("sid", wxsid));
            pairList.add(new BasicNameValuePair("uin", wxuin));

            //分两步进行
            for (int i = 0; i <= 1; i++) {
                String url = new ST(PropertiesUtil.getProperty("webwx-url.logout_url"))
                        .add("type", i)
                        .add("skey", StringUtil.encodeURL(skey, Consts.UTF_8.name()))
                        .render();

                HttpPost httpPost = new HttpPost(url);
                httpPost.setHeader("User-Agent", PropertiesUtil.getProperty("wechat4j.userAgent"));
                httpPost.setHeader("Content-type", ContentType.APPLICATION_FORM_URLENCODED.toString());

                HttpEntity paramEntity = new UrlEncodedFormEntity(pairList);
                httpPost.setEntity(paramEntity);

                httpClient.execute(httpPost);
            }
        } catch (Exception e) {
            logger.error("退出登录异常", e);
        }
    }

    /**
     * 获取初始化数据
     */
    public JSONObject webWeixinInit(HttpClient httpClient,
                                    String passticket,
                                    String wxsid,
                                    String skey,
                                    String wxuin) {
        try {
            String url = new ST(PropertiesUtil.getProperty("webwx-url.webwxinit_url"))
                    .add("pass_ticket", passticket)
                    .add("r", System.currentTimeMillis() / 1252L)
                    .render();

            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("User-Agent", PropertiesUtil.getProperty("wechat4j.userAgent"));
            httpPost.setHeader("Content-type", ContentType.APPLICATION_JSON.toString());

            JSONObject paramJson = new JSONObject();
            paramJson.put("BaseRequest", WechatUtil.createBaseRequest(wxsid, skey, wxuin));
            HttpEntity paramEntity = new StringEntity(paramJson.toJSONString(), Consts.UTF_8);
            httpPost.setEntity(paramEntity);

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK != statusCode) {
                throw new RuntimeException("响应失败(" + statusCode + ")");
            }

            HttpEntity entity = response.getEntity();
            String res = EntityUtils.toString(entity, Consts.UTF_8);

            return JSONObject.parseObject(res);
        } catch (Exception e) {
            logger.error("获取初始化数据异常", e);
            return null;
        }
    }

    /**
     * 开启消息状态通知
     *
     * @return
     */
    public JSONObject statusNotify(HttpClient httpClient,
                                   String passticket,
                                   String wxsid,
                                   String skey,
                                   String wxuin,
                                   String loginUserName) {
        try {
            String url = new ST(PropertiesUtil.getProperty("webwx-url.statusnotify_url"))
                    .add("pass_ticket", passticket)
                    .render();

            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("User-Agent", PropertiesUtil.getProperty("wechat4j.userAgent"));
            httpPost.setHeader("Content-type", ContentType.APPLICATION_JSON.toString());

            JSONObject paramJson = new JSONObject();
            paramJson.put("BaseRequest", WechatUtil.createBaseRequest(wxsid, skey, wxuin));
            paramJson.put("ClientMsgId", System.currentTimeMillis());
            paramJson.put("Code", 3);
            paramJson.put("FromUserName", loginUserName);
            paramJson.put("ToUserName", loginUserName);
            HttpEntity paramEntity = new StringEntity(paramJson.toJSONString(), Consts.UTF_8);
            httpPost.setEntity(paramEntity);

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK != statusCode) {
                throw new RuntimeException("响应失败(" + statusCode + ")");
            }

            HttpEntity entity = response.getEntity();
            String res = EntityUtils.toString(entity, Consts.UTF_8);

            return JSONObject.parseObject(res);
        } catch (Exception e) {
            logger.error("开启消息状态通知异常", e);
            return null;
        }
    }

    /**
     * 服务端状态同步心跳
     */
    public JSONObject syncCheck(HttpClient httpClient,
                                String wxsid,
                                String skey,
                                String wxuin,
                                JSONArray SyncKeyList) {
        try {
            long millis = System.currentTimeMillis();
            String url = new ST(PropertiesUtil.getProperty("webwx-url.synccheck_url"))
                    .add("r", millis)
                    .add("skey", StringUtil.encodeURL(skey, Consts.UTF_8.name()))
                    .add("sid", wxsid)
                    .add("uin", wxuin)
                    .add("deviceid", WechatUtil.createDeviceID())
                    .add("synckey", StringUtil.encodeURL(WechatUtil.syncKeyListToString(SyncKeyList), Consts.UTF_8.name()))
                    .add("_", millis)
                    .render();

            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("User-Agent", PropertiesUtil.getProperty("wechat4j.userAgent"));
            httpGet.setConfig(RequestConfig.custom().setRedirectsEnabled(false).build());

            HttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK != statusCode) {
                throw new RuntimeException("响应失败(" + statusCode + ")");
            }

            HttpEntity entity = response.getEntity();
            String res = EntityUtils.toString(entity, Consts.UTF_8);

            String regExp = "window.synccheck=\\{retcode:\"(\\d+)\",selector:\"(\\d+)\"}";
            Matcher matcher = Pattern.compile(regExp).matcher(res);
            if (!matcher.find()) {
                throw new RuntimeException("返回数据错误");
            }

            JSONObject result = new JSONObject();
            result.put("retcode", matcher.group(1));
            result.put("selector", matcher.group(2));

            return result;
        } catch (Exception e) {
            logger.error("服务端状态同步异常", e);
            return null;
        }
    }

    /**
     * 获取全部联系人列表
     */
    public JSONObject getContact(HttpClient httpClient,
                                 String passticket,
                                 String skey) {
        try {
            String url = new ST(PropertiesUtil.getProperty("webwx-url.getcontact_url"))
                    .add("pass_ticket", StringUtil.encodeURL(passticket, Consts.UTF_8.name()))
                    .add("r", System.currentTimeMillis())
                    .add("skey", StringUtil.encodeURL(skey, Consts.UTF_8.name()))
                    .render();

            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("User-Agent", PropertiesUtil.getProperty("wechat4j.userAgent"));
            httpGet.setConfig(RequestConfig.custom().setRedirectsEnabled(false).build());
            HttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK != statusCode) {
                throw new RuntimeException("响应失败(" + statusCode + ")");
            }

            HttpEntity entity = response.getEntity();
            String res = EntityUtils.toString(entity, Consts.UTF_8);

            return JSONObject.parseObject(res);
        } catch (Exception e) {
            logger.error("获取全部联系人列表异常", e);
            return null;
        }
    }

    /**
     * 批量获取指定用户信息
     */
    public JSONObject batchGetContact(HttpClient httpClient,
                                      String passticket,
                                      String wxsid,
                                      String skey,
                                      String wxuin,
                                      JSONArray batchContactList) {
        try {
            String url = new ST(PropertiesUtil.getProperty("webwx-url.batchgetcontact_url"))
                    .add("pass_ticket", StringUtil.encodeURL(passticket, Consts.UTF_8.name()))
                    .add("r", System.currentTimeMillis())
                    .render();

            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("User-Agent", PropertiesUtil.getProperty("wechat4j.userAgent"));
            httpPost.setHeader("Content-type", ContentType.APPLICATION_JSON.toString());

            JSONObject paramJson = new JSONObject();
            paramJson.put("BaseRequest", WechatUtil.createBaseRequest(wxsid, skey, wxuin));
            paramJson.put("Count", batchContactList.size());
            paramJson.put("List", batchContactList);
            HttpEntity paramEntity = new StringEntity(paramJson.toJSONString(), Consts.UTF_8);
            httpPost.setEntity(paramEntity);

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK != statusCode) {
                throw new RuntimeException("响应失败(" + statusCode + ")");
            }

            HttpEntity entity = response.getEntity();
            String res = EntityUtils.toString(entity, Consts.UTF_8);

            return JSONObject.parseObject(res);
        } catch (Exception e) {
            logger.error("批量获取指定联系人信息异常", e);
            return null;
        }
    }

    /**
     * 从服务端拉取新消息
     */
    public JSONObject pullNewMsg(HttpClient httpClient,
                                 String passticket,
                                 String wxsid,
                                 String skey,
                                 String wxuin,
                                 JSONObject SyncKey) {
        try {
            String url = new ST(PropertiesUtil.getProperty("webwx-url.webwxsync_url"))
                    .add("skey", skey)
                    .add("sid", wxsid)
                    .add("pass_ticket", passticket)
                    .render();

            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("User-Agent", PropertiesUtil.getProperty("wechat4j.userAgent"));
            httpPost.setHeader("Content-type", ContentType.APPLICATION_JSON.toString());

            JSONObject paramJson = new JSONObject();
            paramJson.put("BaseRequest", WechatUtil.createBaseRequest(wxsid, skey, wxuin));
            paramJson.put("SyncKey", SyncKey);
            HttpEntity paramEntity = new StringEntity(paramJson.toJSONString(), Consts.UTF_8);
            httpPost.setEntity(paramEntity);

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK != statusCode) {
                throw new RuntimeException("响应失败(" + statusCode + ")");
            }

            HttpEntity entity = response.getEntity();
            String res = EntityUtils.toString(entity, Consts.UTF_8);

            return JSONObject.parseObject(res);
        } catch (Exception e) {
            logger.error("从服务端拉取新消息异常", e);
            return null;
        }
    }

    /**
     * 发送消息
     */
    public JSONObject sendMsg(HttpClient httpClient,
                              String passticket,
                              String wxsid,
                              String skey,
                              String wxuin,
                              String Content,
                              int Type,
                              String FromUserName,
                              String ToUserName) {
        try {
            String url = new ST(PropertiesUtil.getProperty("webwx-url.webwxsendmsg_url"))
                    .add("pass_ticket", passticket)
                    .render();

            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("User-Agent", PropertiesUtil.getProperty("wechat4j.userAgent"));
            httpPost.setHeader("Content-type", ContentType.APPLICATION_JSON.toString());

            JSONObject paramJson = new JSONObject();
            paramJson.put("BaseRequest", WechatUtil.createBaseRequest(wxsid, skey, wxuin));
            paramJson.put("Msg", WechatUtil.createSendMsg(Content, Type, FromUserName, ToUserName));
            paramJson.put("Scene", 0);
            HttpEntity paramEntity = new StringEntity(paramJson.toJSONString(), Consts.UTF_8);
            httpPost.setEntity(paramEntity);

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK != statusCode) {
                throw new RuntimeException("响应失败(" + statusCode + ")");
            }

            HttpEntity entity = response.getEntity();
            String res = EntityUtils.toString(entity, Consts.UTF_8);

            JSONObject result = JSONObject.parseObject(res);

            return result;
        } catch (Exception e) {
            logger.error("发送消息异常", e);
            return null;
        }
    }
}
