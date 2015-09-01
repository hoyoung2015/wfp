package net.hoyoung.wfp.stockdown;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.management.JMException;

import net.hoyoung.webmagic.downloader.HtmlUnitDownloader;
import net.hoyoung.webmagic.pipeline.SocialReportPipeline;
import net.hoyoung.wfp.core.entity.SocialReportSyn;
import net.hoyoung.wfp.core.service.SocialReportService;
import net.hoyoung.wfp.core.service.SocialReportSynService;

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
 * 员工社会责任报告爬虫
 * 
 * @author hoyoung
 *
 */

public class CompanySocialReportSprderPageProcessor implements PageProcessor {
	private Site site = Site.me().setRetryTimes(5).setSleepTime(200);
	
	JsonPathSelector industry_jps = new JsonPathSelector("$.industry");//股票号
	JsonPathSelector industryrate_jps = new JsonPathSelector("$.industryrate");//总得分
	JsonPathSelector Pricelimit_jps = new JsonPathSelector("$.Pricelimit");//等级
	JsonPathSelector stockNumber_jps = new JsonPathSelector("$.stockNumber");//股东责任
	JsonPathSelector lootingchips_jps = new JsonPathSelector("$.lootingchips");//员工责任
	JsonPathSelector rscramble_jps = new JsonPathSelector("$.rscramble");//环境
	JsonPathSelector Scramble_jps = new JsonPathSelector("$.Scramble");	//权益责任
	JsonPathSelector Strongstock_jps = new JsonPathSelector("$.Strongstock");	//社会责任
	JsonPathSelector StockNameLink_jps = new JsonPathSelector("$.StockNameLink");	//
	@Override
	public void process(Page page) {
		if (page.getUrl().regex( ".*zrbg/data/zrbList.aspx.*").match()) { //total 列表
			String jsonStr = page.getRawText();
			jsonStr = jsonStr.substring(13, jsonStr.length() - 1);
			
			String publishDate = (String) page.getRequest().getExtra("publishDate");
			
			List<String> comList = new JsonPathSelector("$.list")
					.selectList(jsonStr);
			
			List<SocialReportSyn> srsynList = new ArrayList<SocialReportSyn>();
			for (String s : comList) {
				SocialReportSyn srsyn = new SocialReportSyn();
				srsyn.setPublishDate(publishDate);//发布日期
				srsyn.setCreateDate(new Date());//创建日期
				String str = industry_jps.select(s);
				
				srsyn.setStockCode(str.substring(str.indexOf("(")+1, str.length()-1));//股票号
				
				//总得分
				srsyn.setTotalScore(getFloat(industryrate_jps.select(s)));
				//等级
				srsyn.setLevel(Pricelimit_jps.select(s));
				//股东责任
				srsyn.setGdScore(getFloat(stockNumber_jps.select(s)));
				//员工责任
				srsyn.setEmpScore(getFloat(lootingchips_jps.select(s)));
				//权益责任
				srsyn.setEquityScore(getFloat(Scramble_jps.select(s)));
				//环境责任
				srsyn.setHjScore(getFloat(rscramble_jps.select(s)));
				//社会责任
				srsyn.setSocialScore(getFloat(Strongstock_jps.select(s)));
				//pdf下载路径
				srsyn.setReportFileUrl(new XpathSelector("//a/@href").select(s.replaceAll("\\\\", "")));
				srsynList.add(srsyn);
				
				Request req = new Request(ZRBG_URL+StockNameLink_jps.select(s));
				req.putExtra("stockCode", srsyn.getStockCode());
				req.putExtra("publishDate", srsyn.getPublishDate());
//				page.addTargetRequest(req);
			}
			page.putField("srsynList", srsynList);
		}else{
			Selectable div = page.getHtml().xpath("//div[@class='w680']");
			List<Selectable> aSection = div.xpath("/div/div[@class='a_section']").nodes();
			for (Selectable section : aSection) {
				String title = section.xpath("/div/p[@class='as_title']/text()").get();
				
				
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

	private static String[] config = { "classpath:spring-core.xml" };
	public static ApplicationContext APP_CONTEXT;
	private static String REPORT_URL = "http://stockdata.stock.hexun.com/zrbg/data/zrbList.aspx?date=[date]&count=20&page=[page]";
	private static String ZRBG_URL = "http://stockdata.stock.hexun.com/zrbg/";
	private static int PAGE_START = 1;
	private static int PAGE_END = 141;// 141
	private static String[] PUBLISH_DATE = {
		"2014-12-31"
	};
	static {
		APP_CONTEXT = new FileSystemXmlApplicationContext(config);
	}

	public static void main(String[] args) throws JMException {
		//http://stockdata.stock.hexun.com/zrbg/#
		long start = System.currentTimeMillis();
		SocialReportPipeline socialReportPipeline = new SocialReportPipeline(
				APP_CONTEXT.getBean(SocialReportService.class),
				APP_CONTEXT.getBean(SocialReportSynService.class));
		;
		Spider spider = Spider
				.create(new CompanySocialReportSprderPageProcessor())
				.addPipeline(socialReportPipeline);
		for (String pubDate : PUBLISH_DATE) {
			for (int i = PAGE_START; i <= PAGE_END; i++) {
				Request req = new Request(REPORT_URL.replace("[date]", pubDate).replace("[page]", ""+i));
				req.putExtra("publishDate", pubDate);
				spider.addRequest(req);
			}
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
}
