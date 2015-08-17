package net.hoyoung.webmagic.downloader.htmlunit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;

public class WebClientPool {
	private Logger logger = Logger.getLogger(getClass());
	/*
	 * 存放创建的WebClient
	 */
	private List<WebClient> webClientList = Collections.synchronizedList(new ArrayList<WebClient>());
	 /**
     * 存放可用的WebClient
     */
    private BlockingDeque<WebClient> innerQueue = new LinkedBlockingDeque<WebClient>();
    private final static int STAT_RUNNING = 1;
    private final static int STAT_CLODED = 2;
    private AtomicInteger stat = new AtomicInteger(STAT_RUNNING);
	private final static int DEFAULT_CAPACITY = 5;
	private final int capacity;
	public WebClientPool(int capacity) {
		super();
		this.capacity = capacity;
	}
	public WebClientPool() {
		this(DEFAULT_CAPACITY);
	}
	public WebClient get() throws InterruptedException{
		WebClient poll = innerQueue.poll();
		if(poll != null){
			return poll;
		}
		if(webClientList.size()<capacity){
			synchronized (webClientList) {
				WebClient webClient = new WebClient(BrowserVersion.CHROME);
				//这个必须加上，否则如果有页面不存在下载失败会报错
				webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
				webClient.getOptions().setThrowExceptionOnScriptError(false);
		        webClient.getOptions().setCssEnabled(false);
		        webClient.getOptions().setJavaScriptEnabled(false);
//		        webClient.getOptions().setUseInsecureSSL(false);
				innerQueue.add(webClient);
				webClientList.add(webClient);
			}
		}
		return innerQueue.take();
	}
	 public void returnToPool(WebClient webClient) {
	        checkRunning();
	        innerQueue.add(webClient);
	    }
	protected void checkRunning() {
        if (!stat.compareAndSet(STAT_RUNNING, STAT_RUNNING)) {
            throw new IllegalStateException("Already closed!");
        }
    }
	public void closeAll() {
        boolean b = stat.compareAndSet(STAT_RUNNING, STAT_CLODED);
        if (!b) {
            throw new IllegalStateException("Already closed!");
        }
        for (WebClient webClient : webClientList) {
            logger.info("Close webClient" + webClient);
            webClient.closeAllWindows();
            webClient = null;
        }
    }
}
