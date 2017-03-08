package net.hoyoung.downloader;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Hello world!
 *
 */
public class App {
	static class MyProcessor implements PageProcessor {
		private Site site = Site.me().setRetryTimes(3).addHeader("User-Agent",
				"Baiduspider+(+http://www.baidu.com/search/spider.htm)")
				.setSleepTime(0);

		@Override
		public Site getSite() {
			return site;
		}
		
		public void process(Page page) {
			
		}
	}

	public static void main(String[] args) {
		int n = 5;

		long len = 2723336;
//		long len = 245537;

		String url = "http://www.chinadmegc.com/file_download.php?1102";
//		String url = "http://www.chinadmegc.com/material_download.php?12";
		long d = len / n;
		Spider spider = Spider.create(new MyProcessor()).setScheduler(new NotDuplicateSchedule()).setDownloader(new HttpStreamDownloader()).thread(n);
		for (int i = 0; i < n; i++) {
			Request request = new Request(url);
			request.putExtra("i", i);
			if (i == 0) {
				request.putExtra("start", i * d);
			} else {
				request.putExtra("start", i * d + 1);
			}

			if (i == n - 1) {
				request.putExtra("end", len - 1);
			} else {
				request.putExtra("end", i * d + d);
			}
			spider.addRequest(request);
			System.err.println(request);
		}

		spider.run();

	}
}
