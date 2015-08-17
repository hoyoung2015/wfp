package net.hoyoung.wfp.webdown;

import net.hoyoung.webmagic.downloader.htmlunit.HtmlUnitDownloader;
import us.codecraft.webmagic.Spider;

/**
 * Hello world!
 *
 */
public class App {
	private static String REQ_URL = "http://www.baosteel.com/group/index.htm";
	private static String SAVE_PATH = "E:\\data_mining\\wfp";

	public static void main(String[] args) {
		Spider.create(new WfpPageProcessor())
				// .setScheduler(new
				// FileCacheQueueScheduler("E:\\huyang\\webfootprint\\urls"))
				.setDownloader(new HtmlUnitDownloader())
				.addPipeline(new WfpFilePipeline(SAVE_PATH)).addUrl(REQ_URL)
				.thread(5).run();
		System.exit(0);
	}
}
