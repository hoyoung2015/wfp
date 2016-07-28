package net.hoyoung.wfp.searcher;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

import net.hoyoung.wfp.searcher.baidu.BaiduNewsSpider;

@PropertySource("classpath:application.properties")
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {

	static Logger logger = LoggerFactory.getLogger(Application.class);

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	public static void main(String[] args) throws Exception {
		if (args == null || args.length < 2) {
			logger.warn("正确参数为: <企业股票号文本文件> <关键词文本文件>");
			System.exit(1);
		}
		ApplicationContext ctx = SpringApplication.run(Application.class, args);
		BaiduNewsSpider baiduNewsSpider = ctx.getBean(BaiduNewsSpider.class);
		// 读取关键词列表
		List<String> keywords = readListString(args[1]);
		logger.info("读取" + keywords.size() + "个关键词");
		// 读取股票号
		List<String> stockCodeList = readListString(args[0]);
		logger.info("读取" + stockCodeList.size() + "个股票号");

		// 根据股票号从数据库中读取企业信息
		for (String stockCode : stockCodeList) {// 遍历待搜索企业
			for (String keyword : keywords) {// 遍历关键词
				baiduNewsSpider.fetch(stockCode, keyword);
			}
		}

	}

	public static List<String> readListString(String filePath) {
		List<String> list = new ArrayList<String>();

		try {
			FileInputStream fs = new FileInputStream(filePath);
			InputStreamReader isr = new InputStreamReader(fs, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String temp = null;

			while ((temp = br.readLine()) != null) {
				list.add(temp.replace(" ", ""));
			}
			

			fs.close();
			isr.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
