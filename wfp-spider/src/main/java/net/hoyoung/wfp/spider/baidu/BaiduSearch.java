package net.hoyoung.wfp.spider.baidu;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHost;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;

public class BaiduSearch implements PageProcessor {
	
	private Pattern pnPattern = Pattern.compile("&pn=(\\d+)&");

	private String findpn(String url){
		Matcher matcher = pnPattern.matcher(url);
		if(matcher.find()){
			return matcher.group(1);
		}
		return null;
	}
	
	@Override
	public void process(Page page) {
		List<String> links = page.getHtml().$("#page > a").links().all();
		for (String link : links) {
			System.out.println(link);
		}
//		page.addTargetRequests(links);
		
		String format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
		
		try {
			FileUtils.writeStringToFile(new File(format+"-"+findpn(page.getRequest().getUrl())+".html"), page.getRawText(), "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Site site = Site.me().setSleepTime(2000)
			.setRetryTimes(3)
			.setTimeOut(60000)
			.addHeader("User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36")
			.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
			.addHeader("Accept-Language", "zh-CN,zh;q=0.8")
			.addHeader("Accept-Encoding", "gzip, deflate, sdch, br")
			.addHeader("Cache-Control", "max-age=0")
			.addHeader("Upgrade-Insecure-Requests", "1");

	@Override
	public Site getSite() {
		return site;
	}
	
	public static void main(String[] args) {
		
//		RedisProxyPool proxyPool = null;
//		try {
//			proxyPool = new RedisProxyPool();
//		} catch (Exception e1) {
//			e1.printStackTrace();
//			System.err.println("init proxypool error");
//			System.exit(-1);
//		}
		
		BaiduSearch baiduSearch = new BaiduSearch();
		
//		baiduSearch.getSite().setHttpProxyPool(proxyPool);
		try {
			baiduSearch.getSite().setHttpProxy(new HttpHost(InetAddress.getByName("180.243.235.40"), 8080));
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		DuplicateRemovedScheduler scheduler = new QueueScheduler().setDuplicateRemover(new HashSetDuplicateRemover(){

			@Override
			protected String getUrl(Request request) {
				String url = request.getUrl();
				url = url.substring(0, url.indexOf("ie=utf-8"));
				System.err.println(url);
				return url;
			}
		});
		Spider spider = Spider.create(baiduSearch).thread(1)
				.setScheduler(scheduler);
//		String wd = "site:hoyoung.net";
		String wd = "+污染 site:wisco.com.cn";
//		String wd = "site:wisco.com.cn";
		
		try {
			wd = URLEncoder.encode(wd, "utf-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		spider.addUrl("https://www.baidu.com/s?wd="+wd+"&pn=0&oq="+wd+"&ie=utf-8&rsv_idx=1&tn=SE_INTER1");
		spider.run();
	}

}