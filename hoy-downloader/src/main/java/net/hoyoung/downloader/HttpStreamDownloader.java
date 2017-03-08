package net.hoyoung.downloader;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;
import us.codecraft.webmagic.downloader.HttpClientGenerator;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.utils.HttpConstant;
import us.codecraft.webmagic.utils.WMCollections;

/**
 * The http downloader based on HttpClient.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
@ThreadSafe
public class HttpStreamDownloader extends AbstractDownloader {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private final Map<String, CloseableHttpClient> httpClients = new HashMap<String, CloseableHttpClient>();

	private HttpClientGenerator httpClientGenerator = new HttpClientGenerator();

	private CloseableHttpClient getHttpClient(Site site, Proxy proxy) {
		if (site == null) {
			return httpClientGenerator.getClient(null, proxy);
		}
		String domain = site.getDomain();
		CloseableHttpClient httpClient = httpClients.get(domain);
		if (httpClient == null) {
			synchronized (this) {
				httpClient = httpClients.get(domain);
				if (httpClient == null) {
					httpClient = httpClientGenerator.getClient(site, proxy);
					httpClients.put(domain, httpClient);
				}
			}
		}
		return httpClient;
	}

	@Override
	public Page download(Request request, Task task) {
		Site site = null;
		if (task != null) {
			site = task.getSite();
		}
		Set<Integer> acceptStatCode;
		Map<String, String> headers = null;
		if (site != null) {
			acceptStatCode = site.getAcceptStatCode();
			headers = site.getHeaders();
		} else {
			acceptStatCode = WMCollections.newHashSet(200);
		}
		headers.put("Range", String.format("bytes=%d-%d", request.getExtra("start"), request.getExtra("end")));

		logger.info("downloading page {}", request.getUrl());
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

			HttpUriRequest httpUriRequest = getHttpUriRequest(request, site, headers, proxyHost);// ���������˴���
			httpResponse = getHttpClient(site, proxy).execute(httpUriRequest);// getHttpClient�������˴�����֤
			statusCode = httpResponse.getStatusLine().getStatusCode();
			request.putExtra(Request.STATUS_CODE, statusCode);
			if (statusAccept(acceptStatCode, statusCode)) {
				InputStream in = httpResponse.getEntity().getContent();

				RandomAccessFile raf = new RandomAccessFile("a.pdf", "rw");
				Long start = (Long) request.getExtra("start");
				raf.seek(start);
				byte[] buffer = new byte[4 * 1024];
				int len = 0;
				while ((len = in.read(buffer)) > 0) {
					raf.write(buffer, 0, len);
				}

				raf.close();

				Page page = new Page();
				page.setUrl(new PlainText(request.getUrl()));
				page.setRequest(request);
				page.setStatusCode(httpResponse.getStatusLine().getStatusCode());

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
				.setConnectTimeout(site.getTimeOut()).setCookieSpec(CookieSpecs.DEFAULT);
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
}