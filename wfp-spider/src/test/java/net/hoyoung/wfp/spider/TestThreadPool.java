package net.hoyoung.wfp.spider;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class TestThreadPool {

	@Test
	public void test() {
		LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 3, 3, TimeUnit.SECONDS, queue);
		for (int i = 0; i < 20; i++) {
			executor.execute(new MyRunable(i));
		}
		executor.shutdown();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					TimeUnit.SECONDS.sleep(6);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				queue.clear();
			}
		}).start();;
		
		while (executor.isTerminated()==false) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("over");
	}
	static class MyRunable implements Runnable{

		private int index;
		
		public MyRunable(int index) {
			super();
			this.index = index;
		}

		@Override
		public void run() {
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(index);
		}
		
	}
}
