package net.hoyoung.wfp.spider.comweb;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.hoyoung.wfp.spider.comweb.bo.ComPage;
import net.hoyoung.wfp.spider.util.URLNormalizer;
import net.hoyoung.wfp.spider.util.UserAgentUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.utils.HttpConstant;
import us.codecraft.webmagic.utils.UrlUtils;
import us.codecraft.webmagic.utils.WMCollections;

/**
 * The http downloader based on HttpClient.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
@ThreadSafe
public class ComWebHttpClientDownloader extends AbstractDownloader {

	private Logger logger = LoggerFactory.getLogger(getClass());

	// private final Map<String, CloseableHttpClient> httpClients = new
	// HashMap<String, CloseableHttpClient>();

	private ComWebHttpClientGenerator httpClientGenerator = new ComWebHttpClientGenerator();

	private CloseableHttpClient getHttpClient(Site site, Proxy proxy) {
		if (site == null) {
			return httpClientGenerator.getClient(null, proxy);
		}
		// String domain = site.getDomain();
		// CloseableHttpClient httpClient = httpClients.get(domain);
		// if (httpClient == null) {
		// synchronized (this) {
		// httpClient = httpClients.get(domain);
		// if (httpClient == null) {
		// httpClient = httpClientGenerator.getClient(site, proxy);
		// httpClients.put(domain, httpClient);
		// }
		// }
		// }
		return httpClientGenerator.getClient(site, proxy);
	}

	@Override
	public Page download(Request request, Task task) {
		Site site = null;
		if (task != null) {
			site = task.getSite();
		}
		Set<Integer> acceptStatCode;
		String charset = null;
		Map<String, String> headers = null;
		if (site != null) {
			acceptStatCode = site.getAcceptStatCode();
			charset = site.getCharset();
			headers = site.getHeaders();
			// 随机agent
			headers.put("User-Agent", UserAgentUtil.getRandomAgent());
		} else {
			acceptStatCode = WMCollections.newHashSet(200);
		}

		CloseableHttpResponse httpResponse = null;
		int statusCode = 0;
		try {
			HttpHost proxyHost = null;
			Proxy proxy = null; // TODO
			if (site.getHttpProxyPool() != null && site.getHttpProxyPool().isEnable()) {
				proxy = site.getHttpProxyFromPool();
				proxyHost = proxy.getHttpHost();
			} else if (site.getHttpProxy() != null) {
				proxyHost = site.getHttpProxy();
			}
			if (proxy != null) {

			}
			logger.info("downloading(" + (proxyHost == null ? "localhost" : proxyHost.getAddress().getHostAddress())
					+ ") page {}", request.getUrl());
			HttpContext context = new BasicHttpContext();

			HttpUriRequest httpUriRequest = getHttpUriRequest(request, site, headers, proxyHost);
			httpResponse = getHttpClient(site, proxy).execute(httpUriRequest, context);

			statusCode = httpResponse.getStatusLine().getStatusCode();
			request.putExtra(Request.STATUS_CODE, statusCode);
			if (statusAccept(acceptStatCode, statusCode)) {
				Page page = new Page();

				// 获取content-length
				request.putExtra(ComPage.CONTENT_LENGTH, httpResponse.getEntity().getContentLength());
				Header contentType = httpResponse.getEntity().getContentType();
				if (contentType != null) {
					String value = contentType.getValue();
					if (value.contains(";")) {
						value = value.split(";")[0];
					}
					if (value.contains("/")) {
						value = value.split("/")[1];
					}
					request.putExtra(ComPage.CONTENT_TYPE, value);
				}
				/******** 判断是否为重定向 *********/
				// 获取跳转的url
				List<?> redirectUrls = (List<?>) context.getAttribute(HttpClientContext.REDIRECT_LOCATIONS);
				String landingPageUrl = null;
				if (CollectionUtils.isNotEmpty(redirectUrls)) {
					// 最后一个url是最终的url
					Object landingPageReq = redirectUrls.get(redirectUrls.size() - 1);
					String str = landingPageReq.toString();
					str = URLNormalizer.normalize(str);
					if (Pattern.matches(".+\\.(" + ComWebConstant.DOC_REGEX + ")", str)) {
						landingPageUrl = str;
					}
				}
				if(landingPageUrl==null && contentType==null){
					// 下载文件也把它当做重定向处理
					Header[] descHeader = httpResponse.getHeaders("Content-Description");
					Header[] fileHeader = httpResponse.getHeaders("Content-Disposition");
					if (descHeader != null && descHeader.length > 0 && fileHeader != null && fileHeader.length > 0 && "File Transfer".equals(descHeader[0].getValue())) {
//						Matcher matcher = Pattern.compile("filename=.*\\.("+ComWebConstant.DOC_REGEX+")").matcher(new String(fileHeader[0].getValue().getBytes("ISO-8859-1"),"utf8"));
//						if(matcher.find()){
//							request.putExtra(ComPage.CONTENT_TYPE, matcher.group(1));
//							landingPageUrl = request.getUrl();
//						}else {
//							landingPageUrl = "";
//						}
						// 下载文件的链接忽略
						landingPageUrl = "";
					}
				}

				if (landingPageUrl != null) {
					request.putExtra(ComWebConstant.LANDING_PAGE_KEY, landingPageUrl);
					httpResponse.getEntity().getContent().close();
					page.setUrl(new PlainText(request.getUrl()));
					page.setRequest(request);
					page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
				} else {
					page = handleResponse(request, charset, httpResponse, task);
				}
				onSuccess(request);
				return page;
			} else {
				logger.warn("get page {} error, status code {} ", request.getUrl(), statusCode);
				return null;
			}
		} catch (IOException e) {
			logger.warn("download page {} error", request.getUrl(), e);
			if (site.getCycleRetryTimes() > 0) {
				return addToCycleRetry(request, site);
			}
			onError(request);
			return null;
		} finally {
			request.putExtra(Request.STATUS_CODE, statusCode);
			if (site.getHttpProxyPool() != null && site.getHttpProxyPool().isEnable()) {
				site.returnHttpProxyToPool((HttpHost) request.getExtra(Request.PROXY),
						(Integer) request.getExtra(Request.STATUS_CODE));
			}
			try {
				if (httpResponse != null) {
					// ensure the connection is released back to pool
					EntityUtils.consume(httpResponse.getEntity());
				}
			} catch (IOException e) {
				logger.warn("close response fail", e);
			}
		}
	}

	@Override
	public void setThread(int thread) {
		httpClientGenerator.setPoolSize(thread);
	}

	protected boolean statusAccept(Set<Integer> acceptStatCode, int statusCode) {
		return acceptStatCode.contains(statusCode);
	}

	protected HttpUriRequest getHttpUriRequest(Request request, Site site, Map<String, String> headers,
			HttpHost proxy) {
		RequestBuilder requestBuilder = selectRequestMethod(request).setUri(request.getUrl());
		if (headers != null) {
			for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
				requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
			}
		}
		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
				.setConnectionRequestTimeout(site.getTimeOut()).setSocketTimeout(site.getTimeOut())
				.setConnectTimeout(site.getTimeOut()).setCookieSpec(CookieSpecs.BEST_MATCH);
		if (proxy != null) {
			requestConfigBuilder.setProxy(proxy);
			request.putExtra(Request.PROXY, proxy);
		}
		requestBuilder.setConfig(requestConfigBuilder.build());
		return requestBuilder.build();
	}

	protected RequestBuilder selectRequestMethod(Request request) {
		String method = request.getMethod();
		if (method == null || method.equalsIgnoreCase(HttpConstant.Method.GET)) {
			// default get
			return RequestBuilder.get();
		} else if (method.equalsIgnoreCase(HttpConstant.Method.POST)) {
			RequestBuilder requestBuilder = RequestBuilder.post();
			NameValuePair[] nameValuePair = (NameValuePair[]) request.getExtra("nameValuePair");
			if (nameValuePair != null && nameValuePair.length > 0) {
				requestBuilder.addParameters(nameValuePair);
			}
			return requestBuilder;
		} else if (method.equalsIgnoreCase(HttpConstant.Method.HEAD)) {
			return RequestBuilder.head();
		} else if (method.equalsIgnoreCase(HttpConstant.Method.PUT)) {
			return RequestBuilder.put();
		} else if (method.equalsIgnoreCase(HttpConstant.Method.DELETE)) {
			return RequestBuilder.delete();
		} else if (method.equalsIgnoreCase(HttpConstant.Method.TRACE)) {
			return RequestBuilder.trace();
		}
		throw new IllegalArgumentException("Illegal HTTP Method " + method);
	}

	protected Page handleResponse(Request request, String charset, HttpResponse httpResponse, Task task)
			throws IOException {
		String content = getContent(charset, httpResponse);
		Page page = new Page();
		page.setRawText(content);
		page.setUrl(new PlainText(request.getUrl()));
		page.setRequest(request);
		page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
		return page;
	}

	protected String getContent(String charset, HttpResponse httpResponse) throws IOException {
		if (charset == null) {
			byte[] contentBytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
			String htmlCharset = getHtmlCharset(httpResponse, contentBytes);
			if (htmlCharset != null) {
				return new String(contentBytes, htmlCharset);
			} else {
				logger.warn("Charset autodetect failed, use {} as charset. Please specify charset in Site.setCharset()",
						Charset.defaultCharset());
				return new String(contentBytes);
			}
		} else {
			return IOUtils.toString(httpResponse.getEntity().getContent(), charset);
		}
	}

	protected String getHtmlCharset(HttpResponse httpResponse, byte[] contentBytes) throws IOException {
		String charset;
		// charset
		// 1、encoding in http header Content-Type
		String value = httpResponse.getEntity().getContentType().getValue();
		charset = UrlUtils.getCharset(value);
		if (StringUtils.isNotBlank(charset)) {
			logger.debug("Auto get charset: {}", charset);
			return charset;
		}
		// use default charset to decode first time
		Charset defaultCharset = Charset.defaultCharset();
		String content = new String(contentBytes, defaultCharset.name());
		// 2、charset in meta
		if (StringUtils.isNotEmpty(content)) {
			Document document = Jsoup.parse(content);
			Elements links = document.select("meta");
			for (Element link : links) {
				// 2.1、html4.01 <meta http-equiv="Content-Type"
				// content="text/html; charset=UTF-8" />
				String metaContent = link.attr("content");
				String metaCharset = link.attr("charset");
				if (metaContent.indexOf("charset") != -1) {
					metaContent = metaContent.substring(metaContent.indexOf("charset"), metaContent.length());
					charset = metaContent.split("=")[1];
					break;
				}
				// 2.2、html5 <meta charset="UTF-8" />
				else if (StringUtils.isNotEmpty(metaCharset)) {
					charset = metaCharset;
					break;
				}
			}
		}
		logger.debug("Auto get charset: {}", charset);
		// 3、todo use tools as cpdetector for content decode
		return charset;
	}
}
