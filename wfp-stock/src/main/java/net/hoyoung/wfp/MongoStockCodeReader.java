package net.hoyoung.wfp;

import java.io.IOException;
import java.util.List;

import org.bson.Document;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Projections;

import net.hoyoung.wfp.core.utils.MongoUtil;

public class MongoStockCodeReader implements StockCodeReader {

	public MongoStockCodeReader() {
		super();
	}

	@Override
	public List<String> getStockCodes() throws IOException {
		MongoCollection<Document> collection = MongoUtil.getClient().getDatabase("wfp").getCollection("web_source");
		List<String> list = Lists.newArrayList();
		MongoCursor<Document> iterator = collection.find().projection(Projections.include("stockCode")).iterator();
		try {
			while (iterator.hasNext()) {
				Document doc = iterator.next();
				String stockCode = doc.getString("stockCode");
				list.add(stockCode);
			}
		} finally {
			iterator.close();
		}
		return list;
	}

}
