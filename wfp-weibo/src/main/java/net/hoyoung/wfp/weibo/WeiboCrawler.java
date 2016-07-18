package net.hoyoung.wfp.weibo;

import org.jsoup.select.Elements;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

/**
 * 利用WebCollector和获取的cookie爬取新浪微博并抽取数据
 * 
 * @author hu
 */
public class WeiboCrawler extends BreadthCrawler {

	String cookie;

	public WeiboCrawler(String crawlPath, boolean autoParse) throws Exception {
		super(crawlPath, autoParse);
		/* 获取新浪微博的cookie，账号密码以明文形式传输，请使用小号 */
		cookie = WeiboCN.getSinaCookie("shoman@sina.cn", "19920609qwer@");
	}

	@Override
	public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception {
		HttpRequest request = new HttpRequest(crawlDatum);
		request.setCookie(cookie);
		return request.getResponse();
	}

	@Override
	public void visit(Page page, CrawlDatums next) {
			Elements title = page.doc().getElementsByTag("title");
			System.out.println(title);
	}

	public static void main(String[] args) throws Exception {
		WeiboCrawler crawler = new WeiboCrawler("weibo_crawler", false);
		crawler.setThreads(1);
		/* 对某人微博前5页进行爬取 */
		crawler.addSeed(new CrawlDatum(
				"http://m.weibo.cn/page/tpl?containerid=1005051746221281_-_WEIBO_SECOND_PROFILE_WEIBO&itemid=&title=%E5%85%A8%E9%83%A8%E5%BE%AE%E5%8D%9A"));
		crawler.start(1);
		System.out.println("over");
	}

}