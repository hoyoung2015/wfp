package net.hoyoung.wfp.patent.spider;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;

import com.google.common.collect.Lists;

import net.hoyoung.wfp.patent.PatentPage;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

public class PatientPageProcessor implements PageProcessor {
	
	private AtomicInteger counter = new AtomicInteger(0);

	private String getPatentDate(Selectable div) {
		String s = div.$("div.left-record > div.record-subtitle", "text").get();
		Matcher matcher = Pattern.compile("(\\d{4}年\\d{1,2}月\\d{1,2})").matcher(s);
		if (matcher.find()) {
			s = matcher.group(1);
			return s.replace("年", "-").replace("月", "-");
		}
		return null;
	}

	private static final Set<String> PATENT_TYPE = new HashSet<String>() {
		private static final long serialVersionUID = 945955866676266694L;
		{
			add("实用新型");
			add("发明专利");
			add("外观设计");
		}
	};

	private String getPatentType(Selectable div) {
		String s = div.$("div.left-record > div.record-subtitle", "text").get();
		Matcher matcher = Pattern.compile("\\[(.*)\\]").matcher(s);
		if (matcher.find() && PATENT_TYPE.contains(matcher.group(1))) {
			return matcher.group(1);
		} else {
			return null;
		}
	}

	@Override
	public void process(Page page) {
		List<Selectable> items = page.getHtml()
				.$("body > div.content.content-search.clear > div.right > div.record-item-list > div.record-item")
				.nodes();
		if (CollectionUtils.isEmpty(items)) {
			return;
		}
		List<Document> documents = Lists.newArrayList();
		String stockCode = (String) page.getRequest().getExtra(PatentPage.STOCK_CODE);
		for (Selectable div : items) {
			String detailUrl = div.$("div.left-record > div.record-title > a.title", "href").get();
			String patentName = div.$("div.left-record > div.record-title > a.title", "text").get();
			String patentType = getPatentType(div);
			String date = getPatentDate(div);
			System.out.println(counter.incrementAndGet()+"\t"+detailUrl + "\t" + patentName + "\t" + patentType + "\t" + date);
			documents.add(new Document(PatentPage.STOCK_CODE, stockCode).append(PatentPage.DETAIL_URL, detailUrl)
					.append(PatentPage.DATE, date).append(PatentPage.PATENT_NAME, patentName)
					.append(PatentPage.PATENT_TYPE, patentType));
		}
		page.putField("documents", documents);
		nextPage(page);
	}

	private void nextPage(Page page) {
		List<String> pageLinks = page.getHtml()
				.$("body > div.content.content-search.clear > div.right > div.record-item-list > p.pager").links()
				.all();

		if (CollectionUtils.isEmpty(pageLinks)) {
			return;
		}
		for (String url : pageLinks) {
			Request request = new Request(url);
			request.putExtra(PatentPage.STOCK_CODE, page.getRequest().getExtra(PatentPage.STOCK_CODE));
			page.addTargetRequest(request);
		}
	}

	private static final int SLEEP_TIME = 1000;

	private Site site = Site.me().setSleepTime(SLEEP_TIME).setRetryTimes(3).setTimeOut(30000).setCycleRetryTimes(2)
			.addHeader("User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36")
			.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
			.addHeader("Accept-Language", "zh-CN,zh;q=0.8").addHeader("Accept-Encoding", "gzip, deflate, sdch, br")
			.addHeader("Cache-Control", "max-age=0").addHeader("Upgrade-Insecure-Requests", "1");

	@Override
	public Site getSite() {
		return site;
	}

	public static void main(String[] args) {
		PatientPageProcessor pageProcessor = new PatientPageProcessor();
		// 设置代理
		List<String[]> proxies = Lists.newArrayList();
		proxies.add(new String[] { "hoyoung", "QWerASdf", "139.129.93.2", "8128" });// 杨鹏的阿里云
		proxies.add(new String[] { "hoyoung", "QWerASdf", "123.206.58.101", "8128" });// 我的腾讯云
		proxies.add(new String[] { "hoyoung", "QWerASdf", "182.61.20.189", "8128" });// 我的百度云，首月9.9
		proxies.add(new String[] { "hoyoung", "QWerASdf", "118.89.238.129", "8128" });// 余启林的腾讯学生机
		pageProcessor.getSite().setHttpProxyPool(proxies, false);

		Request request = new Request(
				"http://s.wanfangdata.com.cn/patent.aspx?q=%e4%b8%93%e5%88%a9%e6%9d%83%e4%ba%ba%3a%e6%97%a5%e5%87%ba%e4%b8%9c%e6%96%b9%e5%a4%aa%e9%98%b3%e8%83%bd%e8%82%a1%e4%bb%bd%e6%9c%89%e9%99%90%e5%85%ac%e5%8f%b8&f=top&p=1");
		request.putExtra(PatentPage.STOCK_CODE, "111111");
		Spider.create(pageProcessor).addRequest(request).thread(1).run();
	}
}