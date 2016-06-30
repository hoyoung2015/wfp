package net.hoyoung.wfp.stockdown.spider;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.JMException;

import net.hoyoung.webmagic.downloader.HtmlUnitDownloader;
import net.hoyoung.webmagic.pipeline.SocialReportDetailPipeline;
import net.hoyoung.wfp.core.entity.SocialReportSyn;
import net.hoyoung.wfp.core.service.SocialReportSynService;
import net.hoyoung.wfp.core.utils.StringUtils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.BasicDBObject;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

/**
 * 员工社会责任报告明细爬虫
 * 在企业年度综合社会责任报告的基础上爬去明细的报告
 * 由于数据项太多，存储在mongodb中
 * @author hoyoung
 *
 */

public class CompanySocialReportDetailSprderPageProcessor implements PageProcessor {
	
	@Override
	public void process(Page page) {
		BasicDBObject dbo = new BasicDBObject("stockCode",page.getRequest().getExtra("stockCode"));
		
		List<Selectable> h_section2_lis = page.getHtml().xpath("//div[@class='h_section2']/ul/li").nodes();
		for(int i=1;i<=3;i++){
			Selectable li = h_section2_lis.get(i);
			String s = li.xpath("/li/span[1]/text()").get().replaceAll("：|\\s", "");
			String s2 = li.xpath("/li/span[2]/text()").get().replaceAll("：|\\s", "");
			dbo.append(s, s2);
		}
		List<Selectable> list = page.getHtml().xpath("/html/body/div/div[@class='article']/div[@class='a_section']").nodes();
		BasicDBObject dbo2 = new BasicDBObject();
		dbo.append("明细", dbo2);
		for (Selectable section : list) {
			String title = section.xpath("//p[@class='as_title']/text()").get();
//			String data = getData(title);
			title = StringUtils.removeBrackets(title);
			BasicDBObject dbo3 = new BasicDBObject();
			dbo2.append(title,dbo3);
			List<Selectable> asList = section.xpath("//div[@class='as_list']").nodes();
			for (Selectable section2 : asList) {
				String title2 = section2.xpath("//p[@class='c666666 bold']/text()").get();
//				String data2 = getData(title2);
				title2 = StringUtils.removeBrackets(title2);
				BasicDBObject dbo4 = new BasicDBObject();
				dbo3.append(title2, dbo4);
				List<Selectable> lis = section2.xpath("//ul/li/text()").nodes();
				for (Selectable li : lis) {
					String detail = StringUtils.removeBrackets(li.get());
					String[] details = detail.split("：");
					dbo4.append(details[0], details[1]);
				}
			}
		}

		System.err.println(dbo.toString());
		page.putField("dbo", dbo);
	}
	private Site site = Site.me().setRetryTimes(5).setSleepTime(200);
	@Override
	public Site getSite() {
		site.addHeader("Host", "stockdata.stock.hexun.com");
		return site;
	}
	private static String[] config = { "classpath:spring-core.xml" };
	public static ApplicationContext APP_CONTEXT;
	private static String REPORT_URL = "http://stockdata.stock.hexun.com/zrbg/stock_bg.aspx?code=[code]&date=[date]";
	private static String[] PUBLISH_DATE = {
		"2014-12-31"
	};
	static {
		APP_CONTEXT = new FileSystemXmlApplicationContext(config);
	}

	public static void main(String[] args) throws JMException {
		long start = System.currentTimeMillis();
		
		SocialReportSynService socialReportSynService = APP_CONTEXT.getBean(SocialReportSynService.class);
		List<SocialReportSyn> companies = socialReportSynService.findAll();
		
		SocialReportDetailPipeline socialReportDetailPipeline = new SocialReportDetailPipeline(APP_CONTEXT.getBean(MongoTemplate.class));
		Spider spider = Spider
				.create(new CompanySocialReportDetailSprderPageProcessor())
				.addPipeline(socialReportDetailPipeline)
				.setDownloader(new HtmlUnitDownloader());
		
		for (SocialReportSyn socialReportSyn : companies) {
			Request req = new Request(REPORT_URL.replace("[code]", socialReportSyn.getStockCode()).replace("[date]", PUBLISH_DATE[0]));
			req.putExtra("stockCode", socialReportSyn.getStockCode());
			req.putExtra("publishDate", PUBLISH_DATE[0]);
			spider.addRequest(req);
//			break;
		}
		SpiderMonitor.instance().register(spider);//爬虫监控
		spider.thread(6).run();
		System.out.println("耗时:" + (System.currentTimeMillis() - start) / 1000
				+ "秒");
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
	private String getData(String s){
		Pattern p = Pattern.compile("\\-?\\d+(\\.\\d+)?");
		Matcher m = p.matcher(s);
		if(m.find()){
			return m.group();
		}
		return null;
	}
}
