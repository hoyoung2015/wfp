package net.hoyoung.wfp.patent.spider;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;

import net.hoyoung.wfp.core.utils.RedisUtil;
import net.hoyoung.wfp.patent.PatentConstant;
import redis.clients.jedis.Jedis;
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
		Jedis jedis = RedisUtil.getJedis();
		for (Document document : documents) {
			jedis.lpush(PatentConstant.REDIS_KEY, document.toJson());
		}
	}

}
