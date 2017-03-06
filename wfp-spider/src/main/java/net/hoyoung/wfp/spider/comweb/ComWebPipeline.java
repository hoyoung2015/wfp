package net.hoyoung.wfp.spider.comweb;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;
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
		String stockCode = (String) resultItems.getRequest().getExtra(ComPage.STOCK_CODE);
		MongoCollection<Document> collectionTmp = MongoUtil.getCollection(ComWebConstant.DB_NAME, stockCode + "_tmp");
		for (Document document : list) {
			String sha1 = document.getString(ComPage.CONTENT_SHA1);
			if (StringUtils.isNotEmpty(sha1) && collectionTmp.count(Filters.eq(ComPage.CONTENT_SHA1, sha1)) > 0) {
				continue;
			}

			try {
				collectionTmp.insertOne(document);
			} catch (MongoWriteException e) {

			} catch (DuplicateKeyException e) {

			}
		}

	}
}
