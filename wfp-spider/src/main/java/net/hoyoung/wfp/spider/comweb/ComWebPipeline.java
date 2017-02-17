package net.hoyoung.wfp.spider.comweb;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;

import net.hoyoung.wfp.core.utils.MongoUtil;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class ComWebPipeline implements Pipeline {

	@Override
	public void process(ResultItems resultItems, Task task) {
		
		
		List<Document> list = resultItems.get(ComWebConstant.URL_LIST_KEY);
		
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		MongoCollection<Document> collectionTmp = MongoUtil.getCollection(ComWebConstant.DB_NAME,
				ComWebConstant.COLLECTION_NAME_TMP);
		for (Document document : list) {
			try {
				collectionTmp.insertOne(document);
			} catch (MongoWriteException e) {
				
			}
		}
		
	}
}
