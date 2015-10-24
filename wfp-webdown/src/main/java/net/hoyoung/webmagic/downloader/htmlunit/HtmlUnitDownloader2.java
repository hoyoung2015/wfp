package net.hoyoung.webmagic.downloader.htmlunit;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import net.hoyoung.wfp.webdown.utils.WfpUrlUtils;

import org.apache.log4j.Logger;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.selector.PlainText;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class HtmlUnitDownloader2 implements Downloader,Closeable {
	private Logger logger = Logger.getLogger(getClass());
	private int poolSize = 1;
	private void checkInit() {
    }
	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	@Override
	public Page download(Request request, Task task) {
		checkInit();
		//获取当前页面
		WebClient webClient;
		try {
			webClient = new WebClient(BrowserVersion.CHROME);
			//这个必须加上，否则如果有页面不存在下载失败会报错
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
	        webClient.getOptions().setCssEnabled(false);
		} catch (Exception e) {
			logger.warn("interrupted", e);
            return null;
		}
		logger.info("downloading page " + request.getUrl());
		HtmlPage htmlPage = null;
		try {
			htmlPage = webClient.getPage(request.getUrl());
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<HtmlAnchor> anchors = htmlPage.getAnchors();
		System.out.println("----------------->");
		Page page = new Page();
		page.setStatusCode(htmlPage.getWebResponse().getStatusCode());
		for (HtmlAnchor htmlAnchor : anchors) {
			/*
			 * 校验是否合法
			 * 非空串
			 * 存在href属性
			 * 是否为本站
			 */
				com.gargoylesoftware.htmlunit.Page p = null;
				try {
					p = htmlAnchor.click();
				} catch (IOException e) {
					e.printStackTrace();
				}
				String newUrl = p.getUrl().toString();
				System.out.println("newUrl:"+newUrl);
				System.out.println(request.getUrl());
				System.out.println(WfpUrlUtils.isSameHost(request.getUrl(), newUrl));
				//判断是否为站内
				if(!WfpUrlUtils.isSameHost(request.getUrl(), newUrl)){
					continue;
				}
//				page.addTargetRequest(newUrl);
		}
		System.out.println("<-----------------");
		 String content = htmlPage.getWebResponse().getContentAsString();
		 page.setUrl(new PlainText(request.getUrl()));
		 page.setRequest(request);
		 page.setRawText(content);
		 page.getHtml();
		 webClient.closeAllWindows();
		 webClient = null;
		return page;
	}
	
	@Override
	public void setThread(int threadNum) {
		this.poolSize = threadNum;
	}
	@Override
	public void close() {
    }
}
