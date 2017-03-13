package net.hoyoung.wfp.spider.test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;

import net.hoyoung.wfp.core.utils.WFPContext;
import net.hoyoung.wfp.spider.comweb.ComWebProcessor;
import net.hoyoung.wfp.spider.comweb.PhantomJsPageProcessor;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HoyPhantomJSDownloader;
import us.codecraft.webmagic.downloader.PhantomJSDownloader;
import us.codecraft.webmagic.processor.PageProcessor;

public class TestPhantomJs {

	
	static class PhantomJsPageProcessor implements PageProcessor{

		@Override
		public void process(Page page) {
			try {
				FileUtils.writeStringToFile(new File("log/a.html"), page.getRawText());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private Site site = Site.me().setSleepTime(WFPContext.getProperty("compage.spider.commonSleepTime", Integer.class))
				.setRetryTimes(3).setTimeOut(WFPContext.getProperty("compage.spider.sleepTime", Integer.class)).setCycleRetryTimes(WFPContext.getProperty("compage.spider.retryTime", Integer.class))
				.addHeader("Accept-Language", "zh-CN,zh;q=0.8")//有的server需要这个
				.addHeader("User-Agent", "Sogou web spider/3.0(+http://www.sogou.com/docs/help/webmasters.htm#07)");
		
		@Override
		public Site getSite() {
			return site;
		}
		
	}
	
	public static void main(String[] args) {
		PageProcessor processor = new PhantomJsPageProcessor();
		List<String[]> proxies = Lists.newArrayList();
		HoyPhantomJSDownloader downloader = new HoyPhantomJSDownloader("/Users/baidu/local/phantomjs/bin/phantomjs");
//		processor.getSite().setHttpProxyPool(proxies, false);
		Request request = new Request("http://www.valin.cn/");
		request.putExtra(ComPage.STOCK_CODE, "000000");
		Spider.create(processor).setDownloader(downloader).addRequest(request).thread(5).run();
	}
}
