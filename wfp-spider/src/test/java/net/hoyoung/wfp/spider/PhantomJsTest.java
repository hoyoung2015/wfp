package net.hoyoung.wfp.spider;

import java.nio.charset.Charset;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;

import net.hoyoung.wfp.spider.comweb.PhantomJsPageProcessor;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.PhantomJSDownloader;

public class PhantomJsTest {

	@Test
	public void test(){
		PhantomJsPageProcessor processor = new PhantomJsPageProcessor();
		
		List<String[]> proxies = Lists.newArrayList();
		proxies.add(new String[] { "", "", "115.159.92.73", "8128" });// 马晶苗的腾讯学生机
		PhantomJSDownloader downloader = new PhantomJSDownloader("/Users/baidu/local/phantomjs/bin/phantomjs");
		processor.getSite().setHttpProxyPool(proxies, false);
		Spider.create(processor).setDownloader(downloader).addUrl("http://1212.ip138.com/ic.asp").thread(1).run();
	}
	
	@Test
	public void test2(){
		String s = Hashing.md5().newHasher().putString("abc", Charset.defaultCharset()).hash().toString();
		System.out.println(s);
	}
	
}
