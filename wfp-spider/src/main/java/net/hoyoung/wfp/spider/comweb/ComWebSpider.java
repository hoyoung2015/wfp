package net.hoyoung.wfp.spider.comweb;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Projections;

import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.core.utils.RedisUtil;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;
import net.hoyoung.wfp.spider.util.ProxyReader;
import net.hoyoung.wfp.spider.util.URLNormalizer;
import redis.clients.jedis.Jedis;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.RedisScheduler;
import us.codecraft.webmagic.utils.UrlUtils;

public class ComWebSpider {
	static Logger LOG = LoggerFactory.getLogger(ComWebSpider.class);

	public static final int THREAD_NUM = 12;

	public static void main(String[] args) {
		String input = null;
		if (args == null || args.length != 1) {
			System.err.println("input file error");
			System.exit(-3);
		}
		input = args[0];
		List<String> lines = null;
		try {
			lines = readLines(input);
			if (CollectionUtils.isEmpty(lines)) {
				System.err.println("site list is empty");
				System.exit(-1);
			}
		} catch (IOException e) {
			System.err.println("read site list error");
			System.exit(-2);
		}

		// 建索引
		MongoCollection<Document> collectionTmp = MongoUtil.getCollection(ComWebConstant.DB_NAME,
				ComWebConstant.COLLECTION_NAME_TMP);
		String indexTmp = collectionTmp.createIndex(Indexes.ascending(ComPage.STOCK_CODE, ComPage.URL),
				new IndexOptions().unique(true));
		LOG.info("{} create index {}", collectionTmp.getNamespace(), indexTmp);

		MongoCollection<Document> collection = MongoUtil.getCollection(ComWebConstant.DB_NAME,
				ComWebConstant.COLLECTION_NAME);
		String index = collection.createIndex(Indexes.ascending(ComPage.STOCK_CODE, ComPage.URL),
				new IndexOptions().unique(true));
		LOG.info("{} create index {}", collection.getNamespace(), index);

		ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_NUM);
		for (String line : lines) {
			if (line.startsWith("#")) {
				continue;
			}
			String[] split = line.split("\t");
			String stockCode = split[0];
			String webSite = split[2];
			int sleepTime = Integer.valueOf(split[3]);
			if (collection.count(Filters.eq(ComPage.STOCK_CODE, stockCode)) > 0) {
				LOG.info(stockCode + " " + webSite + " has been crawled");
				continue;
			}
			threadPool.execute(new TaskExecutor(stockCode, webSite, sleepTime));
		}
		threadPool.shutdown();
	}

	@SuppressWarnings("unchecked")
	static List<String> readLines(String filePath) throws IOException {
		List<String> lines = FileUtils.readLines(new File(filePath), "UTF-8");
		return lines;
	}

	static class TaskExecutor implements Runnable {

		private static Logger LOG = LoggerFactory.getLogger(TaskExecutor.class);

		private String stockCode;
		private String webSite;
		private int sleepTime;

		public TaskExecutor(String stockCode, String webSite, int sleepTime) {
			super();
			this.stockCode = stockCode;
			this.webSite = webSite;
			this.sleepTime = sleepTime;
		}

		@Override
		public void run() {
			LOG.info("start crawl {} {}", stockCode, webSite);
			ComWebProcessor processor = new ComWebProcessor();
			if (this.sleepTime > 0) {
				processor.getSite().setSleepTime(sleepTime);
			}
			// 设置代理
			List<String[]> proxies = null;
			try {
				proxies = ProxyReader.read("proxy.txt");
			} catch (IOException e) {
				LOG.warn("{} start error {}", stockCode, e.getStackTrace());
				return;
			}
			processor.getSite().setHttpProxyPool(proxies, true);
			RedisScheduler redisScheduler = new RedisScheduler("127.0.0.1");
			Spider spider = Spider.create(processor).setScheduler(redisScheduler).thread(1);
			ComWebSpiderListener spiderListener = new ComWebSpiderListener(spider);
			spider.setSpiderListeners(Lists.newArrayList(spiderListener));
			Request request;
			try {
				request = new Request(URLNormalizer.normalize(webSite));
			} catch (MalformedURLException e1) {
				LOG.warn("{} normalize error", webSite);
				return;
			}
			request.putExtra(ComPage.STOCK_CODE, stockCode);
			request.putExtra("domain", UrlUtils.getDomain(webSite).replaceAll("^www\\.", ""));
			spider.addRequest(request).setDownloader(new ComWebHttpClientDownloader()).addPipeline(new ComWebPipeline())
					.run();
			// 删除redisSchedule
			Jedis jedis = RedisUtil.getJedis();
			jedis.del("set_" + processor.getSite().getDomain());
			jedis.del("item_" + processor.getSite().getDomain());
			MongoCollection<Document> collectionTmp = MongoUtil.getCollection(ComWebConstant.DB_NAME,
					ComWebConstant.COLLECTION_NAME_TMP);
			long llen = collectionTmp.count(Filters.eq(ComPage.STOCK_CODE, stockCode));
			if (spiderListener.isFail() || llen == 1) {
				LOG.warn("{} {} failed", stockCode, webSite);
				try {
					FileUtils.writeStringToFile(new File(stockCode + ".fail"), webSite);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				MongoCollection<Document> collection = MongoUtil.getCollection(ComWebConstant.DB_NAME,
						ComWebConstant.COLLECTION_NAME);
				MongoCursor<Document> iterator = collectionTmp.find(Filters.eq(ComPage.STOCK_CODE, stockCode))
						.projection(Projections.exclude("_id")).iterator();
				try {
					while (iterator.hasNext()) {
						Document document = iterator.next();
						try {
							collection.insertOne(document);
						} catch (MongoWriteException e) {
							LOG.info("duplicate key +" + stockCode + " " + document.getString(ComPage.URL));
						}
					}
				} finally {
					iterator.close();
					collectionTmp.deleteMany(Filters.eq(ComPage.STOCK_CODE, stockCode));
					LOG.info("finish " + stockCode + " " + webSite);
				}
			}
		}

	}

}
