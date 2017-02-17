package net.hoyoung.wfp.patent;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.junit.Test;

import com.google.common.collect.Lists;

import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

public class HtmlParser {

	@Test
	public void test() {
		String s = null;
		try {
			s = FileUtils.readFileToString(new File("patient.html"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Html html = new Html(s);
		List<Selectable> items = html
				.$("body > div.content.content-search.clear > div.right > div.record-item-list > div.record-item")
				.nodes();
		List<Document> documents = Lists.newArrayList();
		for (Selectable div : items) {
			String detailUrl = div.$("div.left-record > div.record-title > a.title", "href").get();
			String patentName = div.$("div.left-record > div.record-title > a.title", "text").get();
			String patentType = getPatentType(div);
			String date = getPatentDate(div);
			System.out.println(detailUrl + "\t" + patentName + "\t" + patentType + "\t" + date);
			documents.add(new Document(PatentPage.STOCK_CODE, "").append(PatentPage.DETAIL_URL, detailUrl)
					.append(PatentPage.DATE, date).append(PatentPage.PATENT_NAME, patentName)
					.append(PatentPage.PATENT_TYPE, patentType));
		}
	}

	private String getPatentDate(Selectable div) {
		String s = div.$("div.left-record > div.record-subtitle", "text").get();
		Matcher matcher = Pattern.compile("(\\d{4}年\\d{1,2}月\\d{1,2})").matcher(s);
		if (matcher.find()) {
			s = matcher.group(1);
			return s.replace("年", "-").replace("月", "-");
		}
		return null;
	}

	private static final Set<String> PATENT_TYPE = new HashSet<String>() {
		private static final long serialVersionUID = 945955866676266694L;
		{
			add("实用新型");
			add("发明专利");
			add("外观设计");
		}
	};

	private String getPatentType(Selectable div) {
		String s = div.$("div.left-record > div.record-subtitle", "text").get();
		Matcher matcher = Pattern.compile("\\[(.*)\\]").matcher(s);
		if (matcher.find() && PATENT_TYPE.contains(matcher.group(1))) {
			return matcher.group(1);
		} else {
			return null;
		}
	}
	@Test
	public void test2(){
		String url = "http://s.wanfangdata.com.cn/patent.aspx?q=%E4%B8%93%E5%88%A9%E6%9D%83%E4%BA%BA%3A%E6%97%A5%E5%87%BA%E4%B8%9C%E6%96%B9%E5%A4%AA%E9%98%B3%E8%83%BD%E8%82%A1%E4%BB%BD%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8&f=top&p=1";
		Matcher matcher = Pattern.compile("&p=(\\d+)$").matcher(url);
		if(matcher.find()){
			System.out.println(matcher.group(1));
		}
	}
}
