package net.hoyoung.wfp.patent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.mongodb.MongoNamespace;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import net.hoyoung.wfp.core.bo.ComInfo;
import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.patent.input.ComInfoInput;
import net.hoyoung.wfp.patent.input.FileComInfoInput;
import net.hoyoung.wfp.patent.spider.PatentPipeline;
import net.hoyoung.wfp.patent.spider.PatientPageProcessor;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

/**
 * Hello world!
 *
 */
public class PatentSpider {
	Logger logger = LoggerFactory.getLogger(getClass());

	private ComInfoInput comInfoInput;

	public PatentSpider(ComInfoInput comInfoInput) {
		super();
		this.comInfoInput = comInfoInput;
	}

	public void startTask() {
		List<ComInfo> list = comInfoInput.getComInfo();

		if (CollectionUtils.isEmpty(list)) {
			System.err.println("company is empty");
			System.exit(-1);
		}
		Set<String> collectionNameSet = getCollectionNameSet();

		MongoCollection<Document> description = MongoUtil.getCollection(PatentConstant.DB_NAME, "description");
		description.createIndex(Indexes.ascending(PatentPage.STOCK_CODE), new IndexOptions().unique(true));
		for (ComInfo comInfo : list) {
			if (collectionNameSet.contains(comInfo.getStockCode())) {
				logger.info("company {} has been crawled", comInfo);
				continue;
			}
			logger.info("start to crawl {}({})", comInfo.getName(), comInfo.getStockCode());
			// 创建索引
			MongoCollection<Document> tmp = MongoUtil.getCollection(PatentConstant.DB_NAME,
					comInfo.getStockCode() + "_tmp");
			tmp.createIndex(Indexes.ascending(PatentPage.STOCK_CODE, PatentPage.DETAIL_URL),
					new IndexOptions().unique(true));

			String fullName = comInfo.getName();
			String url;
			try {
				url = "http://s.wanfangdata.com.cn/patent.aspx?q="
						+ URLEncoder.encode("专利权人:" + fullName, "UTF-8").toLowerCase() + "&f=top&p=1";
				Request request = new Request(url);
				request.putExtra(PatentPage.STOCK_CODE, comInfo.getStockCode());

				PatientPageProcessor pageProcessor = new PatientPageProcessor();
				// 设置代理
				// pageProcessor.getSite().setHttpProxyPool(ProxyReader.read(),
				// false);
				Spider.create(pageProcessor).addPipeline(new PatentPipeline()).addRequest(request).thread(1).run();

				Document desc = description.find(Filters.eq(PatentPage.STOCK_CODE, comInfo.getStockCode())).first();
				if (tmp.count() == 0 || tmp.count() == desc.getInteger("total")) {
					tmp.renameCollection(new MongoNamespace(PatentConstant.DB_NAME, comInfo.getStockCode()));
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				logger.warn("company {} encoding error", comInfo.getStockCode());
			}
		}
	}

	private Set<String> getCollectionNameSet() {
		MongoDatabase db = MongoUtil.getClient().getDatabase(PatentConstant.DB_NAME);
		Set<String> set = Sets.newHashSet();
		for (String name : db.listCollectionNames()) {
			if (name.endsWith("_tmp")) {
				continue;
			}
			set.add(name);
		}
		return set;
	}

	public static void main(String[] args) {
		if (args == null || args.length != 1) {
			System.err.println("com_info.txt not found");
			System.exit(-1);
		}
		new PatentSpider(new FileComInfoInput(args[0])).startTask();
	}
}
