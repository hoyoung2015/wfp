package net.hoyoung.wfp.stock;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Indexes;

import net.hoyoung.wfp.core.utils.MongoUtil;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class TenStockPipeline implements Pipeline {

	
	
	public TenStockPipeline() {
		MongoCollection<Document> collection = MongoUtil.getClient().getDatabase("wfp").getCollection("ten_stock");
		collection.createIndex(Indexes.ascending("stockCode"));
	}

	@Override
	public void process(ResultItems resultItems, Task task) {

		MongoCollection<Document> collection = MongoUtil.getClient().getDatabase("wfp").getCollection("ten_stock");
		Document document = resultItems.get("document");
		try {
			collection.insertOne(document);
		} catch (Exception e) {
			
		}
		
	}

}
