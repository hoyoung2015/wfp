package net.hoyoung.wfp.weibo;

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

		if (ok == 1 && "mod/pagelist".equals(mod)) {
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
			String nextPageNum = pageNum + 1 + "";
			String nextUrl = page.getUrl().replaceAll("\\d+$", nextPageNum);
			try {
				next.add(new CrawlDatum(nextUrl).meta("page", nextPageNum));
			} catch (Exception e) {
				e.printStackTrace();
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