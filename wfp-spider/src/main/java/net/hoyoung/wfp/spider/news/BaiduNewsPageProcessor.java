package net.hoyoung.wfp.spider.news;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.core.utils.ProxyReader;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;
import net.hoyoung.wfp.spider.util.URLNormalizer;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.ProxyHttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;

public class BaiduNewsPageProcessor implements PageProcessor {

	static final String URL_PATTERN = "http://news.baidu.com/ns?word=%2B{keyword}&tn=news&from=news&cl=2&rn=20&ct=0";
	
	@Override
	public void process(Page page) {
		String s = page.getHtml().$("#header_top_bar > span","text").get();
		Matcher matcher = Pattern.compile("找到相关新闻约(\\d+)篇").matcher(s);
		if(matcher.find()){
			Integer newsNum = Integer.valueOf(matcher.group(1));
			page.putField("document", new Document(ComPage.STOCK_CODE, page.getRequest().getExtra(ComPage.STOCK_CODE)).append("news_num", newsNum));
		}
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

	static List<String> getStockNames() {
		List<String> list = Lists.newArrayList();
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

	static Map<String, String> getStockNameMap() {
		Map<String, String> map = Maps.newHashMap();
		MongoCollection<Document> collection = MongoUtil.getClient().getDatabase("wfp").getCollection("company_info");
		MongoCursor<Document> iterator = collection.find(Filters.eq("is_target", 1))
				.projection(Projections.include("name", "stockCode")).iterator();

		try {
			while (iterator.hasNext()) {
				Document document = iterator.next();
				map.put(document.getString(ComPage.STOCK_CODE), document.getString("name"));
			}
		} finally {
			iterator.close();
		}
		return map;
	}

	public static void main(String[] args) {

		Map<String, String> nameMap = getStockNameMap();
		List<String> stocks = getStockNames();
		List<Request> requests = Lists.newArrayList();
		for (String stock : stocks) {
			String fullName = nameMap.get(stock);
			
			String url;
			try {
				url = URL_PATTERN.replace("{keyword}", URLEncoder.encode(fullName, "utf-8"));
				requests.add(new Request(url).putExtra(ComPage.STOCK_CODE, stock));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			break;
		}
		BaiduNewsPageProcessor processor = new BaiduNewsPageProcessor();
		// processor.getSite().setHttpProxyPool(ProxyReader.read(), false);
		Spider.create(processor).addPipeline(new FootprintPipeline()).addRequest(requests.toArray(new Request[] {}))
				.setDownloader(new ProxyHttpClientDownloader()).thread(1).run();
	}
}
