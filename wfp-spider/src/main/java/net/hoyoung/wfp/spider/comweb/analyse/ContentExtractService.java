package net.hoyoung.wfp.spider.comweb.analyse;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Updates;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.spider.comweb.ComWebConstant;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;

public class ContentExtractService {

	public static void main(String[] args) {
		MongoCollection<Document> collection = MongoUtil.getCollection(ComWebConstant.DB_NAME,
				ComWebConstant.COLLECTION_NAME);
		long total = collection.count();
		/**
		 * content不存在 html存在
		 */
		MongoCursor<Document> iterator = collection.find(and(exists(ComPage.CONTENT, false), exists(ComPage.HTML)))
				.iterator();
		try {
			while (iterator.hasNext()) {
				Document document = iterator.next();
				String url = document.getString(ComPage.URL);
				System.out.println(total-- + ":" + url);
				String html = document.getString(ComPage.HTML);
				String content = null;
				try {
					content = ContentExtractor.getContentByHtml(html);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (content == null) {
					content = "";
				}
				collection.updateOne(eq("_id", document.get("_id")), Updates.set(ComPage.CONTENT, content));
			}
		} finally {
			iterator.close();
		}

	}
}
