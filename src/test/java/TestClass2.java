import com.hotlcc.wechat4j.Wechat;
import com.hotlcc.wechat4j.api.WebWeixinApi;

public class TestClass2 {
    public static void main(String[] args) {
        WebWeixinApi api = new WebWeixinApi();
        Wechat wechat = new Wechat();
        wechat.setWebWeixinApi(api);
        System.out.println(wechat.autoLogin());
        wechat.test();
        wechat.logout();
    }
}
