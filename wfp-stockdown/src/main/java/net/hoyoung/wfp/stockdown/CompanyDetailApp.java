package net.hoyoung.wfp.stockdown;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
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

import net.hoyoung.wfp.core.entity.CompanyInfo;
import net.hoyoung.wfp.stockdown.spider.CompanyInfoDetailSpiderPageProcessor;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

@PropertySource("classpath:application.properties")
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class CompanyDetailApp {

	Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	public static void main(String[] args) throws Exception {
		ApplicationContext ctx = SpringApplication.run(CompanyDetailApp.class, args);
		//获取页面解析器
		PageProcessor pageProcessor = ctx.getBean(CompanyInfoDetailSpiderPageProcessor.class);
		//获取mongodb客户端
		MongoTemplate mongoTemplate = ctx.getBean(MongoTemplate.class);
		/**
		 *  查询企业
		 *  因为这里是对基本信息的补充，基本信息中的name是null所以以此为条件
		 */
		List<CompanyInfo> list = mongoTemplate.find(new Query(new Criteria("webSite").is(null)), CompanyInfo.class);
		if(CollectionUtils.isEmpty(list)){
			System.exit(0);
		}
		System.out.println(list.size());
		//创建爬虫
		Spider spider = Spider.create(pageProcessor);
		//导入url
		for (CompanyInfo com : list) {
			Request req = new Request("http://stockdata.stock.hexun.com/gszl/s" + com.getStockCode() + ".shtml");
			req.putExtra("stock_code", com.getStockCode());
			spider.addRequest(req);
		}
		//开始抓取
		long start = System.currentTimeMillis();
		spider.thread(3).run();
		System.out.println("耗时:" + (System.currentTimeMillis() - start) / 1000 + "秒");
	}

}
