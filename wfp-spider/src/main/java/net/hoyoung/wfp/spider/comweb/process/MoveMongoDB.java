package net.hoyoung.wfp.spider.comweb.process;

import java.util.List;

import org.bson.Document;

import com.google.common.collect.Lists;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.spider.comweb.ComWebConstant;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;

public class MoveMongoDB {

	public static void main(String[] args) {

		MongoCollection<Document> collectionTmp = MongoUtil.getCollection(ComWebConstant.DB_NAME,
				ComWebConstant.COLLECTION_NAME_TMP);

//		String stockCode = "600526";
		String stockCode = "000983";

		MongoCollection<Document> collection = MongoUtil.getCollection(ComWebConstant.DB_NAME,
				ComWebConstant.COLLECTION_PREFIX + "t0");

		long total = collectionTmp.count(Filters.eq(ComPage.STOCK_CODE, stockCode));

		
		MongoCursor<Document> iterator = collectionTmp.find(Filters.eq(ComPage.STOCK_CODE, stockCode))
				.projection(Projections.exclude("_id")).iterator();

		int batchSize = 2000;
		List<Document> list = Lists.newArrayList();
		long start = System.currentTimeMillis();
		while (iterator.hasNext()) {
			Document document = iterator.next();
			list.add(document);
			System.out.println(total--);
			if (list.size() == batchSize) {
				try {
					collection.insertMany(list);
				} catch (MongoWriteException e) {
					System.out.println("duplicate key");
				}
				list.clear();
			} 
		}
		System.out.println("cost "+(System.currentTimeMillis()-start)/1000);
	}

}
