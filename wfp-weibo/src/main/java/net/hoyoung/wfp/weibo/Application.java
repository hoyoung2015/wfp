package net.hoyoung.wfp.weibo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import net.hoyoung.wfp.weibo.entity.User;

@Configuration  
@ComponentScan  
@EnableAutoConfiguration
public class Application {

	public static void main(String[] args) {

		ApplicationContext ctx = SpringApplication.run(Application.class, args);
		User wc1 = ctx.getBean(User.class);
		User wc2 = ctx.getBean(User.class);
		System.out.println(">>>>>>>>"+(wc1==wc2));
		/*
		MongoDbFactory mongo = ctx.getBean(MongoDbFactory.class);
		DB db = mongo.getDb("wfp");

		DBCollection collec = db.getCollection("wfp");
		String json = "{\"name\":\"lucy\",\"age\":18}";
		Map<String, Object> map = JSON.parseObject(json);
		collec.insert(new BasicDBObject(map));
		*/
	}

}
