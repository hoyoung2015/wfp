package net.hoyoung.wfp.core;

import net.hoyoung.wfp.core.entity.User;
import net.hoyoung.wfp.core.service.CompanyInfoService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class App {

	public static void main(String[] args) {
		String[] config = {
				"classpath:spring-core.xml"
		};
		ApplicationContext context = new FileSystemXmlApplicationContext(config);
		CompanyInfoService companyInfoService = context.getBean(CompanyInfoService.class);
//		CompanyInfo c = new CompanyInfo();
//		c.setName("hehe");
//		companyInfoService.add(c);
		
		MongoTemplate mongo = context.getBean(MongoTemplate.class);
//		Map<String,Map<String,Object>> map = new HashMap<String, Map<String,Object>>();
//		Map<String,Object> map2 = new HashMap<String, Object>();
//		map2.put("name", "hoyoung");
//		map.put("user", map2);
//		User user = new User();
//		user.setName("fsdfsdfsdf---hoyoung");
//		mongo.insert(user);
		
//		DBCollection coll = mongo.getCollection("ceshi");
//		BasicDBObject dbo = new BasicDBObject("name", "fxn22==========---")
//			.append("age", 23)
//			.append("address", new BasicDBObject("street","xinglong").append("no", "9527"));
//		coll.insert(dbo);
		mongo.insert(new BasicDBObject("sdf","dfsdfdfssssssss"));
	}

}
