package net.hoyoung.wfp.searcher.savehandler.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.hoyoung.wfp.core.entity.CompanyInfo;
import net.hoyoung.wfp.core.utils.HibernateUtils;
import net.hoyoung.wfp.searcher.SearchRequest;
import net.hoyoung.wfp.searcher.savehandler.SaveHandler;

import net.hoyoung.wfp.searcher.vo.NewItem;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
public class DbSaveHandler implements SaveHandler {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Override
	public void save(SearchRequest searchRequest) {
		Html resultHtml = searchRequest.getHtml();
		List<NewItem> list = new ArrayList<NewItem>();
		//开线程来保存
		Selectable pageSele = resultHtml.xpath("//*[@id=\"page\"]/strong/span[2]/text()");
		if(pageSele.get()!=null){
			logger.info("当前页码:"+pageSele);

		}else {
			logger.info("这里待定。。。。。。。。。。。。。。。。");
		}
		Selectable selectable = resultHtml.xpath("//div[@class='result']");
		List<Selectable> nodes = selectable.nodes();
		Session session = HibernateUtils.getCurrentSession();
		session.beginTransaction();
		logger.info("获取"+nodes.size()+"条新闻");
		for (Selectable sele : nodes) {
			Selectable titleSele = sele.xpath("//h3[@class='c-title']/a");
			String title = titleSele.get().replaceAll("(</?em[^>]*>)|(</?a[^>]*>)", "");

			String targetUrl = titleSele.xpath("//*/@href").get();
			Selectable sumSele = sele.xpath("//div[@class='c-summary']");
			String sourceAndDate = sumSele.xpath("//p/text()").get();
			
			String[] ss = sourceAndDate.split("\\u00A0\\u00A0");
			String sourceName = ss[0];
			logger.info(sourceName+":"+title);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
			Date publishDate = null;
			NewItem newItem = new NewItem();
			if(ss!=null&&ss.length>1){
				try {
					publishDate = sdf.parse(ss[1]);
					newItem.setPublishDate(publishDate);
				} catch (ParseException e) {
					logger.info(e.getMessage());
					logger.info("解析发布时间出错，存储原始值");
					newItem.setPublishDateStr(ss[1]);
				}
			}

			String summary = sumSele.get().replaceAll("(<span.*/span>)|(<p.*/p>)|(</?div[^>]*>)|(</?em[^>]*>)", "");

			newItem.setCreateDate(new Date());

			newItem.setSourceName(sourceName);
			newItem.setTargetUrl(targetUrl);
			newItem.setTitle(title);
			newItem.setSummary(summary);

			newItem.setKeyword((String) searchRequest.getExtra("keyword"));

			CompanyInfo companyInfo = (CompanyInfo) searchRequest.getExtra("company");
			
			newItem.setStockCode(companyInfo.getStockCode());
			newItem.setQuery(searchRequest.getQuery());

			//
			/**
			 * 检查是否重复
			 * 1.stock_code一样
			 * 2.target_url一样
			 */
			Long count = (Long)session.createCriteria(NewItem.class)
					.setProjection(Projections.rowCount())
					.add(Restrictions.eq("stockCode", newItem.getStockCode()))
					.add(Restrictions.eq("targetUrl", newItem.getTargetUrl()))
					.uniqueResult();
			if(count>0){
				logger.info(newItem+"已存在");
				continue;
			}
			session.save(newItem);
			list.add(newItem);
		}
		session.getTransaction().commit();
	}

}