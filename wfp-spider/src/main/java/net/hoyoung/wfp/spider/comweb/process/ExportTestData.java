package net.hoyoung.wfp.spider.comweb.process;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Projections;

import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.spider.comweb.ComWebConstant;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;

public class ExportTestData {

	public static void main(String[] args) {
		int n = 10;
		List<String> list = MongoUtil.getCollectionNames(ComWebConstant.DB_NAME, "\\d{6}");
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		for (int i = 0; i < list.size() && i < n; i++) {
			String collectionName = list.get(i);
			System.out.println(String.format("process %s", collectionName));
			MongoCollection<Document> collection = MongoUtil.getCollection(ComWebConstant.DB_NAME, collectionName );
			MongoCursor<Document> iterator = collection.find().projection(Projections.exclude("_id")).iterator();
			MongoCollection<Document> test = MongoUtil.getCollection(ComWebConstant.DB_NAME+"_test", collectionName);
			test.createIndex(Indexes.ascending(ComPage.STOCK_CODE,ComPage.URL),new IndexOptions().unique(true));
			try {
				while(iterator.hasNext()){
					Document document = iterator.next();
					test.insertOne(document);
				}
			} finally {
				iterator.close();
			}
		}
	}

}
