package net.hoyoung.wfp.stockdown;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

public class HtmlXpathTest2 {

	public static void main(String[] args) throws IOException {
		File file = new File("file/test.html");
		FileInputStream fs = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fs, "GBK");
		BufferedReader br = new BufferedReader(isr);
		String temp = null;
		StringBuffer sb = new StringBuffer();
		while ((temp = br.readLine()) != null) {
			sb.append(temp);
		}
		fs.close();
		isr.close();
		br.close();
		
		
		Html html = new Html(sb.toString());
		Selectable div = html.xpath("//div[@class='w680']");
		List<Selectable> aSection = div.xpath("/div/div[@class='a_section']").nodes();
		for (Selectable section : aSection) {
			String title = section.xpath("/div/p[@class='as_title']/text()").get();
			System.out.println(title);
			
		}
		
	}

}
