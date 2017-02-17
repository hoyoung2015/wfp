package net.hoyoung.wfp.spider.test;

import java.io.IOException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;

import net.hoyoung.wfp.spider.comweb.ComWebConstant;
import net.hoyoung.wfp.spider.comweb.ComWebHttpClientDownloader;
import net.hoyoung.wfp.spider.comweb.ComWebProcessor;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;
import net.hoyoung.wfp.spider.util.ProxyReader;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.UrlUtils;

public class TestComWebSpider {

	static class MyPipeline implements Pipeline {

		@Override
		public void process(ResultItems resultItems, Task task) {
			List<Document> list = resultItems.get(ComWebConstant.URL_LIST_KEY);

			if (CollectionUtils.isEmpty(list)) {
				return;
			}
			for (Document document : list) {
				
			}
		}

	}

	public static void main(String[] args) {
		Request request = new Request("http://www.huataipaper.com/");
		request.putExtra(ComPage.STOCK_CODE, "111111");
		request.putExtra("domain", UrlUtils.getDomain(request.getUrl()).replaceAll("^www\\.", ""));
		ComWebProcessor processor = new ComWebProcessor();
		processor.getSite().setSleepTime(100);
		List<String[]> proxies = null;
		try {
			proxies = ProxyReader.read("proxy.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		 processor.getSite().setHttpProxyPool(proxies, true);
		Spider.create(processor).setDownloader(new ComWebHttpClientDownloader()).addRequest(request).addPipeline(new MyPipeline()).thread(1).run();
	}

}
