package net.hoyoung.wfp.search;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import net.hoyoung.wfp.FileStockCodeReader;
import net.hoyoung.wfp.MongoStockCodeReader;
import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.core.utils.ProxyReader;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.ProxyHttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;

public class BaiduNewsPageProcessor implements PageProcessor {

	static final String URL_PATTERN = "http://news.baidu.com/ns?word={keyword}&tn=news&from=news&cl=2&rn=20&ct=0";
	AtomicInteger counter = new AtomicInteger(0);

	@Override
	public void process(Page page) {

		String s = page.getHtml().$("#header_top_bar > span", "text").get();
		Matcher matcher = Pattern.compile("找到相关新闻约?([\\d,]+)篇").matcher(s);
		Integer newsNum = 0;
		if (matcher.find()) {
			newsNum = Integer.valueOf(matcher.group(1).replace(",", ""));
		}
		page.putField("document",
				new Document(STOCK_CODE, page.getRequest().getExtra(STOCK_CODE)).append("news_num", newsNum));
		System.out.println(counter.incrementAndGet() + "\t" + page.getRequest().getExtra(STOCK_CODE) + "\t" + newsNum);
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
/*
	static List<String> getStockNames() {
		List<String> list = new ArrayList<>();
		MongoDatabase db = MongoUtil.getClient().getDatabase("wfp_com_page");
		MongoCursor<String> iterator = db.listCollectionNames().iterator();
		try {
			while (iterator.hasNext()) {
				String s = iterator.next();
				if (Pattern.matches("^\\d+{6}$", s)) {
					list.add(s);
				}
			}
		} finally {
			iterator.close();
		}
		return list;
	}
	*/
	static List<String> getStockNames() {
		List<String> stockCodes = Lists.newArrayList();
		try {
			stockCodes = new FileStockCodeReader(
					"/Users/baidu/workspace/wfp/wfp-python/analyse/stock_gri.csv").getStockCodes();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stockCodes;
	}

	static final String STOCK_CODE = "stockCode";

	static Map<String, String> getStockNameMap() {
		Map<String, String> map = new HashMap<>();
		MongoCollection<Document> collection = MongoUtil.getClient().getDatabase("wfp").getCollection("company_info");
		MongoCursor<Document> iterator = collection.find(Filters.eq("is_target", 1))
				.projection(Projections.include("name", "stockCode")).iterator();

		try {
			while (iterator.hasNext()) {
				Document document = iterator.next();
				map.put(document.getString(STOCK_CODE), document.getString("name"));
			}
		} finally {
			iterator.close();
		}
		return map;
	}

	static Set<String> getCrawedStockSet() {
		Set<String> set = new HashSet<>();
		MongoCollection<Document> collection = MongoUtil.getClient().getDatabase("wfp").getCollection("footprint");
		MongoCursor<Document> iterator = collection
				.find(Filters.and(Filters.exists("news_num"), Filters.gt("news_num", 0)))
				.projection(Projections.include("stockCode")).iterator();

		try {
			while (iterator.hasNext()) {
				Document document = iterator.next();
				set.add(document.getString(STOCK_CODE));
			}
		} finally {
			iterator.close();
		}
		return set;
	}

	public static void main(String[] args) throws IOException {

		Map<String, String> nameMap = getStockNameMap();
		List<String> stocks = new FileStockCodeReader("/Users/baidu/workspace/wfp/wfp-python/analyse/stock_gri.csv").getStockCodes();
		List<Request> requests = new ArrayList<>();
		Set<String> crawedStockSet = getCrawedStockSet();
		for (String stock : stocks) {
			if (crawedStockSet.contains(stock)) {
				continue;
			}
			String fullName = nameMap.get(stock);
			String url;
			try {
				url = URL_PATTERN.replace("{keyword}", URLEncoder.encode("\""+fullName+"\"", "utf-8"));
				requests.add(new Request(url).putExtra(STOCK_CODE, stock));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
//			 break;
		}
		BaiduNewsPageProcessor processor = new BaiduNewsPageProcessor();
//		processor.getSite().setHttpProxyPool(ProxyReader.read(), false);
		Spider.create(processor).addPipeline(new FootprintPipeline()).addRequest(requests.toArray(new Request[] {}))
				.setDownloader(new ProxyHttpClientDownloader()).thread(2).run();
	}
}
