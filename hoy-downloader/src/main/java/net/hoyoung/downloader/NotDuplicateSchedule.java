package net.hoyoung.downloader;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.Scheduler;

public class NotDuplicateSchedule implements Scheduler {

	private BlockingQueue<Request> queue = new LinkedBlockingQueue<Request>();
	
	@Override
	public void push(Request request, Task task) {
		queue.add(request);
	}

	@Override
	public Request poll(Task task) {
		return queue.poll();
	}

}
