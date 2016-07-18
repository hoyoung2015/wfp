package net.hoyoung.wfp.weibo;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;

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
//@Scope("prototype")
public class WeiboCrawler extends BreadthCrawler {

	String cookie;

	public WeiboCrawler(String crawlPath, boolean autoParse) throws Exception {
		super(crawlPath, autoParse);
		/* 获取新浪微博的cookie，账号密码以明文形式传输，请使用小号 */
//		cookie = WeiboCN.getSinaCookie("shoman@sina.cn", "19920609qwer@");
	}

	@Override
	public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception {
		HttpRequest request = new HttpRequest(crawlDatum);
		request.setCookie(cookie);
		return request.getResponse();
	}

	@Override
	public void visit(Page page, CrawlDatums next) {
		Integer o = (Integer) JSONPath.compile("$.ok").eval(JSON.parseObject(page.getHtml()));
		System.out.println(o);
	}

	public static void main(String[] args) throws Exception {
		WeiboCrawler crawler = new WeiboCrawler("weibo_crawler", false);
		crawler.setThreads(1);
		/* 对某人微博前5页进行爬取 */
		crawler.addSeed(new CrawlDatum(
				"http://m.weibo.cn/page/json?containerid=1005051746221281_-_WEIBO_SECOND_PROFILE_WEIBO&page=2"));
		crawler.start(1);
		System.out.println("over");
	}

}