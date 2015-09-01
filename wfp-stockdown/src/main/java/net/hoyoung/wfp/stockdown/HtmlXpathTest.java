package net.hoyoung.wfp.stockdown;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mongodb.BasicDBObject;

import net.hoyoung.wfp.core.utils.StringUtils;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

public class HtmlXpathTest {

	public static void main(String[] args) throws IOException {
		File file = new File("file/detail.html");
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
		BasicDBObject dbo = new BasicDBObject("stockCode","");
		List<Selectable> h_section2_lis = html.xpath("//div[@class='h_section2']/ul/li").nodes();
		for(int i=1;i<=3;i++){
			Selectable li = h_section2_lis.get(i);
			String s = li.xpath("/li/span[1]/text()").get().replaceAll("：|\\s", "");
			String s2 = li.xpath("/li/span[2]/text()").get().replaceAll("：|\\s", "");
			dbo.append(s, s2);
		}
		List<Selectable> list = html.xpath("/html/body/div/div[@class='article']/div[@class='a_section']").nodes();
		BasicDBObject dbo2 = new BasicDBObject();
		dbo.append("明细", dbo2);
		for (Selectable section : list) {
			String title = section.xpath("//p[@class='as_title']/text()").get();
//			String data = getData(title);
			title = StringUtils.removeBrackets(title);
			BasicDBObject dbo3 = new BasicDBObject();
			dbo2.append(title,dbo3);
			List<Selectable> asList = section.xpath("//div[@class='as_list']").nodes();
			for (Selectable section2 : asList) {
				String title2 = section2.xpath("//p[@class='c666666 bold']/text()").get();
				String data2 = getData(title2);
				title2 = StringUtils.removeBrackets(title2);
				BasicDBObject dbo4 = new BasicDBObject();
				dbo3.append(title2, dbo4);
				List<Selectable> lis = section2.xpath("//ul/li/text()").nodes();
				for (Selectable li : lis) {
					String detail = StringUtils.removeBrackets(li.get());
					String[] details = detail.split("：");
					dbo4.append(details[0], details[1]);
				}
			}
		}
	}
	public static String getData(String s){
		Pattern p = Pattern.compile("\\-?\\d+(\\.\\d+)?");
		Matcher m = p.matcher(s);
		if(m.find()){
			return m.group();
		}
		return null;
	}
}
