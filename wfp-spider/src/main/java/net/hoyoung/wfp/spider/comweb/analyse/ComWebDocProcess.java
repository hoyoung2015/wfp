package net.hoyoung.wfp.spider.comweb.analyse;

import java.util.List;

import org.bson.Document;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.spider.comweb.ComWebConstant;
import net.hoyoung.wfp.spider.comweb.ComWebUrlUtils;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;

public class ComWebDocProcess {

	public static void main(String[] args) {
		MongoCollection<Document> collection = MongoUtil.getCollection(ComWebConstant.DB_NAME, ComWebConstant.COLLECTION_NAME);
		Document filter = new Document(ComPage.URL, new Document("$regex", ComWebUrlUtils.DOC_REGEX));

		MongoCursor<Document> iterator = collection.find(filter).iterator();
		List<String> docUrls = Lists.newArrayList();
		while (iterator.hasNext()) {
			Document doc = iterator.next();
			docUrls.add(doc.getString(ComPage.URL));
		}
		
	}
}
