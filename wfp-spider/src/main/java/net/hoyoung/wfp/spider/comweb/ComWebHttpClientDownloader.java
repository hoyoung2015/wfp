package net.hoyoung.wfp.spider.comweb;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.transport.http.Headers;
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

	private String getContentTypeValue(Header contentType) {
		String contentTypeValue = null;
		if (contentType != null) {
			contentTypeValue = contentType.getValue();
			if (contentTypeValue.contains(";")) {
				contentTypeValue = contentTypeValue.split(";")[0];
			}
			if (contentTypeValue.contains("/")) {
				contentTypeValue = contentTypeValue.split("/")[1];
			}
		}
		return contentTypeValue;
	}

	public void redirectProcess(Request request, HttpContext context) {
		// 获取跳转的url
		List<?> redirectUrls = (List<?>) context.getAttribute(HttpClientContext.REDIRECT_LOCATIONS);
		if (CollectionUtils.isNotEmpty(redirectUrls)) {
			// 最后一个url是最终的url
			Object landingPageReq = redirectUrls.get(redirectUrls.size() - 1);
			String str = landingPageReq.toString();
			logger.info("redirect occured,set request url with {}", str);
			request.setUrl(str);
		}
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
		} else {
			acceptStatCode = WMCollections.newHashSet(200);
		}

		CloseableHttpResponse httpResponse = null;
		int statusCode = 0;
		HttpUriRequest httpUriRequest = null;
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

			httpUriRequest = getHttpUriRequest(request, site, headers, proxyHost);

			httpResponse = getHttpClient(site, proxy).execute(httpUriRequest, context);

			statusCode = httpResponse.getStatusLine().getStatusCode();
			request.putExtra(Request.STATUS_CODE, statusCode);
			if (statusAccept(acceptStatCode, statusCode)) {
				/******** 判断是否为重定向 *********/
				redirectProcess(request, context);
				ComWebPage page = new ComWebPage();
				page.setRequest(request);
				page.setUrl(new PlainText(request.getUrl()));
				page.setStatusCode(statusCode);

				Header contentType = httpResponse.getEntity().getContentType();

				// httpResponse.getAllHeaders()

				Header[] dispoHeader = httpResponse.getHeaders("Content-Disposition");

				String contentTypeValue = getContentTypeValue(contentType);
				if (contentType == null && dispoHeader != null && dispoHeader.length > 0) {
					// http://www.nhwa-group.com/sitefiles/services/cms/utils.aspx?type=Download&publishmentSystemID=4&channelID=72&contentID=461
					String dispoValue = dispoHeader[0].getValue();
					// 下载文件
					Matcher matcher = Pattern.compile("filename=(.+\\.(" + ComWebConstant.DOC_REGEX + "))")
							.matcher(new String(dispoValue.getBytes("ISO-8859-1"), "utf8"));
					if (matcher.find()) {
						// TODO
						String filename = matcher.group(1);
						String ext = matcher.group(2);
						if ("pdf".equals(ext.toLowerCase())) {
							page.setContentType("pdf");
						} else {
							page.setContentType("msword");
						}
						page.setFilename(filename);
						return page;
					} else {
						// 其他类型的下载文件skip
						page.setSkip(true);
						return page;
					}
				}

				if (contentTypeValue != null && !Pattern.matches("(msword|pdf|html|octet-stream)", contentTypeValue)) {
					page.setSkip(true);
					return page;
				}
				if (contentTypeValue != null) {
					page.setContentLength(httpResponse.getEntity().getContentLength());
					page.setContentType(contentTypeValue);
				}
				if (page.getContentLength() == -1 || Pattern.matches("html", contentTypeValue)) {// html
					page.setContentType("html");
					String content = getContent(charset, httpResponse);
					page.setRawText(content);
					return page;
				}
				// 文件下载流，这里只要文档
				if ("octet-stream".equals(contentTypeValue)) {
					page.setContentType(contentTypeValue);
					Header[] fileHeader = httpResponse.getHeaders("Content-Disposition");
					if (fileHeader != null && fileHeader.length > 0 && fileHeader[0].getValue().startsWith("attachment")
							&& page.getContentLength() <= 40 * 1024 * 1024) {
						// 下载文件
						Matcher matcher = Pattern.compile("filename=(.+\\.(" + ComWebConstant.DOC_REGEX + "))")
								.matcher(new String(fileHeader[0].getValue().getBytes("ISO-8859-1"), "utf8"));
						if (matcher.find()) {
							// TODO
							String filename = matcher.group(1);
							String ext = matcher.group(2);
							if ("pdf".equals(ext.toLowerCase())) {
								page.setContentType("pdf");
							} else {
								page.setContentType("msword");
							}
							page.setFilename(filename);
							return page;
						} else {
							// 其他类型的下载文件skip
							page.setSkip(true);
							return page;
						}
					}
				}
				// 文档直接返回
				if (Pattern.matches("(msword|pdf)", page.getContentType())) {
					page.setStatusCode(statusCode);
					return page;
				}
				// 其它未知类型skip
				page.setSkip(true);
				return page;
			} else {
				logger.warn("get page {} error, status code {} ", request.getUrl(), statusCode);
				return null;
			}
		} catch (IOException e) {
			logger.warn("download page {} error", request.getUrl(), e);
			if (site.getCycleRetryTimes() > 0 && statusCode != 404) {
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
					httpResponse.close();
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
		if (proxy != null) {
			// 这里将其提到前面，因为如果url验证不合法会抛出异常，异常处理会返还proxy，必须先设置
			request.putExtra(Request.PROXY, proxy);
		}
		RequestBuilder requestBuilder = selectRequestMethod(request);
		if (requestBuilder.getUri() == null) {
			requestBuilder.setUri(request.getUrl());
		}
		if (headers != null) {
			for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
				requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
			}
		}
		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
				.setConnectionRequestTimeout(site.getTimeOut()).setSocketTimeout(site.getTimeOut())
				.setConnectTimeout(site.getTimeOut()).setCookieSpec(CookieSpecs.DEFAULT);
		if (proxy != null) {
			requestConfigBuilder.setProxy(proxy);
		}
		requestBuilder.setConfig(requestConfigBuilder.build());
		return requestBuilder.build();
	}

	protected RequestBuilder selectRequestMethod(Request request) {
		String method = request.getMethod();
		if (method == null || method.equalsIgnoreCase(HttpConstant.Method.GET)) {
			// default get
			String url = request.getUrl();
			try {
				URI uri = URI.create(url);
				return RequestBuilder.get(uri);
			} catch (IllegalArgumentException e) {
				url = url.replace("^", "%5e");
				if (url.contains("|")) {
					url = url.replace("|", "");
					logger.warn("{} contails shuxian", url);
				}
			}
			return RequestBuilder.get(URI.create(url));
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

	protected ComWebPage handleResponse(Request request, String charset, HttpResponse httpResponse, Task task)
			throws IOException {
		String content = getContent(charset, httpResponse);
		ComWebPage page = new ComWebPage();
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
				if ("gbk2312".equals(htmlCharset)) {
					htmlCharset = "gb2312";
				} else if (htmlCharset.contains("utf-8")) {
					htmlCharset = "utf-8";
				}
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
