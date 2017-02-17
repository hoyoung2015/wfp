package net.hoyoung.wfp.spider.comweb;

import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.Document;

import com.google.common.collect.Lists;

import net.hoyoung.wfp.spider.comweb.bo.ComPage;
import net.hoyoung.wfp.spider.util.URLNormalizer;
import net.hoyoung.wfp.spider.util.UserAgentUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.UrlUtils;

public class PhantomJsPageProcessor implements PageProcessor {

	@Override
	public void process(Page page) {
		if ("404 Not Found".equals(page.getHtml().$("title", "text").get())) {
			return;
		}
		System.out.println(page.getRawText());
		Document document = new Document(ComPage.STOCK_CODE, page.getRequest().getExtra(ComPage.STOCK_CODE));
		String landingPageUrl = (String) page.getRequest().getExtra(ComWebConstant.LANDING_PAGE_KEY);
		if (StringUtils.isNotEmpty(landingPageUrl)) {
			document.put(ComPage.URL, landingPageUrl);
			page.putField(ComWebConstant.URL_LIST_KEY, Lists.newArrayList(document));
			System.out.println("redirect to " + landingPageUrl);
			return;
		}
		// 判断是否为标准的包含body的html
		List<Selectable> bodyNodes = page.getHtml().$("body > *").nodes();
		if (CollectionUtils.isEmpty(bodyNodes)) {
			return;
		}
		document.put(ComPage.URL, page.getRequest().getUrl());
		document.put(ComPage.HTML, page.getRawText());
		List<Document> list = Lists.newArrayList(document);
		List<String> links = this.urlFilter(page);
		if (CollectionUtils.isNotEmpty(links)) {
			for (String url : links) {
				try {
					url = URLNormalizer.normalize(url);
				} catch (MalformedURLException e) {
					continue;
				}
				if (StringUtils.isEmpty(url)) {
					continue;
				}
				// 文档
				if (Pattern.matches(".+\\.(" + ComWebConstant.DOC_REGEX + ")$", url)) {
					Document doc = new Document(ComPage.STOCK_CODE, page.getRequest().getExtra(ComPage.STOCK_CODE));
					doc.put(ComPage.URL, page.getRequest().getUrl());
					list.add(doc);
				} else {
					Request request = new Request(url);
					request.putExtra(ComPage.STOCK_CODE, page.getRequest().getExtra(ComPage.STOCK_CODE));
					page.addTargetRequest(request);
				}
			}
		}
		page.putField(ComWebConstant.URL_LIST_KEY, list);
	}

	static final String EXCEPT_SUFFIX = "xls|xlsx|gif|GIF|jpg|JPG|png|PNG|ico|ICO|css|CSS|sit|SIT|eps|EPS|wmf|WMF|zip|ZIP|rar|RAR|ppt|PPT|mpg|MPG|xls|XLS|gz|GZ|rpm|RPM|tgz|TGZ|mov|MOV|exe|EXE|jpeg|JPEG|bmp|BMP|js|JS|swf|SWF|flv|FLV|mp4|MP4|mp3|MP3|wmv|WMV";
	private static final int SLEEP_TIME = 300;

	private List<String> urlFilter(Page page) {
		List<String> all = page.getHtml().links().all();

		String domain = UrlUtils.getDomain(page.getRequest().getUrl()).replaceAll("^www\\.", "");
		Iterator<String> iterator = all.iterator();
		while (iterator.hasNext()) {
			String url = iterator.next();
			String domain2 = UrlUtils.getDomain(url);
			/**
			 * .css?v=1 .css,.jpg 站内 包含#，锚记 "mailto"开头 英文页，繁体
			 */
			if (Pattern.matches(".+(\\.|/)(" + EXCEPT_SUFFIX + ")\\?.*", url)
					|| Pattern.matches(".+\\.(" + EXCEPT_SUFFIX + ")$", url) || domain2.indexOf(domain) < 0
					|| url.contains("#") || url.startsWith("mailto")
					|| Pattern.matches(".+" + domain2 + "/(en|EN|tw|TW|english|ENGLISH)(/.*)?", url)
					|| Pattern.matches("://bbs\\." + domain + ".*", url)
					|| Pattern.matches(".+(&|\\?)id=\\-\\d+.*", url)) {
				iterator.remove();
			}
		}
		return all;
	}

	private Site site = Site.me().setSleepTime(SLEEP_TIME).setRetryTimes(3).setTimeOut(40000).setCycleRetryTimes(2)
			.addHeader("User-Agent", UserAgentUtil.getRandomAgent())
			.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
			.addHeader("Accept-Language", "zh-CN,zh;q=0.8").addHeader("Accept-Encoding", "gzip, deflate, sdch, br")
			.addHeader("Cache-Control", "max-age=0").addHeader("Upgrade-Insecure-Requests", "1");

	@Override
	public Site getSite() {
		return site;
	}

	public static void main(String[] args) {

		// RedisProxyPool proxyPool = null;
		// try {
		// proxyPool = new RedisProxyPool();
		// } catch (Exception e1) {
		// e1.printStackTrace();
		// System.err.println("init proxypool error");
		// System.exit(-1);
		// }

		PhantomJsPageProcessor processor = new PhantomJsPageProcessor();
		// processor.getSite().setHttpProxyPool(proxyPool);
		Spider spider = Spider.create(processor).thread(1);
		Request request = new Request("http://www.hoyoung.net");
		String stockCode = "601233";
		request.putExtra(ComWebConstant.STOCK_CODE_KEY, stockCode);
		spider.addRequest(request).setDownloader(new ComWebHttpClientDownloader()).addPipeline(new ComWebPipeline())
				.run();
	}

}