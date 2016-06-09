package net.hoyoung.wfp.stockdown.spider;

import net.hoyoung.wfp.core.utils.JDBCHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;
import us.codecraft.webmagic.selector.JsonPathSelector;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.selector.XpathSelector;

import javax.management.JMException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 企业基础信息爬取主类
 name,
 ename,
 register_date,
 area,
 addr_reg,
 addr_work,
 market,
 listing_date,
 offer_date,
 stock_type,
 stock_code
 * @author hoyoung
 *
 */
public class CompanyInfoDetailSpiderPageProcessor implements PageProcessor {
    static Logger logger = LoggerFactory.getLogger(CompanyInfoDetailSpiderPageProcessor.class);
    static JdbcTemplate jdbcTemplate;
    static {
        jdbcTemplate = JDBCHelper.createMysqlTemplate("mysql1",
                "jdbc:mysql://localhost/wfp?useUnicode=true&characterEncoding=utf8",
                "root", "", 5, 30);
    }

	public static void main(String[] args) throws JMException {
		long start = System.currentTimeMillis();
		Spider spider = Spider.create(new CompanyInfoDetailSpiderPageProcessor())
                .setScheduler(new FileCacheQueueScheduler("urls_cache"));

        List<String> list = jdbcTemplate.queryForList("SELECT stock_code FROM company_info where name is NULL", String.class);
//        List<String> list = new ArrayList<String>();
//        list.add("600291");

        for (String stock_code : list){
            Request req = new Request("http://stockdata.stock.hexun.com/gszl/s"+stock_code+".shtml");
            req.putExtra("stock_code",stock_code);
            spider.addRequest(req);
//            break;
        }
		spider.thread(6).run();
        logger.info("耗时:" + (System.currentTimeMillis() - start) / 1000
                + "秒");
	}

	@Override
	public void process(Page page) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<Selectable> tableSelector = page.getHtml().xpath("//table[@class='tab_xtable']").nodes();
        Selectable infoSelect = tableSelector.get(0);
        String name = infoSelect.xpath("/table/tbody/tr[3]/td[2]/text()").get();
        String ename = infoSelect.xpath("/table/tbody/tr[4]/td[2]/text()").get();
        String registerDateStr = infoSelect.xpath("/table/tbody/tr[6]/td[2]/a/text()").get();
        Date register_date = null;
        if(registerDateStr != null){
            try {
                register_date = sdf.parse(registerDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String area = infoSelect.xpath("/table/tbody/tr[9]/td[2]/a/text()").get();
        //工商信息
        Selectable gsSelect = tableSelector.get(1);
        String addr_reg = gsSelect.xpath("/table/tbody/tr[2]/td[2]/text()").get();
        String addr_work = gsSelect.xpath("/table/tbody/tr[4]/td[2]/text()").get();
        //证券信息
        Selectable zqSelect = tableSelector.get(2);
        String market = zqSelect.xpath("/table/tbody/tr[3]/td[2]/a/text()").get();
        Date listing_date = null;

        String t_date = zqSelect.xpath("/table/tbody/tr[2]/td[2]/text()").get();
        if(!StringUtils.isEmpty(t_date)){
            try {
                listing_date = sdf.parse(t_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Date offer_date = null;
        t_date = zqSelect.xpath("/table/tbody/tr[1]/td[2]/text()").get();
        if(!StringUtils.isEmpty(t_date)){
            try {
                offer_date = sdf.parse(t_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        String stock_type = zqSelect.xpath("/table/tbody/tr[4]/td[2]/text()").get();
        //联系信息
        Selectable contactSelect = tableSelector.get(3);
        String web = contactSelect.xpath("/table/tbody/tr[4]/td[2]/a/@href").get();

        String stock_code = (String) page.getRequest().getExtra("stock_code");


        int status = jdbcTemplate.update("UPDATE company_info SET name=?,ename=?,register_date=?,area=?,addr_reg=?,addr_work=?,market=?,listing_date=?,offer_date=?,stock_type=? where stock_code=?",
                name,
                ename,
                register_date,
                area,
                addr_reg,
                addr_work,
                market,
                listing_date,
                offer_date,
                stock_type,
                stock_code);
        if(status==1){
            logger.info(stock_code+" "+name+" update success");
        }
    }
    private Site site = Site.me()
            .setRetryTimes(5)
            .setSleepTime(1000)
            .addHeader("Host", "stockdata.stock.hexun.com")
            .addHeader(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");
    @Override
	public Site getSite() {
		return site;
	}

}
