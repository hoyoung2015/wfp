package net.hoyoung.wfp.patent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.patent.spider.PatentInfoPageProcessor;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.PatentHttpClientDownloader;

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
		Set<String> skipSet = Sets.newHashSet();
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("skip_patent_list.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		try {
			String tmp = null;
			while ((tmp = br.readLine()) != null) {
				tmp = tmp.trim().replace("\n", "");
				if (tmp.startsWith("#")) {
					continue;
				}
				skipSet.add(String.format("http://d.wanfangdata.com.cn/Patent/%s/", tmp));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		for (String stockCode : collectionNames) {
			MongoCollection<Document> collection = MongoUtil.getCollection(PatentConstant.DB_NAME, stockCode);
			MongoCursor<Document> ite = collection.find(Filters.exists(PatentPage.MAIN_CLASS_ID, false)).iterator();
			List<Request> requests = Lists.newArrayList();
			try {
				while (ite.hasNext()) {
					Document doc = ite.next();
					String detailUrl = doc.getString(PatentPage.DETAIL_URL);
					if (skipSet.contains(detailUrl)) {
						continue;
					}
					Request request = new Request(detailUrl);
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
			PatentInfoPageProcessor pageProcessor = new PatentInfoPageProcessor(total);
//			pageProcessor.getSite().setHttpProxyPool(ProxyReader.read(), false);
			Spider.create(pageProcessor).setDownloader(new PatentHttpClientDownloader())
					.addRequest(requests.toArray(new Request[] {})).thread(5).run();
		}

	}

	public static void main(String[] args) {
		new PatentInfoSpider().startTask();
	}
}
