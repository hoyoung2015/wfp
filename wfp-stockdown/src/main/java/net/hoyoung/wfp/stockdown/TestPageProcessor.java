package net.hoyoung.wfp.stockdown;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.hoyoung.webmagic.downloader.HttpClientDownloader;
import net.hoyoung.wfp.core.entity.CompanyInfo;
import net.hoyoung.wfp.core.entity.TagMeta;
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
				System.out.println(request);
				Session session = HibernateUtils.openSession();
				//不通，删除
				org.hibernate.Transaction tx = session.beginTransaction(); //保证是同一个事物
				CompanyInfo c = new CompanyInfo();
				c.setId((int)(request.getExtra("cid")));
				c.setStockCode((String) request.getExtra("stockCode"));
				session.delete(c);
				//记录删除项
				if((Integer)request.getExtra("statusCode")==500){
					session.save(new TagMeta("500_error_url_"+c.getStockCode(), request.getUrl(), new Date()));
				}else{
					session.save(new TagMeta("error_url_"+c.getStockCode(), request.getUrl(), new Date()));
				}
				
				tx.commit();
				System.err.println(request);
				System.err.println(c.getStockCode()+" 不通");
			}
		});
		Spider spider = Spider.create(new PageProcessor(){
			private Site site = Site.me().setRetryTimes(3).setSleepTime(200).setTimeOut(60000).setUseGzip(false);
			@Override
			public void process(Page page) {
				System.out.println(page.getRequest().getUrl()+"完成");
			}
			@Override
			public Site getSite() {
				return site;
			}
		})
		.setDownloader(new HttpClientDownloader())
		.setSpiderListeners(spiderListeners)
		.thread(15);
		
		/*Request req = new Request("http://www.chinabird.com");
		req.putExtra("cid", 0);//用来记录所属企业，如果连不通，则将该记录删除
		req.putExtra("stockCode", "600130");
		spider.addRequest(req);*/
		
		Session session = HibernateUtils.openSession();
		List<CompanyInfo> list = session.createQuery("select new CompanyInfo(id,stockCode,webSite) from CompanyInfo").list();
		for (CompanyInfo companyInfo : list) {
			Request req = new Request(companyInfo.getWebSite());
			req.putExtra("cid", companyInfo.getId());//用来记录所属企业，如果连不通，则将该记录删除
			req.putExtra("stockCode", companyInfo.getStockCode());
			spider.addRequest(req);
		}
		spider.run();
		
		System.out.println("-------------------over----------------------");
	}
}
