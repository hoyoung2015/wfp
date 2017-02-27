package net.hoyoung.wfp.spider.comweb.process;

import static com.mongodb.client.model.Filters.eq;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jsoup.Jsoup;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import net.hoyoung.wfp.core.utils.EncryptUtil;
import net.hoyoung.wfp.core.utils.Md5Util;
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

	public static String getContent(String html) {
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

	public static void main(String[] args) {
		String dbName = ComWebConstant.DB_NAME + "_test";

		List<String> list = MongoUtil.getCollectionNames(dbName, "\\d{6}");
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		for (String collectionName : list) {
			MongoCollection<Document> collection = MongoUtil.getCollection(dbName, collectionName);
			Bson filters = Filters.and(Filters.exists(ComPage.HTML), Filters.exists(ComPage.CONTENT, false));
			long total = collection.count(filters);
			System.out.println(String.format("process collection %s,total=%d", collectionName, total));
			MongoCursor<Document> iterator = collection.find(filters)
					.projection(Projections.include(ComPage.HTML, ComPage.URL)).iterator();
			try {
				while (iterator.hasNext()) {
					Document document = iterator.next();
					System.out.println(String.format("process collection %s_%d\t%s", collectionName, total--,
							document.get(ComPage.URL)));
					String html = document.getString(ComPage.HTML);
					String content = null;
					try {
						content = ContentExtractor.getContentByHtml(html);
					} catch (Exception e) {
						e.printStackTrace();
						content = getContent(html);
					}
					if (content == null) {
						content = "";
					}
					try {
						String contentSha1 = EncryptUtil.encryptSha1(content);
						Bson updates = Updates.combine(Updates.set(ComPage.CONTENT, content),
								Updates.set(ComPage.CONTENT_SHA1, contentSha1));
						collection.updateOne(eq("_id", document.get("_id")), updates);
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					}

				}
			} finally {
				iterator.close();
			}
			// break;
		}
	}

}
