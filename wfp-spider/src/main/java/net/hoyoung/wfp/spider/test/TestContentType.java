package net.hoyoung.wfp.spider.test;

import net.hoyoung.wfp.spider.comweb.ComWebHttpClientDownloader;
import net.hoyoung.wfp.spider.comweb.ComWebProcessor;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

public class TestContentType {

	public static void main(String[] args) {
		String url = "http://dl.sungrowpower.com/index.php?s=/Home/File/download_file/id/520/name/SG5KTL-D%E4%BA%A7%E5%93%81%E4%BB%8B%E7%BB%8D.html";
//		String url = "http://www.hoyoung.net/";
		Request req = new Request(url).putExtra(ComPage.STOCK_CODE, "999999")
		.putExtra("domain", "sungrowpower.com");
		Spider.create(new ComWebProcessor())
		.setDownloader(new ComWebHttpClientDownloader())
		.addRequest(req)
		.thread(1)
		.run();
	}
}
