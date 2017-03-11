package net.hoyoung.wfp.spider.comweb;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.URI;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.proxy.Proxy;

/**
 * @author code4crafter@gmail.com <br>
 * @since 0.4.0
 */
public class ComWebHttpClientGenerator {

	private PoolingHttpClientConnectionManager connectionManager;

	public ComWebHttpClientGenerator() {
		Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", SSLConnectionSocketFactory.getSocketFactory()).build();
		connectionManager = new PoolingHttpClientConnectionManager(reg);
		connectionManager.setDefaultMaxPerRoute(10);
	}

	public ComWebHttpClientGenerator setPoolSize(int poolSize) {
		connectionManager.setMaxTotal(poolSize);
		return this;
	}

	public CloseableHttpClient getClient(Site site, Proxy proxy) {
		return generateClient(site, proxy);
	}

	private CloseableHttpClient generateClient(Site site, Proxy proxy) {
		CredentialsProvider credsProvider = null;

		HttpClientBuilder httpClientBuilder = HttpClients.custom();

		if (proxy != null && StringUtils.isNotBlank(proxy.getUser()) && StringUtils.isNotBlank(proxy.getPassword())) {
			credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(
					new AuthScope(proxy.getHttpHost().getAddress().getHostAddress(), proxy.getHttpHost().getPort()),
					new UsernamePasswordCredentials(proxy.getUser(), proxy.getPassword()));
			httpClientBuilder.setDefaultCredentialsProvider(credsProvider);
		}

		if (site != null && site.getHttpProxy() != null && site.getUsernamePasswordCredentials() != null) {
			credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope(site.getHttpProxy()), // 可以访问的范围
					site.getUsernamePasswordCredentials());// 用户名和密码
			httpClientBuilder.setDefaultCredentialsProvider(credsProvider);
		}

		httpClientBuilder.setConnectionManager(connectionManager);
		if (site != null && site.getUserAgent() != null) {
			httpClientBuilder.setUserAgent(site.getUserAgent());
		} else {
			httpClientBuilder.setUserAgent("");
		}
		if (site == null || site.isUseGzip()) {
			httpClientBuilder.addInterceptorFirst(new HttpRequestInterceptor() {

				public void process(final HttpRequest request, final HttpContext context)
						throws HttpException, IOException {
					if (!request.containsHeader("Accept-Encoding")) {
						request.addHeader("Accept-Encoding", "gzip");
					}

				}
			});
		}
		// 加入拦截器修复
		httpClientBuilder.addInterceptorFirst(new HttpResponseInterceptor() {
			@Override
			public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {

				final Header locationHeader = response.getFirstHeader("location");
				if (locationHeader != null && StringUtils.isNotEmpty(locationHeader.getValue())) {
					String location = locationHeader.getValue();
					location = new String(location.getBytes("ISO-8859-1"), "utf8");
					if(Pattern.matches(".*[\u4e00-\u9fa5]+.*", location)){
						location = new URI(location, false, "utf-8").toString();
					}
					response.removeHeader(locationHeader);
					response.addHeader("location", location);
				}

				Header contentEncodingHeader = response.getFirstHeader(HTTP.CONTENT_ENCODING);
				if (contentEncodingHeader != null && contentEncodingHeader.getValue().equalsIgnoreCase("none")) {
					response.removeHeaders(HTTP.CONTENT_ENCODING);
					response.addHeader(HTTP.CONTENT_ENCODING, "identity");
					HttpEntity entity = response.getEntity();
					if (entity != null && entity instanceof HttpEntityWrapper) {
						HttpEntityWrapper wEntity = (HttpEntityWrapper) entity;
						try {
							Field field = HttpEntityWrapper.class.getDeclaredField("wrappedEntity");
							field.setAccessible(true);
							Object o = field.get(wEntity);
							if (o != null && o instanceof BasicHttpEntity) {
								BasicHttpEntity bEntity = (BasicHttpEntity) o;
								bEntity.setContentEncoding("identity");
							}
						} catch (NoSuchFieldException e) {
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}

			}
		});

		SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(site.getTimeOut()).setSoKeepAlive(true)
				.setTcpNoDelay(true).build();
		httpClientBuilder.setDefaultSocketConfig(socketConfig);
		connectionManager.setDefaultSocketConfig(socketConfig);
		if (site != null) {
			httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(site.getRetryTimes(), true));
		}
		generateCookie(httpClientBuilder, site);
		return httpClientBuilder.build();
	}

	private void generateCookie(HttpClientBuilder httpClientBuilder, Site site) {
		CookieStore cookieStore = new BasicCookieStore();
		for (Map.Entry<String, String> cookieEntry : site.getCookies().entrySet()) {
			BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
			cookie.setDomain(site.getDomain());
			cookieStore.addCookie(cookie);
		}
		for (Map.Entry<String, Map<String, String>> domainEntry : site.getAllCookies().entrySet()) {
			for (Map.Entry<String, String> cookieEntry : domainEntry.getValue().entrySet()) {
				BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
				cookie.setDomain(domainEntry.getKey());
				cookieStore.addCookie(cookie);
			}
		}
		httpClientBuilder.setDefaultCookieStore(cookieStore);
	}

}