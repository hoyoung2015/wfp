package net.hoyoung.wfp.spider.comweb.urlfilter;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;

public class PageFilter {

	public boolean accept( Page page) {
		
		// 排除discuz
		String metaGenerator = page.getHtml().xpath("/html/head/meta[@name='generator']/@content").get();
		if(metaGenerator!=null&&metaGenerator.contains("Discuz")){
			return false;
		}
		
		return true;
	}
	public static void main(String[] args) throws IOException {
		Page page = new Page();
		page.setRequest(new Request("http://msxh.xsmd.com.cn/forum.php?mod=viewthread&tid=1853&page=1&authorid=10328"));
		page.setRawText(FileUtils.readFileToString(new File("discuz.html")));
		System.out.println(new PageFilter().accept(page));
	}
}
