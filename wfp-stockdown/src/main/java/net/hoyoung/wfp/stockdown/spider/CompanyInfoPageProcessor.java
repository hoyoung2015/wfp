package net.hoyoung.wfp.stockdown.spider;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;

import net.hoyoung.wfp.core.entity.CompanyInfo;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.XpathSelector;

/**
 * 企业基础信息爬取主类
 * 
 * @author hoyoung
 *
 */
@Component
public class CompanyInfoPageProcessor implements PageProcessor {
	public static Logger logger = LoggerFactory.getLogger(CompanyInfoPageProcessor.class);
	// 1-141
	public static String JSON_LIST_URL = "http://stockdata.stock.hexun.com/gszl/data/jsondata/jbgk.ashx?count=20&page=";

	public static int PAGE_START = 1;
	public static int PAGE_END = 146;// 141

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void process(Page page) {
		String jsonStr = page.getRawText();
		jsonStr = jsonStr.substring(14, jsonStr.length() - 1);
		String listStr = JSONPath.compile("$.list").eval(JSON.parseObject(jsonStr)).toString();
		List<JSONObject> list = JSON.parseArray(listStr, JSONObject.class);
		for (JSONObject s : list) {
			CompanyInfo c = new CompanyInfo();
			String Stockname = JSONPath.compile("$.Stockname").eval(s).toString();
			int t = Stockname.indexOf("(");
			String sname = Stockname.substring(0, t);
			String stock_code = Stockname.substring(t + 1, Stockname.length() - 1);
			c.setSname(sname);
			c.setStockCode(stock_code);
			String industry = new XpathSelector("//a/text()").select(JSONPath.compile("$.deviation").eval(s).toString()).replaceAll(" ", "");
			c.setIndustry(industry);
			String t_institutional = JSONPath.compile("$.Institutional").eval(s).toString();// 注册资本
			double institutional = 0;
			try {
				institutional = Double.parseDouble(t_institutional);
			} catch (NumberFormatException e) {
				institutional = 0;
			}
			c.setInstitutional(institutional);
			float lootchips = getFloat(JSONPath.compile("$.lootchips").eval(s).toString());
			float pricelimit = getFloat(JSONPath.compile("$.Pricelimit").eval(s).toString());
			float shareholders = getFloat(JSONPath.compile("$.shareholders").eval(s).toString());
			c.setLootchips(Double.valueOf(Float.toString(lootchips)));
			c.setPricelimit(Double.valueOf(Float.toString(pricelimit)));
			c.setShareholders(Double.valueOf(Float.toString(shareholders)));
//			System.out.println(ToStringBuilder.reflectionToString(c));
			
			long count = mongoTemplate.count(new Query(new Criteria("stockCode").is(c.getStockCode())), CompanyInfo.class);
			if(count==0){
				mongoTemplate.insert(c);
			}
		}
	}

	private Site site = Site.me().setRetryTimes(5).setSleepTime(1000).addHeader("Host", "stockdata.stock.hexun.com")
			.addHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");

	@Override
	public Site getSite() {
		return site;
	}

	private float getFloat(String s) {
		float r;
		try {
			r = Float.parseFloat(s);
		} catch (NumberFormatException e) {
			r = 0f;
		}
		return r;
	}
}
