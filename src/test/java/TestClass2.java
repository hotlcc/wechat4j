import com.hotlcc.wechat4j.Wechat;
import com.hotlcc.wechat4j.api.WebWeixinApi;
import com.hotlcc.wechat4j.util.CommonUtil;

public class TestClass2 {
    public static void main(String[] args) {
        WebWeixinApi api = new WebWeixinApi();
        Wechat wechat = new Wechat();
        wechat.setWebWeixinApi(api);
        wechat.autoLogin();
        CommonUtil.threadSleep(1000 * 60 * 10);
        wechat.logout();
    }
}
