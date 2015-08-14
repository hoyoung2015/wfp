package net.hoyoung.wfp.searcher;

import net.hoyoung.wfp.core.dao.NewItemDao;
import net.hoyoung.wfp.searcher.pageprocessor.impl.SearchPageProcessor;
import net.hoyoung.wfp.searcher.pipeline.impl.DataBasePipeline;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.Spider;

@Component
public class HtmlDownloader implements ApplicationContextAware{
	private Spider spider;
	private ApplicationContext applicationContext;
	public HtmlDownloader() {
		spider = Spider.create(new SearchPageProcessor());
	}
	public void addUrl(String url){
		spider.addUrl(url);
	}
	public void run(){
		DataBasePipeline pipeline = new DataBasePipeline();
		pipeline.setNewItemDao(applicationContext.getBean(NewItemDao.class));
		spider.addPipeline(pipeline);
		spider.thread(5).run();
		System.out.println("webmagic 爬虫启动......");
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
}
