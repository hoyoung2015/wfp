package net.hoyoung.wfp.search;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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
import us.codecraft.webmagic.utils.HttpConstant;
import us.codecraft.webmagic.utils.UrlUtils;

public class OutLinksPageProcessor implements PageProcessor {
	private static final String URL_PATTERN = "http://outlink.chinaz.com/dataprocess.ashx?action=getdomain&callback=";

	private AtomicInteger count = new AtomicInteger(0);

	private static final String COLUNM_NAME = "outlink_num";
	private static final String COLUNM_NAME_ALL = "outlink_num_all";

	static class JsonVo {
		private Integer state;
		private List<JsonData> data;

		public Integer getState() {
			return state;
		}

		public void setState(Integer state) {
			this.state = state;
		}

		public List<JsonData> getData() {
			return data;
		}

		public void setData(List<JsonData> data) {
			this.data = data;
		}

	}

	static class JsonData {
		private String host;
		private Integer linkcount;

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public Integer getLinkcount() {
			return linkcount;
		}

		public void setLinkcount(Integer linkcount) {
			this.linkcount = linkcount;
		}

	}

	@Override
	public void process(Page page) {
		String rawText = page.getRawText();
		System.out.println(rawText);
		String prefix = (String) page.getRequest().getExtra("prefix");
		String stockCode = (String) page.getRequest().getExtra("stockCode");
		String domain = (String) page.getRequest().getExtra("domain");

		int outlinkNum = 0;
		int outlinkNumAll = 0;
		if (rawText != null && rawText.startsWith(prefix + "({state:1")) {
			rawText = rawText.replace(prefix + "(", "");
			rawText = rawText.substring(0, rawText.length() - 1);

			JsonVo jsonVo = JSON.parseObject(rawText, JsonVo.class);
//			if(jsonVo.getState()==0){
//				System.out.println(stockCode);
//			}
			
			for (JsonData jsonData : jsonVo.getData()) {
				if (domain.equals(jsonData.getHost())) {
					outlinkNum = jsonData.getLinkcount();
				}
				outlinkNumAll += jsonData.getLinkcount();
			}
		}
		page.putField("document", new Document("stockCode", stockCode).append(COLUNM_NAME, outlinkNum)
				.append(COLUNM_NAME_ALL, outlinkNumAll));
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

				if (footPrint.count(Filters.and(Filters.eq("stockCode", stockCode), Filters.exists(COLUNM_NAME),
						Filters.gt(COLUNM_NAME, 0))) > 0) {
					continue;
				}
				String webSite = doc.getString("webSite");
				List<NameValuePair> data = Lists.newArrayList();
				String domain = UrlUtils.getDomain(webSite);
				data.add(new BasicNameValuePair("host", domain));

				String prefix = "json_" + System.currentTimeMillis();

				Request request = new Request(URL_PATTERN + prefix).putExtra("stockCode", stockCode)
						.putExtra("nameValuePair", data.toArray(new NameValuePair[] {})).putExtra("prefix", prefix)
						.putExtra("domain", domain);
				request.setMethod(HttpConstant.Method.POST);
				requests.add(request);
				// break;
			}
		} finally {
			iterator.close();
		}
		OutLinksPageProcessor processor = new OutLinksPageProcessor();
		processor.getSite().setHttpProxyPool(ProxyReader.read(), false);
		Spider.create(processor).setDownloader(new ProxyHttpClientDownloader()).addPipeline(new FootprintPipeline())
				.addRequest(requests.toArray(new Request[] {})).thread(1).run();
	}

}
