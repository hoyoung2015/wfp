package net.hoyoung.wfp.stockdown.greenorg;

import net.hoyoung.wfp.core.utils.JDBCHelper;
import org.springframework.jdbc.core.JdbcTemplate;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by hoyoung on 2015/10/28.
 */
public class BaiduRouteSpiderMultiThread2 implements PageProcessor{

    static JdbcTemplate jdbcTemplate;
    static {
        jdbcTemplate = JDBCHelper.createMysqlTemplate("mysql1",
                "jdbc:mysql://localhost/wfp?useUnicode=true&characterEncoding=utf8",
                "root", "", 5, 30);
    }

    BlockingQueue<Map<String,Object>> comOrgQueue = new LinkedBlockingQueue<>();

    @Override
    public void process(Page page) {

    }

    private Request generateReq(){
        jdbcTemplate.queryForList("SELECT id,stock_code,green_org_id FROM ");
    }


    @Override
    public Site getSite() {
        return null;
    }

    public static void main(String[] args) {

    }
}
