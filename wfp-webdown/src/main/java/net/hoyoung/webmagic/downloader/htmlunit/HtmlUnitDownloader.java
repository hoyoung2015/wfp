package net.hoyoung.webmagic.downloader.htmlunit;

import java.io.Closeable;
import java.io.IOException;

import org.apache.log4j.Logger;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.selector.PlainText;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class HtmlUnitDownloader implements Downloader,Closeable {
	private Logger logger = Logger.getLogger(getClass());
	private volatile WebClientPool webClientPool;
	private int poolSize = 1;
	private void checkInit() {
        if (webClientPool == null) {
            synchronized (this){
            	webClientPool = new WebClientPool(poolSize);
            }
        }
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
			webClient = webClientPool.get();
		} catch (InterruptedException e) {
			logger.warn("interrupted", e);
            return null;
		}
		logger.info("downloading page " + request.getUrl());
		HtmlPage htmlPage = null;
		Page page = new Page();
		try {
			htmlPage = webClient.getPage(request.getUrl());
		} catch (FailingHttpStatusCodeException | IOException e) {
			e.printStackTrace();
			page.setStatusCode(404);
		}
		
		page.setStatusCode(htmlPage.getWebResponse().getStatusCode());
		 String content = htmlPage.getWebResponse().getContentAsString();
		 page.setUrl(new PlainText(request.getUrl()));
		 page.setRequest(request);
		 page.setRawText(content);
		 page.getHtml();
		 webClientPool.returnToPool(webClient);
		return page;
	}
	@Override
	public void setThread(int threadNum) {
		this.poolSize = threadNum;
	}
	@Override
	public void close() {
    	webClientPool.closeAll();
    }
}
