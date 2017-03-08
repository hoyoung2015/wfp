package net.hoyoung.wfp.spider.comweb.urlfilter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Selectable;

public class PageFilter {

	public boolean accept(Page page) {

		// 排除discuz
		String metaGenerator = page.getHtml().xpath("/html/head/meta[@name='generator']/@content").get();
		if (metaGenerator != null && metaGenerator.contains("Discuz")) {
			return false;
		}
		// 判断是否为标准的包含body的html
		List<Selectable> bodyNodes = page.getHtml().$("body > *").nodes();
		if (CollectionUtils.isEmpty(bodyNodes)) {
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
