package net.hoyoung.wfp.stockdown;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
/**
 * 员工社会责任报告爬虫
 * @author hoyoung
 *
 */
public class CompanySocialReportSprderPageProcessor implements PageProcessor {
	private Site site = Site.me().setRetryTimes(5).setSleepTime(200);

	@Override
	public void process(Page page) {

	}

	@Override
	public Site getSite() {
		site.addHeader("Host", "stockdata.stock.hexun.com");
		site.addHeader(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");
		return site;
	}

	public static void main(String[] args) {

	}
}
