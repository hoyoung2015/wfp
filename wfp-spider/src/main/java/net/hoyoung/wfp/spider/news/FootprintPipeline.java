package net.hoyoung.wfp.spider.news;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Updates;

import net.hoyoung.wfp.core.utils.MongoUtil;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class FootprintPipeline implements Pipeline {

	private static final String COLLECTION_NAME = "footprint";

	public FootprintPipeline() {
		MongoCollection<Document> collection = MongoUtil.getClient().getDatabase("wfp").getCollection(COLLECTION_NAME);
		collection.createIndex(Indexes.ascending("stockCode"));
	}

	@Override
	public void process(ResultItems resultItems, Task task) {
		MongoCollection<Document> collection = MongoUtil.getClient().getDatabase("wfp").getCollection(COLLECTION_NAME);
		Document document = resultItems.get("document");
		try {
			Document doc = collection.find(Filters.eq("stockCode", document.getString("stockCode"))).first();
			if (doc == null) {
				collection.insertOne(document);
			} else {
				List<Bson> updates = Lists.newArrayList();
				for (String k : document.keySet()) {
					if ("stockCode".equals(k)) {
						continue;
					}
					if (doc.keySet().contains(k)) {
						continue;
					}
					updates.add(Updates.set(k, document.get(k)));
				}
				if (CollectionUtils.isNotEmpty(updates)) {
					collection.updateOne(Filters.eq("stockCode", document.getString("stockCode")),
							Updates.combine(updates));
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
