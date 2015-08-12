package net.hoyoung.webmagic.pageprocessor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;
/**
 * 股票代码处理
 * @author Administrator
 *
 */
public class StockSymbolPageProcessor implements PageProcessor {
	private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
	@Override
	public void process(Page page) {
		
	}
	@Override
	public Site getSite() {
		return site;
	}
}
