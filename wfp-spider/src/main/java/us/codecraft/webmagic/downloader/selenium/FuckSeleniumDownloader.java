package us.codecraft.webmagic.downloader.selenium;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import net.hoyoung.wfp.spider.comweb.ComWebPage;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.selector.PlainText;

/**
 * 使用Selenium调用浏览器进行渲染。目前仅支持chrome。<br>
 * 需要下载Selenium driver支持。<br>
 *
 * @author code4crafter@gmail.com <br>
 * Date: 13-7-26 <br>
 * Time: 下午1:37 <br>
 */
public class FuckSeleniumDownloader implements Downloader, Closeable {

    private volatile WebDriverPool webDriverPool;

    private Logger logger = Logger.getLogger(getClass());

    private int sleepTime = 1000;

    private int poolSize = 1;

    /**
     * 新建
     *
     * @param chromeDriverPath
     */
    public FuckSeleniumDownloader(String chromeDriverPath) {
        System.getProperties().setProperty("webdriver.chrome.driver", chromeDriverPath);
    }

    /**
     * set sleep time to wait until load success
     *
     * @param sleepTime
     * @return this
     */
    public FuckSeleniumDownloader setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }
    
    @Override
    public Page download(Request request, Task task) {
        checkInit();
        WebDriver webDriver;
        try {
            webDriver = webDriverPool.get();
        } catch (InterruptedException e) {
            logger.warn("interrupted", e);
            return null;
        }
        logger.info("downloading page " + request.getUrl());
        webDriver.get(request.getUrl());
        
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WebDriver.Options manage = webDriver.manage();
        
        
        Site site = task.getSite();
        if (site.getCookies() != null) {
            for (Map.Entry<String, String> cookieEntry : site.getCookies().entrySet()) {
                Cookie cookie = new Cookie(cookieEntry.getKey(), cookieEntry.getValue());
                manage.addCookie(cookie);
            }
        }
        
        
        WebElement webElement = webDriver.findElement(By.xpath("/html"));
        
        request.setUrl(webDriver.getCurrentUrl());
        
        String content = webElement.getAttribute("outerHTML");
        
        
        
        
        ComWebPage page = new ComWebPage();
        page.setRawText(content);
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        webDriverPool.returnToPool(webDriver);
        return page;
    }

    private void checkInit() {
        if (webDriverPool == null) {
            synchronized (this){
                webDriverPool = new WebDriverPool(poolSize);
            }
        }
    }

    @Override
    public void setThread(int thread) {
        this.poolSize = thread;
    }

    @Override
    public void close() throws IOException {
        webDriverPool.closeAll();
    }
}