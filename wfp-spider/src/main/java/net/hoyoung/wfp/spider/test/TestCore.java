package net.hoyoung.wfp.spider.test;

import net.hoyoung.wfp.core.utils.RedisUtil;
import redis.clients.jedis.Jedis;

public class TestCore {

	
	public static void main(String[] args) {
		
		Jedis jedis = RedisUtil.getJedis();
		jedis.set("a", "1");
		
	}
}
