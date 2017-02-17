package net.hoyoung.wfp.patent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;

import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.core.utils.RedisUtil;
import net.hoyoung.wfp.patent.spider.PatentPipeline;
import net.hoyoung.wfp.patent.spider.PatientPageProcessor;
import redis.clients.jedis.Jedis;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Hello world!
 *
 */
public class PatentSpider {
	static Logger LOG = LoggerFactory.getLogger(PatentSpider.class);
	public static void main(String[] args) {
		MongoCollection<Document> collection = MongoUtil.getCollection(PatentConstant.DB_NAME,
				PatentConstant.COLLECTION_NAME);
		collection.createIndex(new Document(PatentPage.STOCK_CODE, 1).append(PatentPage.DETAIL_URL, 1),
				new IndexOptions().unique(true));

		String fullName = "日出东方太阳能股份有限公司";
		try {
			String url = "http://s.wanfangdata.com.cn/patent.aspx?q="
					+ URLEncoder.encode("专利权人:" + fullName, "UTF-8").toLowerCase() + "&f=top&p=1";
//			System.out.println(url);
			Request request = new Request(url);
			String stockCode = "111111";
			request.putExtra(PatentPage.STOCK_CODE, stockCode);
			// 设置代理
			List<String[]> proxies = Lists.newArrayList();
			proxies.add(new String[] { "hoyoung", "QWerASdf", "139.129.93.2", "8128" });// 杨鹏的阿里云
			proxies.add(new String[] { "hoyoung", "QWerASdf", "123.206.58.101", "8128" });// 我的腾讯云
			proxies.add(new String[] { "hoyoung", "QWerASdf", "182.61.20.189", "8128" });// 我的百度云，首月9.9
			proxies.add(new String[] { "hoyoung", "QWerASdf", "118.89.238.129", "8128" });// 余启林的腾讯学生机
			PageProcessor pageProcessor = new PatientPageProcessor();
			pageProcessor.getSite().setHttpProxyPool(proxies, false);
			Spider.create(pageProcessor).addPipeline(new PatentPipeline()).addRequest(request).thread(2).run();
			Jedis jedis = RedisUtil.getJedis();
			String json = null;
			while((json = jedis.lpop(PatentConstant.REDIS_KEY)) != null){
				Document document = JSON.parseObject(json, Document.class);
				try {
					collection.insertOne(document);
				} catch (MongoWriteException e) {
					LOG.info("duplicate key +" + stockCode + " " + document.getString(PatentPage.DETAIL_URL));
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}
}
