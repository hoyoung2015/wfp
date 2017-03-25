package net.hoyoung.wfp.stock;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
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

public class StockPageProcessor implements PageProcessor {

	// http://stockdata.stock.hexun.com/gszl/jbgk.aspx

	private static String URL_PATTERN = "http://stockdata.stock.hexun.com/2009_sdgd_%s.shtml";
	private static final String COLUNM_NAME = "ten_stock";

	public void process(Page page) {
		List<Selectable> tds = page.getHtml()
				.$("#zaiyaocontent > table:nth-child(3) > tbody > tr:nth-child(n+3) > td:nth-child(3) > span", "text")
				.nodes();
		if (CollectionUtils.isEmpty(tds)) {
			return;
		}
		Document document = new Document("stockCode", page.getRequest().getExtra("stockCode"));
		List<Float> stocks = Lists.newArrayList();
		for (int i = 0; i < 10 && i < tds.size(); i++) {
			String stockStr = tds.get(i).get();
			if (Pattern.matches("\\d+(\\.)?\\d+%$", stockStr)) {
				stocks.add(Float.valueOf(stockStr.replace("%", "")));
			}
		}
		if (CollectionUtils.isEmpty(stocks)) {
			return;
		}
		document.append(COLUNM_NAME, StringUtils.join(stocks, ","));
		page.putField("document", document);
	}

	private static final int SLEEP_TIME = 300;
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
		MongoCollection<Document> collection = MongoUtil.getClient().getDatabase("wfp").getCollection("web_source");
		MongoCollection<Document> footPrint = MongoUtil.getClient().getDatabase("wfp").getCollection("footprint");
		MongoCursor<Document> iterator = collection.find().projection(Projections.include("stockCode")).iterator();

		List<Request> requests = Lists.newArrayList();

		try {
			while (iterator.hasNext()) {
				Document doc = iterator.next();
				String stockCode = doc.getString("stockCode");
				if (footPrint.count(Filters.and(Filters.eq("stockCode", stockCode), Filters.exists(COLUNM_NAME))) > 0) {
					continue;
				}

				String targetUrl = String.format(URL_PATTERN, stockCode);
				requests.add(new Request(targetUrl).putExtra("stockCode", stockCode));
//				 break;
			}
		} finally {
			iterator.close();
		}
		StockPageProcessor processor = new StockPageProcessor();
//		processor.getSite().setHttpProxyPool(ProxyReader.read(), false);
		Spider.create(processor).setDownloader(new ProxyHttpClientDownloader()).addPipeline(new FootprintPipeline())
				.addRequest(requests.toArray(new Request[] {})).thread(5).run();
	}

}