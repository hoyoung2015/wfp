package net.hoyoung.wfp.stockdown;

import net.hoyoung.wfp.core.entity.CompanyInfo;
import net.hoyoung.wfp.core.service.CompanyInfoService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	String[] config = {
				"classpath:spring-core.xml"
		};
		ApplicationContext context = new FileSystemXmlApplicationContext(config);
		CompanyInfoService companyInfoService = context.getBean(CompanyInfoService.class);
		CompanyInfo c = new CompanyInfo();
		c.setName("hehe2");
		companyInfoService.add(c);
    }
}
