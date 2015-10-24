package net.hoyoung.wfp.stockdown;

import net.hoyoung.wfp.core.utils.JDBCHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;

import javax.management.JMException;
import java.util.List;
import java.util.Map;

/**
 * 企业基础信息爬取主类
 *
 * @author hoyoung
 *
 */
public class CompanyInfoLocationSpiderPageProcessor2 implements PageProcessor {
    static JdbcTemplate jdbcTemplate;
    static {
        jdbcTemplate = JDBCHelper.createMysqlTemplate("mysql1",
                "jdbc:mysql://localhost/wfp?useUnicode=true&characterEncoding=utf8",
                "root", "", 5, 30);
    }

    public static void main(String[] args) throws JMException {
        long start = System.currentTimeMillis();
        Spider spider = Spider.create(new CompanyInfoLocationSpiderPageProcessor2())
                .setScheduler(new FileCacheQueueScheduler("urls_cache"));

        List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT stock_code,name,addr_reg,area FROM company_info WHERE pos_x is null");

        for (Map<String,Object> com : list){
            String addr_reg = (String) com.get("addr_reg");
            String name = (String) com.get("name");
            String area = (String) com.get("area");
            if(!StringUtils.isEmpty(addr_reg)){
                Request req = new Request("http://api.map.baidu.com/place/v2/search?q="+addr_reg+"&region="+area+"&output=json&ak=i39t59l7L6nzXlOZCfzwUFsK");
                req.putExtra("stock_code",com.get("stock_code"));
                spider.addRequest(req);
            }
//            break;
        }
        spider.thread(10).run();
        System.out.println("耗时:" + (System.currentTimeMillis() - start) / 1000
                + "秒");
    }

    @Override
    public void process(Page page) {
        String status = page.getJson().jsonPath("$.status").get();
        String t_lng = page.getJson().jsonPath("$.results[0].location.lng").get();
        String t_lat = page.getJson().jsonPath("$.results[0].location.lat").get();
        if(!org.apache.commons.lang3.StringUtils.isEmpty(t_lng) && !org.apache.commons.lang3.StringUtils.isEmpty(t_lat)){
            String stock_code = (String) page.getRequest().getExtra("stock_code");
            float pos_x = Float.parseFloat(t_lng);
            float pos_y = Float.parseFloat(t_lat);
            int rs = jdbcTemplate.update("UPDATE company_info SET pos_x=?,pos_y=? where stock_code=?",
                    pos_x,
                    pos_y,
                    stock_code);
            if(rs==1){
                System.out.println(">>>>>>>>>>> "+stock_code+" "+1+" update success");
            }
        }
    }
    private Site site = Site.me()
            .setRetryTimes(5)
            .setSleepTime(300)
            .addHeader("Host", "api.map.baidu.com")
            .addHeader(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");
    //    @Override
    public Site getSite() {
        return site;
    }

}
