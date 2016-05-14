package net.hoyoung.patents.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

public class HtmlParseTest {
	public static void main(String[] args) throws IOException {
		String s = FileUtils.readFileToString(new File("file/a.html"));
		Html html = new Html(s);
		
		String patInfo =  html.xpath("//div[@class=abstracts]/text()").get();
//		System.err.println(patInfo);
		
		Selectable tbody = html.xpath("//table[@id=perildical2_dl]/tbody");
		
		String patName =  html.xpath("//h1/text()").get();
//		System.err.println(patName);
		String comName =  tbody.xpath("/tbody/tr[8]/td/text()").get();
//		System.err.println(comName);
		String patCode =  tbody.xpath("/tbody/tr[2]/td/text()").get();
//		System.err.println(patCode);
		
		String patMainStdmode =  tbody.xpath("/tbody/tr[6]/td/text()").get();
//		System.err.println(patMainStdmode);
		
		String patStdmode =  tbody.xpath("/tbody/tr[7]/td/text()").get();
//		System.err.println(patStdmode);
		

	}
}	
