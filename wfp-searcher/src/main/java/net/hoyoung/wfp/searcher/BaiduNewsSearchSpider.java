package net.hoyoung.wfp.searcher;

import java.util.ArrayList;
import java.util.List;

import net.hoyoung.wfp.core.entity.CompanyInfo;
import net.hoyoung.wfp.core.service.CompanyInfoService;
import net.hoyoung.wfp.searcher.utils.CompanyFileReaderUtil;
import net.hoyoung.wfp.searcher.utils.KeywordsFileReaderUtil;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class BaiduNewsSearchSpider 
{
	private static String[] config = { "classpath*:spring-core.xml" };
	private static ApplicationContext context;
	static {
		context = new ClassPathXmlApplicationContext(config);
	}
	public static void main(String[] args) {
		CompanyInfoService companyInfoService = context.getBean(CompanyInfoService.class);
		List<String> stockCodeList = new ArrayList<String>();
		
		Searcher searcher = context.getBean(Searcher.class);
		
		//读取关键词列表
		List<String> keywords = new KeywordsFileReaderUtil("file/keywords.txt").read();
		//读取股票号
		stockCodeList = new CompanyFileReaderUtil("file/company.txt").read();
		//根据股票号从数据库中读取企业信息
		for (String stockCode : stockCodeList) {//遍历待搜索企业
			CompanyInfo companyInfo = companyInfoService.getByStockCode(stockCode);
			if(companyInfo!=null){
				for (String keyword : keywords) {//遍历关键词
					SearchRequest sr = new SearchRequest();
					sr.putExtra("company", companyInfo);
					sr.addKeyword(keyword)
					.addKeyword(companyInfo.getSname());
					
					searcher.setSearchRequest(sr);
					searcher.run();
				}
			}
		}
		searcher.close();
	}
}
