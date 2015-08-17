package net.hoyoung.wfp.searcher;

import net.hoyoung.wfp.core.service.NewItemService;
import net.hoyoung.wfp.searcher.pageprocessor.impl.SearchPageProcessor;
import net.hoyoung.wfp.searcher.pipeline.impl.DataBasePipeline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

@Component
public class HtmlDownloader{
	private Spider spider;
	@Autowired
	private NewItemService newItemService;
	public HtmlDownloader() {
		spider = Spider.create(new SearchPageProcessor());
	}
	public void addUrl(String url){
		spider.addUrl(url);
	}
	public void addRequest(Request request){
		spider.addRequest(request);
	}
	public void run(){
		DataBasePipeline pipeline = new DataBasePipeline();
		pipeline.setNewItemService(newItemService);
		spider.addPipeline(pipeline);
		spider.thread(5).run();
		System.out.println("webmagic 爬虫启动......");
	}
}
