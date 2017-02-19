package net.hoyoung.wfp.spider.comweb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.Document;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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

public class ComWebProcessor implements PageProcessor {

	private Set<String> blackDomainSet = Sets.newHashSet();
	{
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("com_page_blacklist.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String tmp = null;
		try {
			while ((tmp = reader.readLine()) != null) {
				blackDomainSet.add(tmp.trim());
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	@Override
	public void process(Page page) {
		Document document = new Document(ComPage.STOCK_CODE, page.getRequest().getExtra(ComPage.STOCK_CODE))
				.append(ComPage.CONTENT_LENGTH, page.getRequest().getExtra(ComPage.CONTENT_LENGTH));

		if(page.getRequest().getExtra(ComPage.CONTENT_TYPE)!=null){
			document.put(ComPage.CONTENT_TYPE, page.getRequest().getExtra(ComPage.CONTENT_TYPE));
		}
		
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
					request.putExtra("domain", page.getRequest().getExtra("domain"));
					page.addTargetRequest(request);
				}
			}
		}
		page.putField(ComWebConstant.URL_LIST_KEY, list);
	}

	static final String EXCEPT_SUFFIX = "xls|xlsx|gif|GIF|jpg|JPG|png|PNG|ico|ICO|css|CSS|sit|SIT|eps|EPS|wmf|WMF|zip|ZIP|rar|RAR|ppt|PPT|mpg|MPG|xls|XLS|gz|GZ|rpm|RPM|tgz|TGZ|mov|MOV|exe|EXE|jpeg|JPEG|bmp|BMP|js|JS|swf|SWF|flv|FLV|mp4|MP4|mp3|MP3|wmv|WMV";
	private static final int SLEEP_TIME = 200;

	private List<String> urlFilter(Page page) {
		List<String> all = page.getHtml().links().all();
		String domain = (String) page.getRequest().getExtra("domain");
		Iterator<String> iterator = all.iterator();
		while (iterator.hasNext()) {
			String url = iterator.next();
			String domainThis = UrlUtils.getDomain(url);

			/**
			 * .css?v=1 .css,.jpg 站内 包含#，锚记 "mailto"开头 英文页，繁体
			 */
			if (!url.startsWith("http") // 不是http协议
					|| Pattern.matches(".+(\\.|/)(" + EXCEPT_SUFFIX + ")\\?.*", url)
					|| Pattern.matches(".+\\.(" + EXCEPT_SUFFIX + ")$", url) // 排除后缀
					|| !domainThis.endsWith(domain) 
					|| blackDomainSet.contains(domainThis)
					|| isbbs(domainThis, domain) // 排除bbs
					|| Pattern.matches("http(s?)://" + domainThis + "/(bbs|en|EN|tw|TW|english|ENGLISH)(/.*)?", url)
					|| Pattern.matches("http(s?)://bbs\\." + domain + ".*", url)
					|| Pattern.matches(".+(&|\\?)id=\\-\\d+.*", url)) {
				iterator.remove();
			}
		}
		return all;
	}

	private boolean isbbs(String domainThis, String domain) {
		int i = domainThis.indexOf(domain);
		if (i == 0)
			return false;
		String prefix = domainThis.substring(0, i - 1);
		if (prefix.startsWith("bbs") || prefix.endsWith("bbs")
				|| Pattern.matches("(bbs|mail|video|oa|newoa|hospital|english|en|email|de|jp)", prefix))
			return true;
		return false;
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

		ComWebProcessor processor = new ComWebProcessor();
		// processor.getSite().setHttpProxyPool(proxyPool);
		Spider spider = Spider.create(processor).thread(1);
		Request request = new Request("http://www.hoyoung.net");
		String stockCode = "601233";
		request.putExtra(ComWebConstant.STOCK_CODE_KEY, stockCode);
		spider.addRequest(request).setDownloader(new ComWebHttpClientDownloader()).addPipeline(new ComWebPipeline())
				.run();
	}

}
