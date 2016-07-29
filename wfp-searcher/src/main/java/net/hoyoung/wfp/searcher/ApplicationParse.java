package net.hoyoung.wfp.searcher;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import net.hoyoung.wfp.searcher.parse.ContentParser;
import net.hoyoung.wfp.searcher.vo.NewItem;

/**
 * 将获取到的新闻url下载并解析
 * 
 * @author v_huyang01
 *
 */
@PropertySource("classpath:application.properties")
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class ApplicationParse {

	static Logger logger = LoggerFactory.getLogger(ApplicationParse.class);

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	public static void main(String[] args) throws Exception {

		ApplicationContext ctx = SpringApplication.run(Application.class, args);
		MongoTemplate mongoTemplate = ctx.getBean(MongoTemplate.class);
		ContentParser contentParser = ctx.getBean(ContentParser.class);
		// 查询没有解析的条目
		List<NewItem> list = mongoTemplate.find(new Query().addCriteria(new Criteria("content").is(null)),
				NewItem.class);
		System.out.println("需要解析"+list.size()+"个文档");
		for (NewItem newItem : list) {
			CrawlDatum datum = new CrawlDatum(newItem.getTargetUrl()).meta("stockCode", newItem.getStockCode())
					.setKey(newItem.getStockCode()+newItem.getTargetUrl());
			logger.info(datum.getKey());
			contentParser.addSeed(datum);
//			break;
		}
		contentParser.setThreads(3);
		contentParser.start(99999999);
	}
}
