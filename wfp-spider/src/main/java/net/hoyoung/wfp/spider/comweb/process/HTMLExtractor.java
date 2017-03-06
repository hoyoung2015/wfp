package net.hoyoung.wfp.spider.comweb.process;

import static com.mongodb.client.model.Filters.eq;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.spider.comweb.ComWebConstant;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;

/**
 * html内容提取
 * 
 * @author huyang09
 *
 */
public class HTMLExtractor {
	private static Logger LOG = LoggerFactory.getLogger(HTMLExtractor.class);

	public static String getContentAll(String html) {
		org.jsoup.nodes.Document doc = Jsoup.parse(html);
		doc.getElementsByTag("a").remove();
		doc.getElementsByTag("button").remove();
		doc.getElementsByTag("input").remove();
		doc.getElementsByTag("header").remove();
		doc.getElementsByTag("textarea").remove();
		doc.getElementsByTag("script").remove();
		doc.getElementsByTag("form").remove();
		return doc.getElementsByTag("body").text();
	}

	public static String getContent(String html) {
		if (html == null) {
			return null;
		}
		String content = null;
		try {
			content = ContentExtractor.getContentByHtml(html);
		} catch (Exception e) {
			content = getContentAll(html);
		}
		return content;
	}

	public static void main(String[] args) {
		// String dbName = ComWebConstant.DB_NAME + "_test";
		String dbName = ComWebConstant.DB_NAME;

		List<String> list = MongoUtil.getCollectionNames(dbName, "\\d{6}");
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		for (String collectionName : list) {
			MongoCollection<Document> collection = MongoUtil.getCollection(dbName, collectionName);
			Bson filters = Filters.and(Filters.exists(ComPage.HTML), Filters.exists(ComPage.CONTENT, false));
			long total = collection.count(filters);
			LOG.info(String.format("process collection %s,total=%d", collectionName, total));
			MongoCursor<Document> iterator = collection.find(filters)
					.projection(Projections.include(ComPage.HTML, ComPage.URL)).iterator();
			try {
				while (iterator.hasNext()) {
					Document document = iterator.next();
					LOG.info(String.format("process collection %s_%d\t%s", collectionName, total--,
							document.get(ComPage.URL)));
					String html = document.getString(ComPage.HTML);
					String content = getContent(html);
					if (content == null) {
						content = "";
					}
					Bson updates = Updates.combine(Updates.set(ComPage.CONTENT, content));
					collection.updateOne(eq("_id", document.get("_id")), updates);
				}
			} finally {
				iterator.close();
			}
			// break;
		}
	}

}
