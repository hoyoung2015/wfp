package net.hoyoung.wfp.core.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import redis.clients.jedis.Jedis;

public class RedisUtil {

	private static final ThreadLocal<Jedis> localJedis = new ThreadLocal<>();

	public static Jedis getJedis() {
		Jedis jedis = localJedis.get();
		if (jedis == null) {
			jedis = new Jedis(WFPContext.getProperty("redis.host"), WFPContext.getProperty("redis.port", Integer.class));
			localJedis.set(jedis);
		}
		if(!jedis.isConnected()){
			jedis.connect();
		}
		return jedis;
	}

	public static void main(String[] args) {
		ExecutorService threadPool = Executors.newFixedThreadPool(12);
		for (int i = 0; i < 20; i++) {
			threadPool.execute(new Runnable() {
				
				@Override
				public void run() {
					Jedis jedis = RedisUtil.getJedis();
					System.out.println(jedis.hashCode());
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
		threadPool.shutdown();
	}
}
