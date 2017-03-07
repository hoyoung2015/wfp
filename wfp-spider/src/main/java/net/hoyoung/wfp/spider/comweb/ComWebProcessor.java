package net.hoyoung.wfp.spider.comweb;

import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import net.hoyoung.wfp.core.utils.EncryptUtil;
import net.hoyoung.wfp.core.utils.WFPContext;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;
import net.hoyoung.wfp.spider.comweb.process.HTMLExtractor;
import net.hoyoung.wfp.spider.comweb.urlfilter.DomainUrlFilter;
import net.hoyoung.wfp.spider.comweb.urlfilter.PageFilter;
import net.hoyoung.wfp.spider.util.URLNormalizer;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

public class ComWebProcessor implements PageProcessor {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private DomainUrlFilter urlFilter = new DomainUrlFilter();

	private PageFilter pageFilter = new PageFilter();

	@Override
	public void process(Page page) {
		Document document = new Document(ComPage.STOCK_CODE, page.getRequest().getExtra(ComPage.STOCK_CODE))
				.append(ComPage.CONTENT_LENGTH, page.getRequest().getExtra(ComPage.CONTENT_LENGTH));
		
		if (page.getRequest().getExtra(ComPage.CONTENT_TYPE) != null) {
			document.put(ComPage.CONTENT_TYPE, page.getRequest().getExtra(ComPage.CONTENT_TYPE));
		}

		String landingPageUrl = (String) page.getRequest().getExtra(ComWebConstant.LANDING_PAGE_KEY);
		if (StringUtils.isNotEmpty(landingPageUrl)) {
			document.put(ComPage.URL, landingPageUrl);
			page.putField(ComWebConstant.URL_LIST_KEY, Lists.newArrayList(document));
			System.out.println("redirect to " + landingPageUrl);
			return;
		}
		if (page.getHtml().links().regex("http://.*safedog.cn/.*").all().size() > 0) {
			// 遇到了安全狗
			logger.warn("{} {} safedog.cn", page.getRequest().getExtra(ComPage.STOCK_CODE), page.getRequest().getUrl());
			return;
		}
		if (pageFilter.accept(page) == false) {
			return;
		}
		// 判断是否为标准的包含body的html
		List<Selectable> bodyNodes = page.getHtml().$("body > *").nodes();
		if (CollectionUtils.isEmpty(bodyNodes)) {
			return;
		}
		document.put(ComPage.URL, page.getRequest().getUrl());
		document.put(ComPage.HTML, page.getRawText());
		String content = HTMLExtractor.getContent(page.getRawText());
		if(content==null) content = "";
		document.put(ComPage.CONTENT, content);
		try {
			String sha1 = EncryptUtil.encryptSha1(content);
			document.append(ComPage.CONTENT_SHA1, sha1);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
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
					doc.put(ComPage.URL, url);
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

	private List<String> fuckUrlProcess(Page page) {
		List<String> list = Lists.newArrayList();
		Pattern pattern = Pattern
				.compile("<a[^>]*window\\.location\\.href=(\"([^\"]*)\"|\'([^\']*)\'|([^\\s>]*))[^>]*>(.*?)</a>");
		Matcher matcher = pattern.matcher(page.getRawText());
		while (matcher.find()) {
			String url = page.getRequest().getUrl();
			int i = url.lastIndexOf("/");
			list.add(url.substring(0, i) + "/" + matcher.group(1).replaceAll("'|\"", ""));
		}
		return list;
	}
	
	private List<String> urlFilter(Page page) {
		List<String> all = page.getHtml().links().all();
		
		List<String> fuckLinks = fuckUrlProcess(page);
		all.addAll(fuckLinks);
		
		String domain = (String) page.getRequest().getExtra("domain");
		Iterator<String> iterator = all.iterator();
		while (iterator.hasNext()) {
			String url = iterator.next();
			if (urlFilter.accept(domain, url) == false) {
				iterator.remove();
			}
		}
		return all;
	}

	private Site site = Site.me().setSleepTime(WFPContext.getProperty("compage.spider.commonSleepTime", Integer.class)).setRetryTimes(3).setTimeOut(50000).setCycleRetryTimes(2)
			.addHeader("User-Agent", "Googlebot/2.1 (+http://www.google.com/bot.html)")
			.addHeader("Accept-Language", "zh-CN,zh;q=0.8");

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
