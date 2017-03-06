package net.hoyoung.wfp.spider;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import net.hoyoung.wfp.spider.comweb.process.HTMLExtractor;
import net.hoyoung.wfp.spider.comweb.urlfilter.DomainUrlFilter;
import us.codecraft.webmagic.utils.UrlUtils;

public class ContentExtractorTest {

	@Test
	public void test(){
		
		try {
			String content = HTMLExtractor.getContentAll(FileUtils.readFileToString(new File("discuz.html")));
			System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	@Test
	public void test2(){
		String domain = UrlUtils.getDomain("http://61.178.129.231/report/userLogin.jsp");
		System.out.println(Pattern.matches("((\\d+\\.){3}\\d+.*|localhost.*)", domain));
	}
}
