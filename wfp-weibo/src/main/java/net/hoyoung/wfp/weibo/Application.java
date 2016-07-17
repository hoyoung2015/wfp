package net.hoyoung.wfp.weibo;

import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.MongoDbFactory;

import com.alibaba.fastjson.JSON;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

@SpringBootApplication(scanBasePackages = { "net.hoyoung.wfp.weibo" })
public class Application {

	public static void main(String[] args) {

		ApplicationContext ctx = SpringApplication.run(Application.class, args);
		MongoDbFactory mongo = ctx.getBean(MongoDbFactory.class);
		DB db = mongo.getDb("wfp");

		DBCollection collec = db.getCollection("wfp");
		String json = "{\"name\":\"lucy\",\"age\":18}";
		Map<String, Object> map = JSON.parseObject(json);
		collec.insert(new BasicDBObject(map));
	}

}
