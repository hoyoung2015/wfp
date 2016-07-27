package net.hoyoung.wfp.weibo;

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

@PropertySource("classpath:config.properties")
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {

	Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	public static void main(String[] args) throws Exception {

		ApplicationContext ctx = SpringApplication.run(Application.class, args);
		CrawlService crawlService = ctx.getBean(CrawlService.class);
		crawlService.startJob("110113",
				"http://m.weibo.cn/page/json?containerid=1005051746221281_-_WEIBO_SECOND_PROFILE_WEIBO");
	}

}
