package net.hoyoung.wfp.searcher;

import java.util.ArrayList;
import java.util.List;

import net.hoyoung.wfp.core.entity.CompanyInfo;
import net.hoyoung.wfp.core.service.CompanyInfoService;
import net.hoyoung.wfp.core.utils.HibernateUtils;
import net.hoyoung.wfp.searcher.savehandler.SaveHandler;
import net.hoyoung.wfp.searcher.savehandler.impl.DbSaveHandler;
import net.hoyoung.wfp.searcher.utils.CompanyFileReaderUtil;
import net.hoyoung.wfp.searcher.utils.KeywordsFileReaderUtil;

import net.hoyoung.wfp.searcher.vo.NewItem;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class BaiduNewsSearchSpider 
{
	static Logger logger = LoggerFactory.getLogger(BaiduNewsSearchSpider.class);
	public static void main(String[] args) {
		List<String> stockCodeList = new ArrayList<String>();
		HtmlDownloader downloader = new HtmlDownloader();
		SaveHandler saveHandler = new DbSaveHandler();
		Searcher searcher = new Searcher(saveHandler,downloader);
		
		//读取关键词列表
		List<String> keywords = new KeywordsFileReaderUtil("/home/hoyoung/workspace/Intellij/wfp/wfp-searcher/data/keywords.txt").read();
		logger.info("读取"+keywords.size()+"个关键词");
		for (String keyword : keywords){
			logger.info(keyword);
		}
		System.out.println();
		//读取股票号
		stockCodeList = new CompanyFileReaderUtil("/home/hoyoung/workspace/Intellij/wfp/wfp-searcher/data/company.txt").read();
		logger.info("读取"+stockCodeList.size()+"个股票号");
		for (String code : stockCodeList){
			logger.info(code);
		}


		//根据股票号从数据库中读取企业信息
		for (String stockCode : stockCodeList) {//遍历待搜索企业
			Session session = HibernateUtils.getCurrentSession();
			session.beginTransaction();
			CompanyInfo companyInfo = (CompanyInfo) session.get(CompanyInfo.class, stockCode);
			session.getTransaction().commit();
			logger.info(companyInfo.toString());
			if(companyInfo!=null){
				for (String keyword : keywords) {//遍历关键词
					SearchRequest sr = new SearchRequest();
					sr.putExtra("company", companyInfo);
					sr.putExtra("keyword",keyword);
					sr.addKeyword("+"+keyword)//加号表示这个关键词一定出现
							.addKeyword("\"" + companyInfo.getSname() + "\"");//加上双引号避免被分词
					/**
					 * 校验股票号+关键词是否已经搜索
					 */
					session = HibernateUtils.getCurrentSession();
					session.beginTransaction();
					Long count = (Long)session.createCriteria(NewItem.class)
							.setProjection(Projections.rowCount())
							.add(Restrictions.eq("stockCode", companyInfo.getStockCode()))
							.add(Restrictions.eq("keyword", keyword))
							.uniqueResult();
					session.getTransaction().commit();
					if(count>0){
						logger.info(sr.getQuery()+" 已经采集过");
						continue;
					}

					
					searcher.setSearchRequest(sr);

					searcher.run();
				}
			}
		}
		searcher.close();
	}
}
