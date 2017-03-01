package net.hoyoung.wfp.spider.comweb.process;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.spider.comweb.ComWebConstant;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;

public class DeleteUnknownContentType {

	public static void main(String[] args) {

		// String dbName = ComWebConstant.DB_NAME + "_test";
		String dbName = ComWebConstant.DB_NAME;

		List<String> list = MongoUtil.getCollectionNames(dbName, "\\d{6}");
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		int cnt = 0;
		for (String collectionName : list) {
//			if (++cnt > 50)
//				break;
			MongoCollection<Document> collection = MongoUtil.getCollection(dbName, collectionName);
			Bson filters = Filters.and(Filters.eq(ComPage.CONTENT_TYPE, "unknown"));
			System.out.println(String.format("process collection %s", collectionName));
			collection.deleteMany(filters);
			// break;
		}
	}

}
