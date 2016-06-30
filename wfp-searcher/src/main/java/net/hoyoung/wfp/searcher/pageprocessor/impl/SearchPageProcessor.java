package net.hoyoung.wfp.searcher.pageprocessor.impl;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class SearchPageProcessor implements PageProcessor {
	private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
	@Override
	public void process(Page page) {
		page.putField("body", page.getHtml().xpath("//body").get());
	}
	@Override
	public Site getSite() {
		return site;
	}
}
