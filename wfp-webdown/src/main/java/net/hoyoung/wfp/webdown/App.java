package net.hoyoung.wfp.webdown;

import net.hoyoung.webmagic.downloader.htmlunit.HtmlUnitDownloader;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;

/**
 * Hello world!
 *
 */
public class App 
{
	private static String REQ_URL = "http://www.dfac.com/";
//	private static String REQ_URL = "http://192.168.21.190:8375/rimp/login.do";
//	private static String REQ_URL = "http://www.wisco.com.cn/wgxw2015/index.jhtml";
//	private static String REQ_URL = "http://www.sse.com.cn/assortment/stock/list/stockdetails/company/index.shtml?COMPANY_CODE=600000";
    public static void main( String[] args )
    {
    	System.out.println("haha");
    	Spider.create(new WfpPageProcessor())
    	.setScheduler(new FileCacheQueueScheduler("E:\\huyang\\webfootprint\\urls"))
    	.setDownloader(new HtmlUnitDownloader())
//    	.setDownloader(new SeleniumDownloader("E:\\huyang\\webfootprint\\chromedriver.exe"))
    	.addPipeline(new WfpFilePipeline("E:\\huyang\\webfootprint\\downloads"))
    	.addUrl(REQ_URL).thread(1)
    	.run();
    	System.exit(0);
    }
}
