package net.hoyoung.wfp.spider.comweb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
	Logger logger = LoggerFactory.getLogger(getClass());

	public static final int THREAD_NUM = 12;

	private String siteFile = null;

	public ComWebSpider(String siteFile) {
		super();
		this.siteFile = siteFile;
	}

	class ComVo {
		private String stockCode;
		private String sname;
		private String webSite;
		private Integer sleepTime;

		public ComVo(String stockCode, String sname, String webSite, Integer sleepTime) {
			super();
			this.stockCode = stockCode;
			this.sname = sname;
			this.webSite = webSite;
			this.sleepTime = sleepTime;
		}

		public String getStockCode() {
			return stockCode;
		}

		public void setStockCode(String stockCode) {
			this.stockCode = stockCode;
		}

		public String getSname() {
			return sname;
		}

		public void setSname(String sname) {
			this.sname = sname;
		}

		public String getWebSite() {
			return webSite;
		}

		public void setWebSite(String webSite) {
			this.webSite = webSite;
		}

		public Integer getSleepTime() {
			return sleepTime;
		}

		public void setSleepTime(Integer sleepTime) {
			this.sleepTime = sleepTime;
		}

	}

	private void createIndex() {
		// 建索引
		MongoCollection<Document> collectionTmp = MongoUtil.getCollection(ComWebConstant.DB_NAME,
				ComWebConstant.COLLECTION_NAME_TMP);
		String indexTmp = collectionTmp.createIndex(Indexes.ascending(ComPage.STOCK_CODE, ComPage.URL),
				new IndexOptions().unique(true));
		logger.info("{} create index {}", collectionTmp.getNamespace(), indexTmp);
	}

	private List<ComVo> readCom() {
		List<ComVo> list = Lists.newArrayList();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(siteFile)));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				String[] split = line.split("\t");
				ComVo vo = new ComVo(split[0], split[1], split[2], Integer.valueOf(split[3]));
				list.add(vo);
			}

		} catch (IOException e) {
			logger.warn("read site list error {}", e.getMessage());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	
	public Set<String> getCollectionNameSet(){
		Set<String> set = new HashSet<>();
		MongoCursor<String> iterator = MongoUtil.getClient().getDatabase(ComWebConstant.DB_NAME).listCollectionNames().iterator();
		try {
			while(iterator.hasNext()){
				String name = iterator.next();
				if(name.startsWith(ComWebConstant.COLLECTION_PREFIX)){
					set.add(name);
				}
			}
		} finally {
			iterator.close();
		}
		return set;
	}

	public void start() {
		List<ComVo> list = readCom();
		if (CollectionUtils.isEmpty(list)) {
			System.exit(-1);
		}
		this.createIndex();
		ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_NUM);
		Set<String> nameSet = getCollectionNameSet();
		for (ComVo vo : list) {
			if (nameSet.contains(ComWebConstant.COLLECTION_PREFIX+vo.getStockCode())) {
				logger.info(vo.getStockCode() + " " + vo.getWebSite() + " has been crawled");
				continue;
			}
			if(new File(vo.getStockCode()+".fail").exists()){
				logger.info(vo.getStockCode() + " " + vo.getWebSite() + " is arready fail");
				continue;
			}
			threadPool.execute(new TaskExecutor(vo));
		}
		threadPool.shutdown();
	}

	public static void main(String[] args) {
		String input = null;
		if (args == null || args.length != 1) {
			System.err.println("input file error");
			System.exit(-3);
		}
		input = args[0];
		new ComWebSpider(input).start();
	}

	static class TaskExecutor implements Runnable {

		private static Logger LOG = LoggerFactory.getLogger(TaskExecutor.class);

		private ComVo com;

		public TaskExecutor(ComVo vo) {
			this.com = vo;
		}

		@Override
		public void run() {
			LOG.info("start crawl {} {}", com.getStockCode(), com.getWebSite());
			ComWebProcessor processor = new ComWebProcessor();
			if (com.getSleepTime() > 0) {
				processor.getSite().setSleepTime(com.getSleepTime());
			}
			// 设置代理
			List<String[]> proxies = null;
			try {
				proxies = ProxyReader.read("proxy.txt");
			} catch (IOException e) {
				LOG.warn("{} start error {}", com.getStockCode(), e.getStackTrace());
				return;
			}
			processor.getSite().setHttpProxyPool(proxies, false);
			RedisScheduler redisScheduler = new RedisScheduler("127.0.0.1");
			Spider spider = Spider.create(processor).setScheduler(redisScheduler).thread(3);
			ComWebSpiderListener spiderListener = new ComWebSpiderListener(spider);
			spider.setSpiderListeners(Lists.newArrayList(spiderListener));
			Request request;
			try {
				request = new Request(URLNormalizer.normalize(com.getWebSite()));
			} catch (MalformedURLException e1) {
				LOG.warn("{} normalize error", com.getWebSite());
				return;
			}
			request.putExtra(ComPage.STOCK_CODE, com.getStockCode());
			request.putExtra("domain", UrlUtils.getDomain(com.getWebSite()).replaceAll("^www\\.", ""));
			spider.addRequest(request).setDownloader(new ComWebHttpClientDownloader()).addPipeline(new ComWebPipeline())
					.run();
			// 删除redisSchedule
			Jedis jedis = RedisUtil.getJedis();
			jedis.del("set_" + processor.getSite().getDomain());
			jedis.del("item_" + processor.getSite().getDomain());
			MongoCollection<Document> collectionTmp = MongoUtil.getCollection(ComWebConstant.DB_NAME,
					ComWebConstant.COLLECTION_NAME_TMP);
			long llen = collectionTmp.count(Filters.eq(ComPage.STOCK_CODE, com.getStockCode()));
			if (spiderListener.isFail() || llen == 1) {
				LOG.warn("{} {} failed", com.getStockCode(), com.getWebSite());
				try {
					FileUtils.writeStringToFile(new File(com.getStockCode() + ".fail"), com.getWebSite());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				extract();
			}
		}
		private void extract(){
			// 创建新的collection，com_page_stockcode，如com_page_000123
			MongoCollection<Document> collection = MongoUtil.getCollection(ComWebConstant.DB_NAME,
					ComWebConstant.COLLECTION_PREFIX+this.com.getStockCode());
			MongoCollection<Document> collectionTmp = MongoUtil.getCollection(ComWebConstant.DB_NAME,
					ComWebConstant.COLLECTION_NAME_TMP);
			MongoCursor<Document> iterator = collectionTmp.find(Filters.eq(ComPage.STOCK_CODE, com.getStockCode()))
					.projection(Projections.exclude("_id")).iterator();
			try {
				while (iterator.hasNext()) {
					Document document = iterator.next();
					try {
						collection.insertOne(document);
					} catch (MongoWriteException e) {
						LOG.info("duplicate key +" + com.getStockCode() + " " + document.getString(ComPage.URL));
					}
				}
			} finally {
				iterator.close();
				collectionTmp.deleteMany(Filters.eq(ComPage.STOCK_CODE, com.getStockCode()));
				LOG.info("finish " + com.getStockCode() + " " + com.getStockCode());
			}
		}
	}
	
}
