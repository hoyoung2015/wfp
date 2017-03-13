package net.hoyoung.wfp.spider.comweb;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.hoyoung.wfp.core.utils.WFPContext;
import net.hoyoung.wfp.core.utils.mail.MailUtil;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;

public class ComWebSpiderListener implements SpiderListener {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private Spider spider;
	private AtomicInteger error = new AtomicInteger(0);
	private static final int MAX_ERROR_NUM = 5;
	private volatile boolean isFail = false;

	public ComWebSpiderListener(Spider spider) {
		super();
		this.spider = spider;
	}

	@Override
	public void onSuccess(Request request) {
		error.set(0);
	}

	@Override
	public void onError(Request request) {
		Integer statusCode = (Integer) request.getExtra(Request.STATUS_CODE);
		if (404 == statusCode) {
			return;
		}
		if (500 == statusCode) {
			return;
		}
		if (403 == statusCode) {
			return;
		}

		int errorNow = error.incrementAndGet();
		logger.warn("{} failed {} time", request.getUrl(), errorNow);
		// 如果错误连续超过5次，则停止
		if (errorNow > MAX_ERROR_NUM) {
			processFailJob(request);
		}
	}

	public boolean isFail() {
		return isFail;
	}

	private synchronized void processFailJob(Request request) {
		if (isFail) {
			return;
		}
		// 停止爬虫
		spider.stop();
		new Thread(new Runnable() {
			@Override
			public void run() {
				String stockCode = (String) request.getExtra(ComPage.STOCK_CODE);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String domain = (String) request.getExtra("domain");
				try {
					MailUtil.sendMail(WFPContext.getProperty("spider.fail.receiver"),
							stockCode + " " + sdf.format(new Date()), domain);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (MessagingException e) {
					e.printStackTrace();
					logger.warn("send email fail");
				}
			}
		}).start();
		isFail = true;
	}

}
