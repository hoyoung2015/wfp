package net.hoyoung.wfp.spider;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.Sha2Crypt;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.io.FileUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.xmlbeans.impl.xb.xsdschema.impl.PublicImpl;
import org.junit.Test;

import net.hoyoung.wfp.core.utils.RedisUtil;
import net.hoyoung.wfp.spider.util.UserAgentUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.MyRedisScheduler;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.UrlUtils;

public class RedisTest {

	@Test
	public void test() {
		MyRedisScheduler redisScheduler = new MyRedisScheduler("127.0.0.1");
		TestProcessor processor = new TestProcessor();
		Spider.create(processor).setScheduler(redisScheduler).addUrl("http://www.hoyoung.net").thread(1).run();
		Jedis jedis = RedisUtil.getJedis();
		jedis.del("set_" + processor.getSite().getDomain());
	}

	static class TestProcessor implements PageProcessor {

		@Override
		public void process(Page page) {
			System.out.println(page.getHtml().$("title", "text").get());
			page.addTargetRequest("http://www.hoyoung.net/2017/02/10/squid3-proxy/");

		}

		private Site site = Site.me().setSleepTime(2000).setRetryTimes(3).setTimeOut(40000).setCycleRetryTimes(2)
				.addHeader("User-Agent", UserAgentUtil.getRandomAgent())
				.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
				.addHeader("Accept-Language", "zh-CN,zh;q=0.8").addHeader("Accept-Encoding", "gzip, deflate, sdch, br")
				.addHeader("Cache-Control", "max-age=0").addHeader("Upgrade-Insecure-Requests", "1");

		@Override
		public Site getSite() {
			return site;
		}

	}

	@Test
	public void testMulti() {
		JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "127.0.0.1");
		Jedis jedis = jedisPool.getResource();
		try {
			Transaction tx = jedis.multi();
			tx.set("a", "1");
			tx.lpush("b", "2");
			// System.out.println(1/0);
			// tx.exec();
		} finally {
			jedis.close();
		}

		jedisPool.close();
	}

	@Test
	public void testHtml() {
		String url = "http://www.faratronic.com/Upfile_pro/investor/201032685818979.doc";
		int i = url.lastIndexOf(".");
		String filename = url.substring(0, i);
		String ext = url.substring(i);
		System.out.println(String.format("%s \t %s", filename, ext));
	}

	@Test
	public void testSha2() {
		File file = new File("/Users/baidu/tmp/downloader/000060");
		for (String string : file.list()) {
			System.out.println(string);
		}
	}

	@Test
	public void testSha3() throws URIException, NullPointerException {
		String url = "http://www.salubris.cn/ch/news_detail.asp?typeid=2&typename=&id=286&name=信立泰携手中国心血管健康联盟共同打造ACS诊疗、预防、随访为一体的全程关爱项目";

		if (Pattern.matches("http(s?)://.*[\\u4e00-\\u9fa5]+.*", url)) {
			
		    String encodedUrl = new org.apache.commons.httpclient.URI(url,false,"utf-8").toString();
		    System.out.println(encodedUrl);
//			URL u = new URL(url);
//			String query = u.getQuery();
//			int port = u.getPort();
//			url = u.getProtocol()+"://" + u.getHost();
//			if (port > -1) {
//				url += ":" + port;
//			}
//			
////			query.split("&")
//			
//			url += u.getPath() + "?" + URLEncoder.encode(query, "utf-8");
//			System.out.println(url);

		}
		

	}
	
	@Test
	public void test3() {
		try {
			URI uri = new URI("http://hr.grgbanking.com/social/job/getJobListByCompany?workPlace=%E5%B9%BF%E5%B7%9E", false, "utf-8");
			System.out.println(uri.toString());
		} catch (URIException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
}
