package net.hoyoung.wfp.searcher;

import java.util.ArrayList;
import java.util.List;

import net.hoyoung.wfp.core.entity.CompanyInfo;
import net.hoyoung.wfp.core.service.CompanyInfoService;
import net.hoyoung.wfp.searcher.utils.CompanyFileReaderUtil;
import net.hoyoung.wfp.searcher.utils.KeywordsFileReaderUtil;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App 
{
	private static String[] config = { "spring-core.xml" };
	private static ApplicationContext context;
	static {
		context = new FileSystemXmlApplicationContext(config);
	}
	public static void main(String[] args) {
		CompanyInfoService companyInfoService = context.getBean(CompanyInfoService.class);
		List<CompanyInfo> companyList = new ArrayList<CompanyInfo>();
		companyList = new CompanyFileReaderUtil("file/company.txt").read();
		for (CompanyInfo companyInfo : companyList) {
			companyInfo = companyInfoService.getByStockCode(companyInfo.getStockCode());
		}
		
		List<String> keywords = new KeywordsFileReaderUtil("file/keywords.txt").read();
		
		
		Searcher searcher = context.getBean(Searcher.class);
		searcher.addKeyword("武汉钢铁")
		.addKeywords(keywords)
		.run();
	}
}
