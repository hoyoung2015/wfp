package net.hoyoung.wfp.weibo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

public class WeiboCrawler extends BreadthCrawler {


    static final int MAX_RETYR_TIME = 5;
	private int retryTime = 0;
	
	private String userAgent;
	private String cookie;

	private DBCollection dbCollection;
	
	public void setDbCollection(DBCollection dbCollection) {
		this.dbCollection = dbCollection;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public WeiboCrawler(String crawlPath, boolean autoParse) {
		super(crawlPath, autoParse);
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	@Override
	public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception {
		HttpRequest request = new HttpRequest(crawlDatum);
		request.setCookie(this.cookie);
		request.setUserAgent(this.userAgent);
		return request.getResponse();
	}

	
	@Override
	public void visit(Page page, CrawlDatums next) {
		HashMap<String, String> meta = page.getMetaData();
		Integer pageNum = Integer.valueOf(meta.get("page"));

		String json = page.getHtml();
		Integer ok = (Integer) JSONPath.compile("$.ok").eval(JSON.parseObject(json));
		String mod = (String) JSONPath.compile("$.cards[0].mod_type").eval(JSON.parseObject(json));

		LOG.info("Json:"+page.getHtml());
		if (ok == 1 && "mod/pagelist".equals(mod)) {
			if(this.retryTime>0){
				LOG.info("异常恢复");
				this.retryTime = 0;
			}
			LOG.info("准备提取数据...");
			/**
			 * 提取数据
			 */
			String listStr = JSONPath.compile("$.cards[0].card_group").eval(JSON.parseObject(json)).toString();
			List<HashMap> maps = JSON.parseArray(listStr, HashMap.class);
			List<DBObject> dbobjects = Lists.transform(maps, new Function<HashMap, DBObject>() {
				@Override
				public DBObject apply(HashMap map) {
					return new BasicDBObject(map);
				}
			});
			/**
			 * 入库
			 */
			dbCollection.insert(dbobjects);
			/**
			 * 继续下一页
			 */
			int nextPageNum = pageNum + 1;
			
			String nextUrl = page.getUrl().replaceAll("page=\\d+", "page="+nextPageNum);
//			String nextUrl = page.getUrl();
			/**
			 * 防止去重
			 */
			nextUrl = nextUrl.replaceAll("r=\\d+", "r="+new Date().getTime());
			try {
				next.add(new CrawlDatum(nextUrl).meta("page", nextPageNum+""));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			LOG.info("数据异常 - "+this.retryTime+",准备重试");
			if(this.retryTime++<MAX_RETYR_TIME){
				/**
				 * 替换时间戳防止去重
				 */
				next.add(new CrawlDatum(page.getUrl().replaceAll("r=\\d+", "r="+new Date().getTime())).meta("page", page.getMetaData().get("page")));
			}
		}
	}

	public static void main(String[] args) {
		WeiboCrawler crawler = null;
		try {
			crawler = new WeiboCrawler("weibo_crawler", false);
			crawler.setThreads(1);
			crawler.addSeed(new CrawlDatum(
					"http://m.weibo.cn/page/json?containerid=1005051746221281_-_WEIBO_SECOND_PROFILE_WEIBO&page=2"));
			crawler.start(100);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}
}