package net.hoyoung.patents;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import net.hoyoung.wfp.core.service.CompanyPatentsService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 绿色专利过滤器
 * @author hoyoung
 *
 */
public class GreenPatentsFilter {
	private static String[] config = { "classpath:spring-core.xml" };
	public static ApplicationContext APP_CONTEXT;
	static{
		APP_CONTEXT = new FileSystemXmlApplicationContext(config);
	}
	public static void main(String[] args) {
		
		CompanyPatentsService companyPatentsService = APP_CONTEXT.getBean(CompanyPatentsService.class);
		
		
		
		ObjectMapper om = new ObjectMapper();
		Map<String,String> patentsMap = null;
		try {
			patentsMap = om.readValue(new File("file/patents.json"), new TypeReference<Map<String,String>>() { });
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
//		System.out.println(patentsMap.size());
		companyPatentsService.updateGreen(patentsMap);
		
		
	}

}
