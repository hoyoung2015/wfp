package net.hoyoung.wfp.spider;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.beust.jcommander.internal.Lists;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.selector.Selectable;

public class BeachUrlTest {

	private Pattern beachPattern = Pattern.compile("return jump_([a-zA-Z0-9]+)\\((\\d+),(\\d+)\\);");
	private Pattern currentNoPattern = Pattern.compile("var current_no=\"(.*?)\"");
	private String currentSize = null;

	private List<String> beachUrlProcess(Page page) {
		if (currentSize == null) {
			synchronized (this) {
				if (currentSize == null) {
					Pattern pattern = Pattern.compile("var current_size=\"(.*?)\"");
					Matcher matcher = pattern.matcher(page.getRawText());
					if (matcher.find()) {
						this.currentSize = matcher.group(1);
					}
				}
			}
		}
		Matcher matcher = currentNoPattern.matcher(page.getRawText());
		String currentNo = null;
		if(matcher.find()){
			currentNo = matcher.group(1);
		}
		
		
		List<String> list = Lists.newArrayList();
		for (Selectable e : page.getHtml().$("a", "onclick").nodes()) {
			if (StringUtils.isEmpty(e.get())) {
				continue;
			}
			String s = e.get().trim();
			String jumplocation = page.getRequest().getUrl();
			if (page.getRequest().getUrl().indexOf("=") == -1) {
				jumplocation = jumplocation.replace(".html", "/.html");
			}
			matcher = beachPattern.matcher(s);
			if (!matcher.find()) {
				continue;
			}
//			String key = matcher.group(1);
			String pageNo = matcher.group(2);
			String pageSize = matcher.group(3);
			if (page.getRequest().getUrl().indexOf(currentNo) != -1) {
				jumplocation = jumplocation.replace(currentNo, currentNo.replaceAll("\\d+$", pageNo));
			} else {
				jumplocation = jumplocation.replace(".html", "&" + currentNo.replaceAll("\\d+$", pageNo) + ".html");
			}
			if (page.getRequest().getUrl().indexOf(currentSize) != -1) {
				jumplocation = jumplocation.replace(currentSize, currentSize.replaceAll("\\d+$", pageSize));
			} else {
				jumplocation = jumplocation.replace(".html", "&" + currentSize.replaceAll("\\d+$", pageSize) + ".html");
			}
			list.add(jumplocation);
		}

		return list;
	}

	@Test
	public void test() {
		String rawText = null;
		try {
			rawText = FileUtils.readFileToString(new File("log/jump.html"));
		} catch (IOException e) {
			return;
		}
		Page page = new Page();
		page.setRawText(rawText);
		Request request = new Request(
				"http://www.chinakingking.com/news_list/newsCategoryId=3&FrontNews_list01-1450763995000_pageNo=2&FrontNews_list01-1450763995000_pageSize=10.html");
		page.setRequest(request);
		page.setUrl(new PlainText(request.getUrl()));

		List<String> list = beachUrlProcess(page);
		for (String s : list) {
			System.out.println(s);
		}

	}
}
