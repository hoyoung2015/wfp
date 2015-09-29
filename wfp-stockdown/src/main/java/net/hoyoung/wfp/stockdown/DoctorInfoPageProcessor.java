package net.hoyoung.wfp.stockdown;

import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

/**
 * Hello world!
 *
 */
public class DoctorInfoPageProcessor implements PageProcessor {
	public void process(Page page) {
		System.err.println(page.getHtml().xpath("/html/body/div[@class='wraper']").get());
		
		if(page.getRequest().getUrl().equals("http://www.chunyuyisheng.com/clinics")){
			List<Selectable> nodes = page.getHtml().xpath("/html/body/div[@class='wraper']").nodes();
			for (Selectable sele : nodes) {
				System.err.println(sele.get());
			}
		}else{
			
		}
	}

	public static void main(String[] args) {
		Spider.create(new DoctorInfoPageProcessor())
		.addUrl("http://www.chunyuyisheng.com/clinics")
		.thread(2)
		.run();
	}

	public Site getSite() {
		return site;
	}

	private Site site = Site.me().setRetryTimes(5).setSleepTime(200);
}
