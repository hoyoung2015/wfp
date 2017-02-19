package net.hoyoung.wfp.spider.test;

import net.hoyoung.wfp.spider.comweb.ComWebHttpClientDownloader;
import net.hoyoung.wfp.spider.comweb.ComWebProcessor;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

public class TestContentType {

	public static void main(String[] args) {
		String url = "http://dl.sungrowpower.com/index.php?s=/Home/File/download_file/id/285/name/%E9%98%B3%E5%85%89%E7%94%B5%E6%BA%90%E4%BC%81%E4%B8%9A%E7%94%BB%E5%86%8C.html";
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
