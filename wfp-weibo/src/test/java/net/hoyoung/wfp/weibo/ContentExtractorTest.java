package net.hoyoung.wfp.weibo;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;

public class ContentExtractorTest {

	@Test
	public void test() throws IOException, Exception{
		
		String page = ContentExtractor.getContentByHtml(FileUtils.readFileToString(new File("D:/new.html")));
		
		System.out.println(page);
	}
}
