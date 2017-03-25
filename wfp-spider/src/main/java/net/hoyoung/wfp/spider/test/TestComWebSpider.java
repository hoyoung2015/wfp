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
import us.codecraft.webmagic.proxy.ComWebProxyPool;

public class TestComWebSpider {

	static class MyPipeline implements Pipeline {
		private AtomicInteger count = new AtomicInteger(0);

		@Override
		public void process(ResultItems resultItems, Task task) {

			List<Document> list = resultItems.get(ComWebConstant.URL_LIST_KEY);

			if (CollectionUtils.isEmpty(list)) {
				return;
			}
			for (Document document : list) {
				System.out.println(count.incrementAndGet() + "\t" + document);
			}
		}

	}

	public static void main(String[] args) {
		// Request request = new
		// Request("http://www.hoyoung.net/2017/02/10/squid3-proxy/");
//		 Request request = new Request("http://www.jonjee.com/admin/webfiles/magazine/151_2.pdf"); //download
//		 Request request = new
//		 Request("http://www.chinadmegc.com/ebook_download.php?46");
//		 Request request = new
//		 Request("http://www.chinadmegc.com/ebook_download.php?48");//big
		// Request request = new
		// Request("http://www.nhwa-group.com/sitefiles/services/cms/utils.aspx?type=Download&publishmentSystemID=4&channelID=72&contentID=461");
		// Request request = new
		// Request("http://www.kingyork.biz/pages/{$MODULE[guestbook][url]}post.php'");
		// Request request = new
		// Request("http://www.600795.com.cn/publish/main/25/27/20150116104829606723809/kzzzp_xxpl_05");//download
		// Request request = new
		// Request("http://www.loncinindustries.com/Group/NewsDetails.aspx?catid=7|68|80&id=2146794229");//url包含竖线
		// Request request = new
		// Request("http://www.jmc.com.cn/jmc/home/file/download/id/39.html");//download
		// fuck
		// Request request = new
		// Request("http://www.nhwa-group.com/sitefiles/services/cms/utils.aspx?type=Download&publishmentSystemID=4&channelID=72&contentID=457");//download
//		 Request request = new
//		 Request("http://www.salubris.cn/ch/news_detail.asp?typeid=2&typename=&id=286&name=信立泰携手中国心血管健康联盟共同打造ACS诊疗、预防、随访为一体的全程关爱项目");//urlencode
//		Request request = new Request("http://www.sanju.cn/Home/Index/downFiles/newsid/395.html");// download 反射修改content-encoding
//		Request request = new Request("http://www.hybio.com.cn/ajax/file/id/353.php");//
//		Request request = new Request("http://www.loncinindustries.com/motocycle/TopicActive.aspx?catid=9-706-463554675-1374275421");//
		Request request = new Request("http://www.ceepower.com/");//

		request.putExtra(ComPage.STOCK_CODE, "111111");
		request.putExtra("domain", "ceepower.com");
		ComWebProcessor processor = new ComWebProcessor();
		
//		processor.getSite().setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
		
//		processor.getSite().setHttpProxyPool(new ComWebProxyPool(ProxyReader.read(), false));
		Spider spider = Spider.create(processor)
				.setDownloader(new ComWebHttpClientDownloader())
				.addRequest(request)
				.addPipeline(new MyPipeline()).thread(1);
		ComWebSpiderListener spiderListener = new ComWebSpiderListener(spider);
		spider.setSpiderListeners(Lists.newArrayList(spiderListener)).run();
	}

}
