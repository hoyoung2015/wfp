package net.hoyoung.wfp.stock;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.bson.Document;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import net.hoyoung.wfp.FileStockCodeReader;
import net.hoyoung.wfp.StockCodeReader;
import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.core.utils.ProxyReader;
import net.hoyoung.wfp.search.FootprintPipeline;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.ProxyHttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;

public class FuzhaibiPageProcessor implements PageProcessor {
	private static final String URL_PATTERN = "http://stockpage.10jqka.com.cn/%s/finance/";

	private AtomicInteger count = new AtomicInteger(0);

	private static final String COLUNM_NAME = "fuzhaibi";

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

	@Override
	public void process(Page page) {
		System.out.println(">>\t" + count.incrementAndGet());
		String src = page.getHtml().$("#main", "text").get();
		if (StringUtils.isEmpty(src)) {
			return;
		}
		FinanceVo vo = JSON.parseObject(src, FinanceVo.class);
		Document document = new Document("stockCode", page.getRequest().getExtra("stockCode")).append(COLUNM_NAME,
				Float.valueOf(vo.report[10][0]) / 100);
		page.putField("document", document);
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
		
		MongoCollection<Document> footPrint = MongoUtil.getClient().getDatabase("wfp").getCollection("footprint");
		StockCodeReader stockCodeReader = new FileStockCodeReader("/Users/baidu/workspace/wfp/wfp-python/analyse/stock_gri.csv");
		List<Request> requests = Lists.newArrayList();
		List<String> stockCodes = null;
		try {
			stockCodes = stockCodeReader.getStockCodes();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		for (String stockCode : stockCodes) {
			if (footPrint.count(Filters.and(Filters.eq("stockCode", stockCode), Filters.exists(COLUNM_NAME))) > 0) {
				continue;
			}
			requests.add(new Request(String.format(URL_PATTERN, stockCode)).putExtra("stockCode", stockCode));
//			break;
		}
		FuzhaibiPageProcessor processor = new FuzhaibiPageProcessor();
//		processor.getSite().setHttpProxyPool(ProxyReader.read(), false);
		Spider.create(processor).setDownloader(new ProxyHttpClientDownloader()).addPipeline(new FootprintPipeline())
				.addRequest(requests.toArray(new Request[] {})).thread(3).run();
	}

}
