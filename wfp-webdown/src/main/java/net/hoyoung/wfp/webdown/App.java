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
	private static String REQ_URL = "http://www.baosteel.com/group/index.htm";
    public static void main( String[] args )
    {
    	Spider.create(new WfpPageProcessor())
//    	.setScheduler(new FileCacheQueueScheduler("E:\\huyang\\webfootprint\\urls"))
    	.setDownloader(new HtmlUnitDownloader())
    	.addPipeline(new WfpFilePipeline("E:\\huyang\\webfootprint\\downloads"))
    	.addUrl(REQ_URL).thread(1)
    	.run();
    	System.exit(0);
    }
}
