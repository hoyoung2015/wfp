package net.hoyoung.wfp.searcher;

import net.hoyoung.wfp.searcher.pageprocessor.impl.SearchPageProcessor;
import net.hoyoung.wfp.searcher.pipeline.impl.DataBasePipeline;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

public class HtmlDownloader{
	private Spider spider;
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
		spider.addPipeline(pipeline);
		spider.thread(5).run();
		//18500986197
		System.out.println("webmagic 爬虫启动......");
	}
}
