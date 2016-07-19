package net.hoyoung.wfp.weibo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.stereotype.Component;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;

@Component
public class CrawlService {

	@Value("${webcollector.crawlPath}")
	private String crawlPath;
	// private boolean autoParse;

	@Value("${webcollector.threads}")
	private int threads;

	@Value("${webcollector.executeInterval}")
	private long executeInterval;

	private WeiboCrawler weiboCrawler;
	@Autowired
	private MongoDbFactory mongo;

	private String cookie;
	
	@Autowired
	Environment env;

	public void startJob(String stockCode, String url) {
		if (weiboCrawler == null) {
			try {
				cookie = WeiboCN.getSinaCookie("shoman@sina.cn", "19920609qwer@");
			} catch (Exception e) {
				System.err.println("登录失败");
				System.exit(1);
			}
			weiboCrawler = new WeiboCrawler(crawlPath, false);
			weiboCrawler.setCookie(cookie);
			weiboCrawler.setThreads(threads);
			weiboCrawler.setExecuteInterval(executeInterval);
			weiboCrawler.setUserAgent(env.getProperty("webcollector.userAgent",String.class));
		}
		weiboCrawler.setDbCollection(mongo.getDb(env.getProperty("mongodb.dbname", String.class)).getCollection(stockCode));
		CrawlDatum crawlDatum = new CrawlDatum(url + "&page=1").meta("page","1");
		weiboCrawler.addSeed(crawlDatum);
		try {
			weiboCrawler.start(env.getProperty("webcollector.depth",Integer.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
