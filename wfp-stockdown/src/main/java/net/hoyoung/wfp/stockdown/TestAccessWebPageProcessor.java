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
/**
 * 探测网址是否可访问
 * @author hoyoung
 *不可访问的网址对应的记录被删除，同时删除的记录存储在tag_meta的数据表中
 */
public class TestAccessWebPageProcessor{
	public static void main(String[] args) {
		List<SpiderListener> spiderListeners = new ArrayList<SpiderListener>();
		spiderListeners.add(new SpiderListener() {
			
			@Override
			public void onSuccess(Request request) {
				
			}
			@Override
			public void onError(Request request) {

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
//			req.putExtra("cid", companyInfo.getId());//用来记录所属企业，如果连不通，则将该记录删除
			req.putExtra("stockCode", companyInfo.getStockCode());
			spider.addRequest(req);
		}
		spider.run();
		
		System.out.println("-------------------over----------------------");
	}
}
