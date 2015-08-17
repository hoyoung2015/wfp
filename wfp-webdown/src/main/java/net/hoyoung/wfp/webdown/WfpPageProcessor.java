package net.hoyoung.wfp.webdown;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class WfpPageProcessor implements PageProcessor {
	private Logger logger = Logger.getLogger(getClass());
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
			".exe",
			".tar",
			".tar.gz",
			".xls"
	};
	@Override
	public void process(Page page) {
		//生成正则
		String url = page.getRequest().getUrl().replaceAll("^http://www\\.", "");
		int endIndex = url.indexOf("/");
		if(endIndex==-1){
			endIndex = url.length();
		}
		url = url.substring(0,endIndex);
		/*String regx = ".*"+url+".*";
		List<String> targetUrls = page.getHtml().links().regex(regx).all();
		for (String string : targetUrls) {
			logger.error(">>>>>>>>>>>>>>>>"+string);
		}*/
		List<String> targetUrls = page.getHtml().links().all();
		Iterator<String> ite = targetUrls.iterator();
		while(ite.hasNext()){
			String str = ite.next();
			
			if(!str.contains(url) || str.contains("@")){
				ite.remove();
				continue;
			}
			for (String ext : EXCEPT_EXTS) {
				if(str.toLowerCase().endsWith(ext)){
					ite.remove();
					break;
				}
			}
			logger.error(">>>>>>>>>>>>>>>>"+str);
		}//while
		page.addTargetRequests(targetUrls);//必须是站内的
		page.putField("html", page.getHtml());
	}
	@Override
	public Site getSite() {
		return site;
	}
}
