package net.hoyoung.wfp.searcher.baidu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import net.hoyoung.wfp.core.entity.CompanyInfo;
import net.hoyoung.wfp.core.utils.BaiduNewsTimeConverter;
import net.hoyoung.wfp.searcher.vo.NewItem;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

@Component
public class MongoDbSaveHandler implements SaveHandler {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private MongoTemplate mongoTemplate;

	public void save(SearchRequest searchRequest) {
		Html resultHtml = searchRequest.getHtml();
		List<NewItem> list = new ArrayList<NewItem>();
		// 开线程来保存
		Selectable pageSele = resultHtml.xpath("//*[@id=\"page\"]/strong/span[2]/text()");
		if (pageSele.get() != null) {
			logger.info("当前页码:" + pageSele);

		} else {
			logger.info("这里待定。。。。。。。。。。。。。。。。");
		}
		Selectable selectable = resultHtml.xpath("//div[@class='result']");
		List<Selectable> nodes = selectable.nodes();
		logger.info("获取" + nodes.size() + "条新闻");
		for (Selectable sele : nodes) {
			Selectable titleSele = sele.xpath("//h3[@class='c-title']/a");
			String title = titleSele.get().replaceAll("(</?em[^>]*>)|(</?a[^>]*>)", "");

			String targetUrl = titleSele.xpath("//*/@href").get();
			Selectable sumSele = sele.xpath("//div[@class='c-summary']");
			String sourceAndDate = sumSele.xpath("//p/text()").get();

			String[] ss = sourceAndDate.split("\\u00A0\\u00A0");
			String sourceName = ss[0];
			logger.info(sourceName + ":" + title);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
			Date publishDate = null;
			NewItem newItem = new NewItem();
			if (ss != null && ss.length > 1) {
				try {
					publishDate = sdf.parse(ss[1]);
					newItem.setPublishDate(publishDate);
				} catch (ParseException e) {
					logger.info("解析发布时间出错，尝试进行日期转换");
					publishDate = BaiduNewsTimeConverter.convert(ss[1], new Date());
					if(publishDate!=null){
						newItem.setPublishDate(publishDate);
					}else{
						logger.info(e.getMessage());
						logger.info("转换日期出错，存储原始值");
						newItem.setPublishDateStr(ss[1]);
					}
				}
			}

			String summary = sumSele.get().replaceAll("(<span.*/span>)|(<p.*/p>)|(</?div[^>]*>)|(</?em[^>]*>)", "");

			newItem.setCreateDate(new Date());

			newItem.setSourceName(sourceName);
			newItem.setTargetUrl(targetUrl);
			newItem.setTitle(title);
			newItem.setSummary(summary);

			newItem.setKeyword((String) searchRequest.getExtra("keyword"));

			CompanyInfo companyInfo = (CompanyInfo) searchRequest.getExtra("company");

			newItem.setStockCode(companyInfo.getStockCode());
			newItem.setQuery(searchRequest.getQuery());

			//
			/**
			 * 检查是否重复 1.stock_code一样 2.target_url一样
			 */
			long count = mongoTemplate
					.count(new Query().addCriteria(new Criteria("stockCode").is(newItem.getStockCode()))
							.addCriteria(new Criteria("targetUrl").is(newItem.getTargetUrl())), NewItem.class);
			if (count > 0) {
				logger.info(newItem + "已存在");
				continue;
			}
			newItem.setId(Long.toString(new Date().getTime())+"_"+Math.round(Math.random()*10000));
			list.add(newItem);
		}
		mongoTemplate.insertAll(list);
	}

}