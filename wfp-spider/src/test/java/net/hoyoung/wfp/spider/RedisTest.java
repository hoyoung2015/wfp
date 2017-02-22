package net.hoyoung.wfp.spider;

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

public class RedisTest {

	@Test
	public void test(){
		MyRedisScheduler redisScheduler = new MyRedisScheduler("127.0.0.1");
		TestProcessor processor = new TestProcessor();
		Spider.create(processor)
		.setScheduler(redisScheduler).addUrl("http://www.hoyoung.net").thread(1).run();
		Jedis jedis = RedisUtil.getJedis();
		jedis.del("set_"+processor.getSite().getDomain());
	}
	
	static class TestProcessor implements PageProcessor{

		@Override
		public void process(Page page) {
			System.out.println(page.getHtml().$("title","text").get());
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
	public void testMulti(){
		JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "127.0.0.1");
		Jedis jedis = jedisPool.getResource();
		try {
			Transaction tx = jedis.multi();
			tx.set("a", "1");
			tx.lpush("b", "2");
//			System.out.println(1/0);
//			tx.exec();
		} finally {
			jedis.close();
		}
		
		jedisPool.close();
	}
}
