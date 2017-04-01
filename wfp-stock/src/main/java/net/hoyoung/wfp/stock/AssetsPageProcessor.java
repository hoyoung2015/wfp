package net.hoyoung.wfp.stock;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.Document;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.core.utils.ProxyReader;
import net.hoyoung.wfp.search.FootprintPipeline;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.ProxyHttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

public class AssetsPageProcessor implements PageProcessor {
	private static final String URL_PATTERN = "http://stockpage.10jqka.com.cn/%s/finance/";

	private AtomicInteger count = new AtomicInteger(0);

	private static final String COLUNM_NAME = "assets";

	static class FinanceVo {
		Object title;
		String[][] report;

		public Object getTitle() {
			return title;
		}

		public void setTitle(Object title) {
			this.title = title;
		}

		public String[][] getReport() {
			return report;
		}

		public void setReport(String[][] report) {
			this.report = report;
		}

		@Override
		public String toString() {
			return "FinanceVo [title=" + title + ", report=" + Arrays.toString(report) + "]";
		}
	}

	private ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

	@Override
	public void process(Page page) {
		List<Selectable> nodes = page.getHtml()
				.$("#assetdebt > div.bd.clearfix > div.flashbox.first > table > tbody > tr").nodes();
		String jinrong = page.getHtml()
				.$("#assetdebt > div.bd.clearfix > div.flashbox.first > table > thead > tr > th:nth-child(2)", "text")
				.get();
		int danwei = 1;
		if (jinrong.contains("万元")) {
			danwei = 10000;
		}
		String stockCode = (String) page.getRequest().getExtra("stockCode");
		float f = 0f;
		for (Selectable selectable : nodes) {
			String th = selectable.$("th", "text").get();
			if (StringUtils.isNotEmpty(th)) {
				th = th.replace(" ", "");
			}
			if ("资产总计".equals(th)) {
				Matcher matcher = Pattern.compile("[\\d\\.]+").matcher(selectable.$("td", "text").get());
				if (matcher.find()) {
					String s = selectable.$("td", "text").get().replaceAll("(\\d|\\.)", "");
					if ("万元".equals(s)) {
						danwei = 10000;
					} else if ("亿元".equals(s)) {
						danwei = 1;
					}
					f = Float.valueOf(matcher.group()) / danwei;
					page.putField("document",
							new Document("stockCode", page.getRequest().getExtra("stockCode")).append(COLUNM_NAME, f));
				} else {
					try {
						FileUtils.writeStringToFile(new File(stockCode), "没找到资产");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}
		System.out.println(count.incrementAndGet() + "-" + stockCode + "-" + f);
	}

	private static final int SLEEP_TIME = 500;
	private Site site = Site.me().setSleepTime(SLEEP_TIME).setRetryTimes(3).setTimeOut(50000).setCycleRetryTimes(3)
			.addHeader("User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36")
			.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
			.addHeader("Accept-Language", "zh-CN,zh;q=0.8").addHeader("Accept-Encoding", "gzip, deflate, sdch, br")
			.addHeader("Cache-Control", "max-age=0");

	@Override
	public Site getSite() {
		return site;
	}

	public static void main(String[] args) {
		MongoCollection<Document> collection = MongoUtil.getClient().getDatabase("wfp").getCollection("footprint");
		MongoCollection<Document> footPrint = MongoUtil.getClient().getDatabase("wfp").getCollection("footprint");
		MongoCursor<Document> iterator = collection.find(/*Filters.eq("stockCode", "300361")*/).projection(Projections.include("stockCode")).iterator();
		List<Request> requests = Lists.newArrayList();
		try {
			while (iterator.hasNext()) {
				Document doc = iterator.next();
				String stockCode = doc.getString("stockCode");
				// if (footPrint.count(Filters.and(Filters.eq("stockCode",
				// stockCode), Filters.exists(COLUNM_NAME))) > 0) {
				// continue;
				// }
				requests.add(new Request(String.format(URL_PATTERN, stockCode)).putExtra("stockCode", stockCode));
//				 break;
			}
		} finally {
			iterator.close();
		}
		AssetsPageProcessor processor = new AssetsPageProcessor();
		processor.getSite().setHttpProxyPool(ProxyReader.read(), false);
		Spider.create(processor).setDownloader(new ProxyHttpClientDownloader()).addPipeline(new FootprintPipeline())
				.addRequest(requests.toArray(new Request[] {})).thread(4).run();
	}

}
