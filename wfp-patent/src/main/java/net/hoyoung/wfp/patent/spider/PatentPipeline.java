package net.hoyoung.wfp.patent.spider;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;

import com.mongodb.client.MongoCollection;

import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.patent.PatentConstant;
import net.hoyoung.wfp.patent.PatentPage;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class PatentPipeline implements Pipeline {

	@Override
	public void process(ResultItems resultItems, Task task) {
		List<Document> documents = resultItems.get("documents");
		if (CollectionUtils.isEmpty(documents)) {
			return;
		}
		String stockCode = (String) resultItems.getRequest().getExtra(PatentPage.STOCK_CODE);
		MongoCollection<Document> tmp = MongoUtil.getCollection(PatentConstant.DB_NAME, stockCode+"_tmp");
		for (Document document : documents) {
			tmp.insertOne(document);
		}
	}

}
