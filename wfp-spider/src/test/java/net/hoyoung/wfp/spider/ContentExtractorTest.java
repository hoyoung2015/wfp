package net.hoyoung.wfp.spider;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import net.hoyoung.wfp.spider.comweb.process.HTMLExtractor;

public class ContentExtractorTest {

	@Test
	public void test(){
		
		try {
			String content = HTMLExtractor.getContent(FileUtils.readFileToString(new File("discuz.html")));
			System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
