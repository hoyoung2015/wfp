package net.hoyoung.wfp.stockdown;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.management.JMException;

import net.hoyoung.webmagic.pipeline.DBPipeline;
import net.hoyoung.wfp.core.service.CompanyInfoService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.processor.PageProcessor;
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
	private static String[] config = { "classpath:spring-core.xml" };
	public static ApplicationContext APP_CONTEXT;
	// 1-141
	private static String JSON_LIST_URL = "http://stockdata.stock.hexun.com/gszl/data/jsondata/jbgk.ashx?count=20&page=";
	static {
		APP_CONTEXT = new FileSystemXmlApplicationContext(config);
	}
	private static int PAGE_START = 1;
	private static int PAGE_END = 141;// 141
	private static String StockNameLinkPath = "http://stockdata.stock.hexun.com/gszl/";

	public static void main(String[] args) throws JMException {
		long start = System.currentTimeMillis();
		DBPipeline dBPipeline = new DBPipeline(
				APP_CONTEXT.getBean(CompanyInfoService.class));
		Spider spider = Spider.create(new CompanyInfoSpiderPageProcessor())
				.addPipeline(dBPipeline);

		for (int i = PAGE_START; i <= PAGE_END; i++) {
			spider.addUrl(JSON_LIST_URL + i);
		}
		SpiderMonitor.instance().register(spider);//爬虫监控
		spider.thread(5).run();
		System.out.println("耗时:" + (System.currentTimeMillis() - start) / 1000
				+ "秒");
	}

	private int count = 0;

	@Override
	public void process(Page page) {
		if (page.getUrl().regex( ".*/gszl/data/jsondata/jbgk.ashx.*").match()) { //json列表
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
				String stockCode = Stockname.substring(t + 1,
						Stockname.length() - 1);

				
				String url = StockNameLinkPath + StockNameLink_jps.select(s);
				Request req = new Request(url);

				req.putExtra("industry", new XpathSelector("//a/text()").select(deviation_jps.select(s)).replaceAll(" ", ""));
				req.putExtra("sname", sname);
				req.putExtra("stockCode", stockCode);

				String Institutional = Institutional_jps.select(s);// 注册资本
				double institutional = 0;
				try {
					institutional = Double.parseDouble(Institutional);
				} catch (NumberFormatException e) {
					institutional = 0;
				}
				req.putExtra("institutional", institutional);// 注册资本
				req.putExtra("lootchips", getFloat(lootchips_jps.select(s)));// 流通股本
				req.putExtra("pricelimit", getFloat(Pricelimit_jps.select(s)));// 总股本（亿股）
				req.putExtra("shareholders",
						getFloat(shareholders_jps.select(s)));// 流通市值（亿元）
//				System.err.println(req.getExtras().toString());
				page.addTargetRequest(req);
			}

		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			List<Selectable> tableSelector = page.getHtml().xpath("//table[@class='tab_xtable']").nodes();
			Selectable infoSelect = tableSelector.get(0);
			String name = infoSelect.xpath("/table/tbody/tr[3]/td[2]/text()").get();
			page.putField("name", name);
			String ename = infoSelect.xpath("/table/tbody/tr[4]/td[2]/text()").get();
			page.putField("ename", ename);
			String registerDateStr = infoSelect.xpath("/table/tbody/tr[6]/td[2]/a/text()").get();
			try {
				Date registerDate = sdf.parse(registerDateStr);
				page.putField("registerDate", registerDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String area = infoSelect.xpath("/table/tbody/tr[9]/td[2]/a/text()").get();
			page.putField("area", area);
			//工商信息
			Selectable gsSelect = tableSelector.get(1);
			String addr = gsSelect.xpath("/table/tbody/tr[4]/td[2]/text()").get();
			page.putField("addr", addr);
			//证券信息
			Selectable zqSelect = tableSelector.get(2);
			String market = zqSelect.xpath("/table/tbody/tr[3]/td[2]/a/text()").get();
			page.putField("market", market);
			try {
				Date listingDate = sdf.parse(zqSelect.xpath("/table/tbody/tr[2]/td[2]/text()").get());
				page.putField("listingDate", listingDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			try {
				Date offerDate = sdf.parse(zqSelect.xpath("/table/tbody/tr[1]/td[2]/text()").get());
				page.putField("offerDate", offerDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String stockType = zqSelect.xpath("/table/tbody/tr[4]/td[2]/text()").get();
			page.putField("stockType", stockType);
			//联系信息
			Selectable contactSelect = tableSelector.get(3);
			String webSite = contactSelect.xpath("/table/tbody/tr[4]/td[2]/a/@href").get();
			page.putField("webSite", webSite);
			
			for (Entry<String, Object> map : page.getRequest().getExtras().entrySet()) {
				page.putField(map.getKey(), map.getValue());
			}
		}
	}

	@Override
	public Site getSite() {
		site.addHeader("Host", "stockdata.stock.hexun.com");
		site.addHeader(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");
		return site;
	}

	private Site site = Site.me().setRetryTimes(5).setSleepTime(200);

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
