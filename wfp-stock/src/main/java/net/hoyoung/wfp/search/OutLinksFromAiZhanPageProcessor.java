package net.hoyoung.wfp.search;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.bson.Document;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.core.utils.ProxyReader;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.ProxyHttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.UrlUtils;

public class OutLinksFromAiZhanPageProcessor implements PageProcessor {
	private static final String URL_PATTERN = "http://link.aizhan.com/index.php?r=ajax/get-out-link&id=%s&t=1491073002&cc=8e1fe1ee74e1373a1285869f941f1707";

	private AtomicInteger count = new AtomicInteger(0);

	private static final String COLUNM_NAME = "outlink_num_az";

	static class JsonData {
		private String state;
		private Integer count;

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public Integer getCount() {
			return count;
		}

		public void setCount(Integer count) {
			this.count = count;
		}

	}

	@Override
	public void process(Page page) {
		String rawText = page.getRawText();
		String stockCode = (String) page.getRequest().getExtra("stockCode");

		int outlinkNum = 0;
		try {
			JsonData jsonData = JSON.parseObject(rawText, JsonData.class);
			if (!"1".equals(jsonData.getState())) {
				throw new Exception("");
			}
			outlinkNum = jsonData.getCount();
			page.putField("document", new Document("stockCode", stockCode).append(COLUNM_NAME, outlinkNum)
					.append(COLUNM_NAME, outlinkNum));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(count.incrementAndGet() + "-" + outlinkNum);
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
		MongoCollection<Document> collection = MongoUtil.getClient().getDatabase("wfp").getCollection("web_source");
		MongoCollection<Document> footPrint = MongoUtil.getClient().getDatabase("wfp").getCollection("footprint");
		MongoCursor<Document> iterator = collection
				.find(/* Filters.eq("stockCode", "600378") */).projection(Projections.include("stockCode", "webSite"))
				.iterator();
		List<Request> requests = Lists.newArrayList();
		try {
			while (iterator.hasNext()) {
				Document doc = iterator.next();
				String stockCode = doc.getString("stockCode");

				if (footPrint.count(Filters.and(Filters.eq("stockCode", stockCode), Filters.exists(COLUNM_NAME))) > 0) {
					continue;
				}
				String webSite = doc.getString("webSite");
				String domain = UrlUtils.getDomain(webSite);
				Request request = new Request(String.format(URL_PATTERN, domain)).putExtra("stockCode", stockCode)
						.putExtra("domain", domain);
				requests.add(request);
//				break;
			}
		} finally {
			iterator.close();
		}
		OutLinksFromAiZhanPageProcessor processor = new OutLinksFromAiZhanPageProcessor();
//		 processor.getSite().setHttpProxyPool(ProxyReader.read(), false);
		Spider.create(processor).setDownloader(new ProxyHttpClientDownloader()).addPipeline(new FootprintPipeline())
				.addRequest(requests.toArray(new Request[] {})).thread(4).run();
	}

}
