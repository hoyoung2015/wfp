package net.hoyoung.wfp.core.utils;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

public class MongoUtil {
	private static MongoClient mongoClient = null;

	private static ThreadLocal<Map<String, MongoCollection<Document>>> dbLocal = new ThreadLocal<>();

	public static MongoClient getClient() {
		if (mongoClient == null) {
			synchronized (MongoUtil.class) {
				if (mongoClient == null) {
					mongoClient = new MongoClient(WFPContext.getProperty("mongodb.host"), WFPContext.getProperty("mongodb.port", Integer.class));
				}
			}
		}
		return mongoClient;
	}

	public static MongoCollection<Document> getCollection(String db, String collection) {
		Map<String, MongoCollection<Document>> map = dbLocal.get();
		if (map == null) {
			dbLocal.set(map = new HashMap<>());
		}
		String key = db + "." + collection;
		MongoCollection<Document> col = map.get(key);
		if (col == null) {
			col = getClient().getDatabase(db).getCollection(collection);
			map.put(key, col);
		}
		return col;
	}

	public static void main(String[] args) {
//		MongoCollection<Document> collection = MongoUtil.getCollection("wfp_spider", "com_page");
	}
}
