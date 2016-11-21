package net.hoyoung.wfp.stockdown;

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

import net.hoyoung.wfp.stockdown.spider.CompanyInfoPageProcessor;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;

@PropertySource("classpath:application.properties")
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class CompanyInfoApp {

	Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	public static void main(String[] args) throws Exception {
		ApplicationContext ctx = SpringApplication.run(CompanyInfoApp.class, args);
		CompanyInfoPageProcessor pageProcessor = ctx.getBean(CompanyInfoPageProcessor.class);
		long start = System.currentTimeMillis();
		Spider spider = Spider.create(pageProcessor);
		for (int i = CompanyInfoPageProcessor.PAGE_START; i <= CompanyInfoPageProcessor.PAGE_END; i++) {
			spider.addUrl(CompanyInfoPageProcessor.JSON_LIST_URL + i);
		}
		spider.thread(1).run();
		System.out.println("耗时:" + (System.currentTimeMillis() - start) / 1000 + "秒");
	}

}
