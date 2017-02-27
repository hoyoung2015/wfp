package net.hoyoung.wfp.core.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bson.Document;

import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class MongoUtil {
	private static MongoClient mongoClient = null;

	private static ThreadLocal<Map<String, MongoCollection<Document>>> dbLocal = new ThreadLocal<>();

	public static MongoClient getClient() {
		if (mongoClient == null) {
			synchronized (MongoUtil.class) {
				if (mongoClient == null) {
					mongoClient = new MongoClient(WFPContext.getProperty("mongodb.host"),
							WFPContext.getProperty("mongodb.port", Integer.class));
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

	public static List<String> getCollectionNames(String db, String regex) {
		List<String> rs = Lists.newArrayList();

		MongoCursor<String> iterator = getClient().getDatabase(db).listCollectionNames().iterator();
		while (iterator.hasNext()) {
			String name = iterator.next();
			if (regex != null && !"".equals(regex) && !Pattern.matches(regex, name)) {
				continue;
			}
			rs.add(name);
		}
		return rs;
	}

	public static void main(String[] args) {
		// MongoCollection<Document> collection =
		// MongoUtil.getCollection("wfp_spider", "com_page");
		List<String> list = MongoUtil.getCollectionNames("wfp_com_page", "\\d{6}");
		for (String s : list) {
			System.out.println(s);
		}
	}
}
