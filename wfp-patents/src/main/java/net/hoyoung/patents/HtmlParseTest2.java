package net.hoyoung.patents;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

public class HtmlParseTest2 {
	public static void main(String[] args) throws IOException {
		String s = FileUtils.readFileToString(new File("file/b.html"));
		Html html = new Html(s);
		
		List<String> list = HtmlParseTest2.getDetailUrl(html);
		for (String string : list) {
			System.out.println(string);
		}
	}
	private static List<String> getDetailUrl(Html html){
		List<Selectable> list = html.xpath("//ul[@class=list_ul]").nodes();
		List<String> urls = new ArrayList<String>();
		for(int i=0;i<list.size();i+=2){
			String a = list.get(i).xpath("/ul/li[1]/a[2]/@href").get();
			urls.add(a);
		}
		return urls;
	}
}	
