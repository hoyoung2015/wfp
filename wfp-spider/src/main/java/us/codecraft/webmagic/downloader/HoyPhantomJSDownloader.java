package us.codecraft.webmagic.downloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.annotation.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.hash.Hashing;

import net.hoyoung.wfp.spider.vo.PhantomJsParams;
import net.hoyoung.wfp.spider.vo.PhantomJsResponse;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.utils.WMCollections;

/**
 * this downloader is used to download pages which need to render the javascript
 *
 * @author dolphineor@gmail.com
 * @version 0.5.3
 */
@ThreadSafe
public class HoyPhantomJSDownloader extends AbstractDownloader {

	private static Logger logger = LoggerFactory.getLogger(PhantomJSDownloader.class);
	private static String crawlJsPath;
	private static String phantomJsCommand = "phantomjs"; // default

	private static String TMP_PATH = ".phantomjs/";

	private int retryNum;
	private int threadNum;

	public HoyPhantomJSDownloader() {
		this.initPhantomjsCrawlPath();
	}

	/**
	 * 添加新的构造函数，支持phantomjs自定义命令
	 * 
	 * example: phantomjs.exe 支持windows环境 phantomjs --ignore-ssl-errors=yes
	 * 忽略抓取地址是https时的一些错误 /usr/local/bin/phantomjs
	 * 命令的绝对路径，避免因系统环境变量引起的IOException
	 * 
	 * @param phantomJsCommand
	 *            phantomJsCommand
	 */
	public HoyPhantomJSDownloader(String phantomJsCommand) {
		this.initPhantomjsCrawlPath();
		HoyPhantomJSDownloader.phantomJsCommand = phantomJsCommand;
		System.out.println(threadNum);
	}

	private void initPhantomjsCrawlPath() {
		// HoyPhantomJSDownloader.crawlJsPath = new
		// File(this.getClass().getResource("/").getPath()).getPath()
		// + System.getProperty("file.separator") + "crawl.js ";
		HoyPhantomJSDownloader.crawlJsPath = "/Users/baidu/workspace/wfp/wfp-spider/src/main/resources/crawl.js";
	}

	@Override
	public Page download(Request request, Task task) {
		if (logger.isInfoEnabled()) {
			logger.info("downloading page: " + request.getUrl());
		}
		PhantomJsResponse response = getPage(request, task.getSite());
		Set<Integer> acceptStatCode;
		Site site = task.getSite();
		if (site != null) {
			acceptStatCode = site.getAcceptStatCode();
		} else {
			acceptStatCode = WMCollections.newHashSet(200);
		}
		if (acceptStatCode.contains(response.getStatusCode())) {
			onSuccess(request);
			String content = response.getContent();
			Page page = new Page();
			page.setRawText(content);
			page.setUrl(new PlainText(request.getUrl()));
			page.setRequest(request);
			page.setStatusCode(response.getStatusCode());
			return page;
		} else {
			logger.warn("get page {} error, status code {} ", request.getUrl(), response.getStatusCode());
			return null;
		}
	}

	@Override
	public void setThread(int threadNum) {
		this.threadNum = threadNum;
	}

	protected PhantomJsResponse getPage(Request request, Site site) {
		PhantomJsResponse response = new PhantomJsResponse();
		PhantomJsParams params = new PhantomJsParams();
		params.setTimeOut(site.getTimeOut());
		params.setUrl(request.getUrl());
		params.setHeaders(site.getHeaders());
		String jsonParams = JSON.toJSONString(params);

		String jsonFileName = TMP_PATH
				+ Hashing.md5().newHasher().putString(jsonParams, Charset.defaultCharset()).hash().toString() + ".json";

		try {
			FileUtils.writeStringToFile(new File(jsonFileName), jsonParams);
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}

		String proxyConfig = "";
		if (site.getHttpProxyPool() != null && site.getHttpProxyPool().isEnable()) {
			Proxy proxy = site.getHttpProxyFromPool();
			proxyConfig += " --proxy=" + proxy.getHttpHost().getHostName() + ":" + proxy.getHttpHost().getPort();
			if (StringUtils.isNotEmpty(proxy.getUser())) {
				proxyConfig += " --proxy-auth=" + proxy.getUser() + ":" + proxy.getPassword();
			}
		}
		try {
			Runtime runtime = Runtime.getRuntime();
			String commond = phantomJsCommand + proxyConfig + " " + crawlJsPath + " " + jsonFileName;
			logger.debug("command:{}", commond);
			Process process = runtime.exec(commond);
			InputStream is = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			boolean isContent = false;
			while ((line = br.readLine()) != null) {
				logger.debug(line);
				if (isContent) {
					stringBuffer.append(line).append("\n");
					continue;
				}
				if (line.startsWith("statusCode:")) {
					int i = line.indexOf(":");
					response.setStatusCode(Integer.valueOf(line.substring(i + 1)));
				} else if (line.startsWith("redirectURL:")) {
					int i = line.indexOf(":");
					String redirectUrl = line.substring(i + 1);
					if (StringUtils.isNotEmpty(redirectUrl) && !"null".equals(redirectUrl)) {
						response.setRedirectUrl(redirectUrl);
					}
				} else if (">----------------------<".equals(line)) {
					isContent = true;
				}
			}
			response.setContent(stringBuffer.toString());
			return response;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			File file = new File(jsonFileName);
			if (file.exists()) {
				file.delete();
			}
		}

		return null;
	}

	public int getRetryNum() {
		return retryNum;
	}

	public HoyPhantomJSDownloader setRetryNum(int retryNum) {
		this.retryNum = retryNum;
		return this;
	}
}
