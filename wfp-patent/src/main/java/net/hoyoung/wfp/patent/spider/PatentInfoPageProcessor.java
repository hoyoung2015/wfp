package net.hoyoung.wfp.patent.spider;

import java.util.concurrent.atomic.AtomicLong;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.patent.PatentConstant;
import net.hoyoung.wfp.patent.PatentPage;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class PatentInfoPageProcessor implements PageProcessor {
	Logger logger = LoggerFactory.getLogger(getClass());
	private AtomicLong counter;

	public PatentInfoPageProcessor(long total) {
		super();
		this.counter = new AtomicLong(total);
	}

	@Override
	public void process(Page page) {
		String appId = page.getHtml()
				.$("body > div.fixed-width-wrap.fixed-width-wrap-feild > div > div:nth-child(2) > span.text", "text")
				.get();
		String mainClassId = page.getHtml()
				.$("body > div.fixed-width-wrap.fixed-width-wrap-feild > div > div:nth-child(6) > span.text", "text")
				.get();
		String classId = page.getHtml()
				.$("body > div.fixed-width-wrap.fixed-width-wrap-feild > div > div:nth-child(7) > span.text", "text")
				.get();
		String intro = page.getHtml()
				.$("body > div.fixed-width.baseinfo.clear > div > div.baseinfo-feild.abstract > div > div", "text")
				.get();
		ObjectId _id = (ObjectId) page.getRequest().getExtra("_id");
		String stockCode = (String) page.getRequest().getExtra(PatentPage.STOCK_CODE);

		MongoCollection<Document> collection = MongoUtil.getCollection(PatentConstant.DB_NAME, stockCode);
		Bson updates = Updates.combine(Updates.set(PatentPage.MAIN_CLASS_ID, mainClassId),
				Updates.set(PatentPage.APP_ID, appId), Updates.set(PatentPage.CLASS_ID, classId),
				Updates.set(PatentPage.INTRO, intro));
		collection.updateOne(Filters.eq("_id", _id), updates);
		logger.info("{}\t{}", stockCode, counter.decrementAndGet());
	}

	private static final int SLEEP_TIME = 500;

	private Site site = Site.me().setSleepTime(SLEEP_TIME).setRetryTimes(3).setTimeOut(50000).setCycleRetryTimes(3)
			.addHeader("User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36")
			.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
			.addHeader("Accept-Language", "zh-CN,zh;q=0.8").addHeader("Accept-Encoding", "gzip, deflate, sdch, br")
			.addHeader("Cache-Control", "max-age=0");

	@Override
	public Site getSite() {
		return site;
	}

}
