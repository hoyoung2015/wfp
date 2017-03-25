package net.hoyoung.wfp.spider.comweb;

import java.net.MalformedURLException;
import java.util.ArrayList;
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

import net.hoyoung.wfp.core.utils.WFPContext;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;
import net.hoyoung.wfp.spider.comweb.urlfilter.DomainUrlFilter;
import net.hoyoung.wfp.spider.comweb.urlfilter.PageFilter;
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
	private Logger logger = LoggerFactory.getLogger(getClass());

	private DomainUrlFilter urlFilter = new DomainUrlFilter();

	private PageFilter pageFilter = new PageFilter();

	private Pattern beachPattern = Pattern.compile("return jump_([a-zA-Z0-9]+)\\((\\d+),(\\d+)\\);");
	private Pattern currentNoPattern = Pattern.compile("var current_no=\"(.*?)\"");
	private Pattern currentSizePattern = Pattern.compile("var current_size=\"(.*?)\"");

	private List<String> beachUrlProcess(Page page) {
		Matcher matcher = currentSizePattern.matcher(page.getRawText());
		String currentSize = null;
		if (matcher.find()) {
			currentSize = matcher.group(1);
		}

		matcher = currentNoPattern.matcher(page.getRawText());
		String currentNo = null;
		if (matcher.find()) {
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
			// String key = matcher.group(1);
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

	@Override
	public void process(Page page0) {
		if (page0.getResultItems().isSkip()) {
			return;
		}
		if (!(page0 instanceof ComWebPage)) {
			return;
		}

		// 重定向出现的不合法的url
		if (urlFilter.isRejectFileUrl(page0.getRequest().getUrl())) {
			return;
		}

		ComWebPage page = (ComWebPage) page0;

		Document document = new Document(ComPage.STOCK_CODE, page.getRequest().getExtra(ComPage.STOCK_CODE))
				.append(ComPage.CONTENT_LENGTH, page.getContentLength())
				.append(ComPage.CONTENT_TYPE, page.getContentType()).append(ComPage.URL, page.getRequest().getUrl());
		if (page.getFilename() != null) {
			document.append(ComPage.FILE_NAME, page.getFilename());
		}
		ArrayList<Document> data = Lists.newArrayList(document);
		if (page.getRawText() == null) {
			page.putField(ComWebConstant.URL_LIST_KEY, data);
			return;
		}

		if (page.getHtml().links().regex("http://.*safedog.cn/.*").all().size() > 0) {
			// 遇到了安全狗
			logger.warn("{} {} found safedog", page.getRequest().getExtra(ComPage.STOCK_CODE),
					page.getRequest().getUrl());
			return;
		}
		if (pageFilter.accept(page) == false) {
			return;
		}
		document.put(ComPage.HTML, page.getRawText());
		// String content = HTMLExtractor.getContent(page.getRawText());
		// if (content == null)
		// content = "";
		// document.put(ComPage.CONTENT, content);
		// try {
		// String sha1 = EncryptUtil.encryptSha1(content);
		// document.append(ComPage.CONTENT_SHA1, sha1);
		// } catch (NoSuchAlgorithmException e1) {
		// e1.printStackTrace();
		// }
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
				if (Pattern.matches("http(s?)://.+\\.(" + ComWebConstant.DOC_REGEX + ")$", url)) {
					Document doc = new Document(ComPage.STOCK_CODE, page.getRequest().getExtra(ComPage.STOCK_CODE));
					doc.put(ComPage.URL, url);
					int i = url.lastIndexOf(".");
					String ext = url.substring(i + 1);
					if ("pdf".equals(ext.toLowerCase())) {
						doc.append(ComPage.CONTENT_TYPE, "pdf");
					} else {
						doc.append(ComPage.CONTENT_TYPE, "msword");
					}
					data.add(doc);
				} else {
					if (UrlUtils.getDomain(url).equals("www.xintaidianqi.com")) {
						url = url.replace("\\", "/");
					}
					Request request = new Request(url);
					request.putExtra(ComPage.STOCK_CODE, page.getRequest().getExtra(ComPage.STOCK_CODE));
					request.putExtra("domain", page.getRequest().getExtra("domain"));
					page.addTargetRequest(request);
				}
			}
		}
		page.putField(ComWebConstant.URL_LIST_KEY, data);
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

		all.addAll(fuckUrlProcess(page));
		all.addAll(beachUrlProcess(page));

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

	private Site site = Site.me().setSleepTime(WFPContext.getProperty("compage.spider.commonSleepTime", Integer.class))
			.setRetryTimes(3).setTimeOut(WFPContext.getProperty("compage.spider.sleepTime", Integer.class))
			.setCycleRetryTimes(WFPContext.getProperty("compage.spider.retryTime", Integer.class))
			.addHeader("Accept-Language", "zh-CN,zh;q=0.8")// 有的server需要这个
			.addHeader("User-Agent", UserAgentUtil.getRandomAgent());

	// .addHeader("User-Agent", "Sogou web
	// spider/3.0(+http://www.sogou.com/docs/help/webmasters.htm#07)");
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
