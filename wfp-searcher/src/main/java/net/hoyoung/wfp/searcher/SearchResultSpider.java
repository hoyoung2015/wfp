package net.hoyoung.wfp.searcher;

import java.util.List;

import net.hoyoung.wfp.core.entity.NewItem;
import net.hoyoung.wfp.core.service.NewItemService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import us.codecraft.webmagic.Request;

public class SearchResultSpider {
	private static String[] config = { "classpath*:spring-core.xml" };
	private static ApplicationContext context;
	static {
		context = new ClassPathXmlApplicationContext(config);
	}
	public static void main(String[] args) {
		NewItemService newItemService = context.getBean(NewItemService.class);
		HtmlDownloader htmlDownloader = context.getBean(HtmlDownloader.class);
		List<NewItem> newItems = newItemService.findAll();
		for (NewItem newItem : newItems) {
			if(newItem.getTargetUrl()==null){
				continue;
			}
			Request request = new Request();
			request.setUrl(newItem.getTargetUrl());
			request.putExtra("stockCode", newItem.getStockCode());
			htmlDownloader.addRequest(request);
		}
		htmlDownloader.run();
	}
}