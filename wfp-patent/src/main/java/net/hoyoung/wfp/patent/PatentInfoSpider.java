package net.hoyoung.wfp.patent;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.patent.spider.PatentInfoPageProcessor;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

/**
 * 专利详情爬虫
 *
 */
public class PatentInfoSpider {
	Logger logger = LoggerFactory.getLogger(getClass());

	public void startTask() {

		MongoCursor<String> iterator = MongoUtil.getClient().getDatabase(PatentConstant.DB_NAME).listCollectionNames()
				.iterator();
		List<String> collectionNames = Lists.newArrayList();

		try {
			while (iterator.hasNext()) {
				String name = iterator.next();
				if (Pattern.matches("\\d{6}", name)) {
					collectionNames.add(name);
				}
			}
		} finally {
			iterator.close();
		}
		for (String stockCode : collectionNames) {
			MongoCollection<Document> collection = MongoUtil.getCollection(PatentConstant.DB_NAME, stockCode);
			MongoCursor<Document> ite = collection.find(Filters.exists(PatentPage.MAIN_CLASS_ID, false)).iterator();
			List<Request> requests = Lists.newArrayList();
			try {
				while (ite.hasNext()) {
					Document doc = ite.next();
					Request request = new Request(doc.getString(PatentPage.DETAIL_URL));
					request.putExtra("_id", doc.getObjectId("_id"));
					request.putExtra(PatentPage.STOCK_CODE, stockCode);
					requests.add(request);
				}
			} finally {
				ite.close();
			}
			if (CollectionUtils.isEmpty(requests))
				continue;
			long total = collection.count();
			logger.info("start to process {},total {}", stockCode, total);
			Spider.create(new PatentInfoPageProcessor(total)).addRequest(requests.toArray(new Request[] {})).thread(5).run();
		}

	}

	public static void main(String[] args) {
		new PatentInfoSpider().startTask();
	}
}
