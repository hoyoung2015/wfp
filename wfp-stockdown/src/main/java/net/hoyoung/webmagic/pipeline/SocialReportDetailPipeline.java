package net.hoyoung.webmagic.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.BasicDBObject;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * 企业社会责任报告详细也持久化类
 */
public class SocialReportDetailPipeline implements Pipeline {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private MongoTemplate mongoTemplate;
	
	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public SocialReportDetailPipeline(MongoTemplate mongoTemplate) {
		super();
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public void process(ResultItems resultItems, Task task) {
		BasicDBObject dbo = resultItems.get("dbo");
		logger.info(dbo.toString());
		mongoTemplate.getCollection("zrbg_flat").insert(dbo);
	}

}
