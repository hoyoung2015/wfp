package net.hoyoung.wfp.webdown;

import java.util.Iterator;
import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class WfpPageProcessor implements PageProcessor {
	private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
	private static String[] EXCEPT_EXTS = {
			".jpg",
			".js",
			".jpeg",
			".gif",
			".css",
			".png",
			".pdf",
			".zip",
			".rar",
			".doc",
			".docx",
			".ppt",
			".pptx",
			".swf",
			".xls"
	};
	@Override
	public void process(Page page) {
//		System.err.println(page.getHtml());
		//生成正则
		String url = page.getRequest().getUrl().replace("http://", "").replace("www.", "");
		url = url.substring(0, url.indexOf("/"));
//		System.err.println(url);
		String regx = ".*"+url+".*";
		List<String> targetUrls = page.getHtml().links().regex(regx).all();
		Iterator<String> ite = targetUrls.iterator();
		while(ite.hasNext()){
			String str = ite.next();
			System.out.println("str="+str);
			for (String ext : EXCEPT_EXTS) {
				System.out.println("ext="+ext);
				if(str.toLowerCase().endsWith(ext)){
					ite.remove();
					break;
				}
			}
		}
		page.addTargetRequests(targetUrls);//必须是站内的
		page.putField("html", page.getHtml());
	}
	@Override
	public Site getSite() {
		return site;
	}
}
