package net.hoyoung.webmagic.pipeline;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.BasicDBObject;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class SocialReportDetailPipeline implements Pipeline {
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
		System.err.println("===========================================------------------------------------------------");
		BasicDBObject dbo = resultItems.get("dbo");
		System.err.println(dbo.toString());
		mongoTemplate.getCollection("zrbg").insert(dbo);
	}

}
