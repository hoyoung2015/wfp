package net.hoyoung.wfp.spider.test;

import java.util.List;

import com.google.common.collect.Lists;

import net.hoyoung.wfp.spider.comweb.ComWebProcessor;
import net.hoyoung.wfp.spider.comweb.PhantomJsPageProcessor;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.PhantomJSDownloader;
import us.codecraft.webmagic.processor.PageProcessor;

public class TestPhantomJs {

	
	public static void main(String[] args) {
		PageProcessor processor = new ComWebProcessor();
		List<String[]> proxies = Lists.newArrayList();
		PhantomJSDownloader downloader = new PhantomJSDownloader("/Users/baidu/local/phantomjs/bin/phantomjs");
//		processor.getSite().setHttpProxyPool(proxies, false);
		Request request = new Request("http://www.hoyoung.net/");
		request.putExtra(ComPage.STOCK_CODE, "000000");
		Spider.create(processor).setDownloader(downloader).addRequest(request).thread(5).run();
	}
}
