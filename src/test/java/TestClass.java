import com.hotlcc.wechat4j.Wechat;
import com.hotlcc.wechat4j.api.WebWeixinApi;
import com.hotlcc.wechat4j.util.QRCodeUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

public class TestClass {
    private static Logger logger = LoggerFactory.getLogger(TestClass.class);

    public void test03() throws IOException {
        BufferedImage image = ImageIO.read(new FileInputStream("D:/2.jpg"));
        System.out.println(QRCodeUtil.toCharMatrix(image));
    }

    public void test04() {
        WebWeixinApi api = new WebWeixinApi();
        Wechat wechat = new Wechat();
        wechat.setWebWeixinApi(api);
        wechat.test();
    }

    @Test
    public void test05() {
        WebWeixinApi api = new WebWeixinApi();
        Wechat wechat = new Wechat();
        wechat.setWebWeixinApi(api);
        System.out.println(wechat.autoLogin());
    }
}
