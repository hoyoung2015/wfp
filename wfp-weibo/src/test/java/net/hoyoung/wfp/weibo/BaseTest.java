package net.hoyoung.wfp.weibo;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.alibaba.fastjson.JSON;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class, loader = AnnotationConfigContextLoader.class)
public class BaseTest {

	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	private Environment env;
	
	@Autowired
	MongoDbFactory mongo;
	
	@Test
	public void test() {
		DB db = mongo.getDb(env.getProperty("mongo.dbname"));
		
		DBCollection collec = db.getCollection("wfp");
		String json = "{\"name\":\"lucy\",\"age\":18}";
		Map<String,Object> map = JSON.parseObject(json);
		collec.insert(new BasicDBObject(map));
	}
}
