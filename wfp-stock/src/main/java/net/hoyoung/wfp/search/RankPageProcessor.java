package net.hoyoung.wfp.search;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.bson.Document;

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

public class RankPageProcessor implements PageProcessor {
	private static final String URL_PATTERN = "http://pr.seowhy.com/%s";

	// http://seo.chinaz.com/?q=dongeejiao.com
	private Pattern rankPattern = Pattern.compile(".+/ranks/(\\d+)\\.gif$");

	private AtomicInteger count = new AtomicInteger(0);
	private static final String COLUNM_NAME = "page_rank";

	@Override
	public void process(Page page) {

		String s = page.getHtml().$("#data-prNow > em", "text").get();
		if (StringUtils.isEmpty(s)) {
			return;
		}
		int rs = 0;
		if (Pattern.matches("\\d", s)) {
			rs = Integer.valueOf(s);
		}
		String stockCode = (String) page.getRequest().getExtra("stockCode");
		page.putField("document", new Document("stockCode", stockCode).append(COLUNM_NAME, rs));
		System.out.println(count.incrementAndGet() + "-" + stockCode + "-" + rs);

	}

	private static final int SLEEP_TIME = 400;
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
		MongoCursor<Document> iterator = collection.find().projection(Projections.include("stockCode", "webSite"))
				.iterator();
		List<Request> requests = Lists.newArrayList();
		try {
			while (iterator.hasNext()) {
				Document doc = iterator.next();
				String stockCode = doc.getString("stockCode");
				// if (footPrint.count(Filters.and(Filters.eq("stockCode",
				// stockCode), Filters.exists(COLUNM_NAME))) > 0) {
				// continue;
				// }
				String webSite = doc.getString("webSite");
				requests.add(
						new Request(String.format(URL_PATTERN, UrlUtils.getDomain(webSite).replaceAll("^www\\.", "")))
								.putExtra("stockCode", stockCode));
				break;
			}
		} finally {
			iterator.close();
		}
		RankPageProcessor processor = new RankPageProcessor();
		// processor.getSite().setHttpProxyPool(ProxyReader.read(), false);
		Spider.create(processor).setDownloader(new ProxyHttpClientDownloader()).addPipeline(new FootprintPipeline())
				.addRequest(requests.toArray(new Request[] {})).thread(5).run();
	}

}
