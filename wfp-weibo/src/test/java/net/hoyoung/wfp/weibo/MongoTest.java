package net.hoyoung.wfp.weibo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;

import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class MongoTest extends BaseTest {
	@Autowired
	MongoDbFactory mongo;

	@Test
	public void test2() throws IOException {
		String json = FileUtils.readFileToString(new File("page_29.json"));
		System.out.println(json);
		String listStr = JSONPath.compile("$.cards[0].card_group").eval(JSON.parseObject(json)).toString();
		List<HashMap> maps = JSON.parseArray(listStr, HashMap.class);
		List<DBObject> dbobjects = Lists.transform(maps, new Function<HashMap, DBObject>() {
			@Override
			public DBObject apply(HashMap map) {
				return new BasicDBObject(map);
			}
		});
		 DB db = mongo.getDb("wfp");
		 DBCollection col = db.getCollection("test");
		 col.insert(dbobjects);
	}
}
