package net.hoyoung.wfp.stockdown;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.management.JMException;

import net.hoyoung.webmagic.pipeline.DBPipeline;
import net.hoyoung.wfp.core.service.CompanyInfoService;

import net.hoyoung.wfp.core.utils.JDBCHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;
import us.codecraft.webmagic.selector.JsonPathSelector;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.selector.XpathSelector;

/**
 * 企业基础信息爬取主类
 * 
 * @author hoyoung
 *
 */
public class CompanyInfoSpiderPageProcessor implements PageProcessor {
	// 1-141
	private static String JSON_LIST_URL = "http://stockdata.stock.hexun.com/gszl/data/jsondata/jbgk.ashx?count=20&page=";

	private static int PAGE_START = 1;
	private static int PAGE_END = 141;// 141

    static JdbcTemplate jdbcTemplate;
    static {
        jdbcTemplate = JDBCHelper.createMysqlTemplate("mysql1",
                "jdbc:mysql://localhost/wfp?useUnicode=true&characterEncoding=utf8",
                "root", "", 5, 30);
    }

	public static void main(String[] args) throws JMException {
		long start = System.currentTimeMillis();
		Spider spider = Spider.create(new CompanyInfoSpiderPageProcessor())
                .setScheduler(new FileCacheQueueScheduler("urls_cache"));
		for (int i = PAGE_START; i <= PAGE_END; i++) {
			spider.addUrl(JSON_LIST_URL + i);
//            break;
		}
		spider.thread(6).run();
		System.out.println("耗时:" + (System.currentTimeMillis() - start) / 1000
				+ "秒");
	}

	@Override
	public void process(Page page) {
        String jsonStr = page.getRawText();
        jsonStr = jsonStr.substring(14, jsonStr.length() - 1);
        List<String> comList = new JsonPathSelector("$.list")
                .selectList(jsonStr);
        JsonPathSelector Stockname_jps = new JsonPathSelector("$.Stockname");
        JsonPathSelector Institutional_jps = new JsonPathSelector(
                "$.Institutional");// 注册资本，单位万元
        JsonPathSelector lootchips_jps = new JsonPathSelector("$.lootchips");// 流通股本（亿股）
        JsonPathSelector Pricelimit_jps = new JsonPathSelector(
                "$.Pricelimit");// 总股本（亿股）
        JsonPathSelector shareholders_jps = new JsonPathSelector(
                "$.shareholders");// 流通市值（亿元）

        JsonPathSelector StockNameLink_jps = new JsonPathSelector(
                "$.StockNameLink");
        JsonPathSelector deviation_jps = new JsonPathSelector(
                "$.deviation");
        for (String s : comList) {
            String Stockname = Stockname_jps.select(s);
            int t = Stockname.indexOf("(");
            String sname = Stockname.substring(0, t);
            String stock_code = Stockname.substring(t + 1,
                    Stockname.length() - 1);
            String industry = new XpathSelector("//a/text()").select(deviation_jps.select(s)).replaceAll(" ", "");

            String t_institutional = Institutional_jps.select(s);// 注册资本
            double institutional = 0;
            try {
                institutional = Double.parseDouble(t_institutional);
            } catch (NumberFormatException e) {
                institutional = 0;
            }
            float lootchips = getFloat(lootchips_jps.select(s));
            float pricelimit = getFloat(Pricelimit_jps.select(s));
            float shareholders = getFloat(shareholders_jps.select(s));


            int status = jdbcTemplate.update("INSERT INTO company_info(sname,stock_code,industry,institutional,lootchips,pricelimit,shareholders) values(?,?,?,?,?,?,?)",
                    sname,
                    stock_code,
                    industry,
                    institutional,
                    lootchips,
                    pricelimit,
                    shareholders);
            if(status==1){
                System.out.println(">>>>>>>>>>>>>>>>>> "+stock_code+" insert successful");
            }
        }
	}
    private Site site = Site.me()
            .setRetryTimes(5)
            .setSleepTime(300)
            .addHeader("Host", "stockdata.stock.hexun.com")
            .addHeader(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");
    @Override
	public Site getSite() {
		return site;
	}



	private float getFloat(String s) {
		float r;
		try {
			r = Float.parseFloat(s);
		} catch (NumberFormatException e) {
			r = 0f;
		}
		return r;
	}
}
