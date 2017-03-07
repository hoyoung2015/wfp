package net.hoyoung.wfp.spider.test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;

import com.google.common.collect.Lists;

import net.hoyoung.wfp.core.utils.ProxyReader;
import net.hoyoung.wfp.spider.comweb.ComWebConstant;
import net.hoyoung.wfp.spider.comweb.ComWebHttpClientDownloader;
import net.hoyoung.wfp.spider.comweb.ComWebProcessor;
import net.hoyoung.wfp.spider.comweb.ComWebSpiderListener;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class TestComWebSpider {

	
	static class MyPipeline implements Pipeline {
		private AtomicInteger count = new AtomicInteger(0);
		@Override
		public void process(ResultItems resultItems, Task task) {
			
			List<Document> list = resultItems.get(ComWebConstant.URL_LIST_KEY);

			if (CollectionUtils.isEmpty(list)) {
				return;
			}
			for (int i=0;i<list.size();i++){
				System.out.println(count.incrementAndGet());
			}
		}

	}

	public static void main(String[] args) {
		Request request = new Request("http://www.ntfan.com");
		request.putExtra(ComPage.STOCK_CODE, "111111");
		request.putExtra("domain", "ntfan.com");
		ComWebProcessor processor = new ComWebProcessor();
		 processor.getSite().setHttpProxyPool(ProxyReader.read(), false);
		 processor.getSite().addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1");
		 Spider spider = Spider.create(processor).setDownloader(new ComWebHttpClientDownloader()).addRequest(request).addPipeline(new MyPipeline()).thread(1);
		ComWebSpiderListener spiderListener = new ComWebSpiderListener(spider);
		spider.setSpiderListeners(Lists.newArrayList(spiderListener)).run();
	}

}
