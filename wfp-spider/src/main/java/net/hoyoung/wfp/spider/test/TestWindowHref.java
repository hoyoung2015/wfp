package net.hoyoung.wfp.spider.test;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

import net.hoyoung.wfp.spider.util.UserAgentUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class TestWindowHref {

	static class MyPageProcessor implements PageProcessor {

		private List<String> fuckUrlProcess(Page page) {
			List<String> list = Lists.newArrayList();
			Pattern pattern = Pattern
					.compile("<a[^>]*window\\.location\\.href=(\"([^\"]*)\"|\'([^\']*)\'|([^\\s>]*))[^>]*>(.*?)</a>");
			Matcher matcher = pattern.matcher(page.getRawText());
			while (matcher.find()) {
				String url = page.getRequest().getUrl();
				int i = url.lastIndexOf("/");
				list.add(url.substring(0, i) + "/" + matcher.group(1).replaceAll("'|\"", ""));
			}
			return list;
		}

		@Override
		public void process(Page page) {
			List<String> list = fuckUrlProcess(page);
			for (String s : list) {
				System.out.println(s);
			}
			List<String> links = page.getHtml().links().all();
			for (String s : links) {
				System.err.println(s);
			}
		}

		private Site site = Site.me().setSleepTime(1000).setRetryTimes(3).setTimeOut(50000).setCycleRetryTimes(2)
				.addHeader("User-Agent", UserAgentUtil.getRandomAgent())
				.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
				.addHeader("Accept-Language", "zh-CN,zh;q=0.8")
				.addHeader("Accept-Encoding", "gzip, deflate, sdch, br")
				.addHeader("Cache-Control", "max-age=0").addHeader("Upgrade-Insecure-Requests", "1");

		@Override
		public Site getSite() {
			return site;
		}

	}

	public static void main(String[] args) {
		Spider.create(new MyPageProcessor()).addUrl("http://www.vtron.com/").thread(1).run();
	}

}
