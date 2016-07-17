package net.hoyoung.wfp.weibo.spring;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configurable
@PropertySource("classpath:mongodb.properties")
public class ApplicationConfig {

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	/*
	@Autowired
	private Environment env;

	@Bean
	public MongoClient mongoClient() throws UnknownHostException{
		MongoClient mongoClient = new MongoClient(env.getProperty("mongo.host"), env.getProperty("mongo.port", Integer.class));
		return mongoClient;
	}*/
}
