package net.hoyoung.wfp.stockdown;

import java.util.ArrayList;
import java.util.List;

import net.hoyoung.wfp.core.entity.CompanyInfo;
import net.hoyoung.wfp.core.utils.HibernateUtils;

import org.hibernate.Session;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.processor.PageProcessor;

public class TestPageProcessor{
	public static void main(String[] args) {
		List<SpiderListener> spiderListeners = new ArrayList<SpiderListener>();
		spiderListeners.add(new SpiderListener() {
			@Override
			public void onSuccess(Request request) {
				
			}
			@Override
			public void onError(Request request) {
				System.err.println(request);
			}
		});
		Spider spider = Spider.create(new PageProcessor(){
			private Site site = Site.me().setRetryTimes(5).setSleepTime(200);
			@Override
			public void process(Page page) {
				
			}
			@Override
			public Site getSite() {
				return site;
			}
		})
		.setSpiderListeners(spiderListeners)
		.thread(5).addUrl("www.jei.com.cn/");
//		
//		Session session = HibernateUtils.openSession();
//		List<CompanyInfo> list = session.createQuery("select new CompanyInfo(id,stockCode,webSite) from CompanyInfo").list();
//		for (CompanyInfo companyInfo : list) {
//			System.out.println(companyInfo.toString());
//		}
		
		spider.run();
	}
}
