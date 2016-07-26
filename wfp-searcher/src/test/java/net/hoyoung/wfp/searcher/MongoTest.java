package net.hoyoung.wfp.searcher;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;

public class MongoTest extends BaseTest {

	@Autowired
	MongoTemplate mongoTemplate;
	@Test
	public void test(){
		System.out.println("hello");
		mongoTemplate.insert(new User("hoyoung"));
	}
	@Document(collection="news")
	static class User{
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public User(String name) {
			super();
			this.name = name;
		}
		
	}
}
