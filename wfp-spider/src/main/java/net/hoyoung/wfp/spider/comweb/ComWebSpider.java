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
import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.mongodb.MongoNamespace;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.core.utils.ProxyReader;
import net.hoyoung.wfp.core.utils.RedisUtil;
import net.hoyoung.wfp.core.utils.WFPContext;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;
import net.hoyoung.wfp.spider.comweb.vo.ComVo;
import net.hoyoung.wfp.spider.util.URLNormalizer;
import redis.clients.jedis.Jedis;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.proxy.ComWebProxyPool;
import us.codecraft.webmagic.scheduler.MyRedisScheduler;
import us.codecraft.webmagic.utils.UrlUtils;

public class ComWebSpider {
	Logger logger = LoggerFactory.getLogger(getClass());

	private String siteFile = null;

	public ComWebSpider(String siteFile) {
		super();
		this.siteFile = siteFile;
	}

	private List<ComVo> readCom() {
		List<ComVo> list = Lists.newArrayList();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(siteFile)));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (StringUtils.isEmpty(line) || line.startsWith("#")) {
					continue;
				}
				String[] split = line.split("\t");
				ComVo vo = new ComVo(split[0], split[1], split[2], Integer.valueOf(split[3]));
				if (split.length > 4) {
					vo.setUserAgent(split[4]);
				}
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

	public Set<String> getCollectionNameSet() {
		Set<String> set = new HashSet<>();
		MongoCursor<String> iterator = MongoUtil.getClient().getDatabase(ComWebConstant.DB_NAME).listCollectionNames()
				.iterator();
		try {
			while (iterator.hasNext()) {
				String name = iterator.next();
				if (!name.endsWith("tmp")) {
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
		ExecutorService threadPool = Executors
				.newFixedThreadPool(WFPContext.getProperty("compage.executors", Integer.class));
		Set<String> nameSet = getCollectionNameSet();
		for (ComVo vo : list) {
			if (nameSet.contains(vo.getStockCode())) {
				logger.info(vo.getStockCode() + " " + vo.getWebSite() + " has been crawled");
				continue;
			}
			if (new File(vo.getStockCode() + ".fail").exists()) {
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
			MongoCollection<Document> collectionTmp = MongoUtil.getCollection(ComWebConstant.DB_NAME,
					com.getStockCode() + "_tmp");
			String indexTmp = collectionTmp.createIndex(Indexes.ascending(ComPage.STOCK_CODE, ComPage.URL),
					new IndexOptions().unique(true));
			// sha1 去重
			collectionTmp.createIndex(Indexes.ascending(ComPage.CONTENT_SHA1));
			LOG.info("{} create index {}", collectionTmp.getNamespace(), indexTmp);
			ComWebProcessor processor = new ComWebProcessor();
			if (com.getSleepTime() > 0) {
				processor.getSite().setSleepTime(com.getSleepTime());
			}
			if (com.getUserAgent() != null) {
				processor.getSite().addHeader("User-Agent", com.getUserAgent());
			}
			// 设置代理
			if (WFPContext.getProperty("compage.spider.useProxy", Boolean.class)) {
				processor.getSite().setHttpProxyPool(new ComWebProxyPool(ProxyReader.read(), false));
			}
			MyRedisScheduler redisScheduler = new MyRedisScheduler("127.0.0.1");
			Spider spider = Spider.create(processor).setScheduler(redisScheduler)
					.thread(WFPContext.getProperty("compage.spider.thread", Integer.class));
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
			
			long llen = collectionTmp.count(Filters.eq(ComPage.STOCK_CODE, com.getStockCode()));
			if (spiderListener.isFail() || llen <= 1) {
				// collectionTmp.drop();
				LOG.warn("{} {} failed", com.getStockCode(), com.getWebSite());
				try {
					FileUtils.writeStringToFile(new File(com.getStockCode() + ".fail"), com.getWebSite() + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}

			} else {
				// 成功的话重命名com_page_000000_tmp -> com_page_000000
				collectionTmp.renameCollection(new MongoNamespace(ComWebConstant.DB_NAME + "." + com.getStockCode()));
				LOG.info("finish " + com.getStockCode() + " " + com.getStockCode());
				// 删除redisSchedule
				Jedis jedis = RedisUtil.getJedis();
				jedis.del("set_" + processor.getSite().getDomain());
				jedis.del("item_" + processor.getSite().getDomain());
			}
		}
	}

}
