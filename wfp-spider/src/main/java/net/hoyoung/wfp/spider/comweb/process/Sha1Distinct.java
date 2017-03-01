package net.hoyoung.wfp.spider.comweb.process;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.spider.comweb.ComWebConstant;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;

public class Sha1Distinct {

	public static void main(String[] args) {
		// String dbName = ComWebConstant.DB_NAME + "_test";
		String dbName = ComWebConstant.DB_NAME;

		List<String> list = MongoUtil.getCollectionNames(dbName, "\\d{6}");
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		int cnt = 0;
		for (String collectionName : list) {
			if (++cnt > 450)
				break;
			MongoCollection<Document> collection = MongoUtil.getCollection(dbName, collectionName);
			Bson filters = Filters.and(Filters.exists(ComPage.CONTENT_SHA1));
			long total = collection.count(filters);
			System.out.println(String.format("%d\tprocess collection %s,total=%d", cnt, collectionName, total));
			MongoCursor<Document> iterator = collection.find(filters)
					.projection(Projections.include(ComPage.CONTENT_SHA1)).iterator();
			Set<String> sha1Set = Sets.newHashSet();
			List<ObjectId> oids = Lists.newArrayList();
			try {
				while (iterator.hasNext()) {
					Document document = iterator.next();
					String sha1 = document.getString(ComPage.CONTENT_SHA1);
					if (sha1Set.contains(sha1)) {
						oids.add(document.getObjectId("_id"));
					} else {
						sha1Set.add(sha1);
					}
				}
			} finally {
				iterator.close();
			}
			System.out.println(String.format("collection %s delete %d", collectionName, oids.size()));
			if (CollectionUtils.isNotEmpty(oids)) {
				collection.deleteMany(Filters.in("_id", oids));
			}
			// break;
		}
	}

}
