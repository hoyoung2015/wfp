package net.hoyoung.wfp.searcher.baidu;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import net.hoyoung.wfp.core.entity.CompanyInfo;
import net.hoyoung.wfp.searcher.vo.NewItem;

/**
 * Created by v_huyang01 on 2016/6/13.
 */
@Component
public class BaiduNewsSpider {
	static Logger logger = LoggerFactory.getLogger(BaiduNewsSpider.class);
	private static final int OK = 0;
	private static final int WRONG_ARGS = 1;
	private static final int STOCK_CODE_NOT_EXISTS = 2;
	private static final int NEWS_HAS_CRAWLED = 3;

	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	private SaveHandler saveHandler;
	
	public int fetch(String stockCode, String keyword) {
		if (StringUtils.isEmpty(stockCode) || StringUtils.isEmpty(keyword)) {
			return WRONG_ARGS;
		}
		// 创建存储器
		Searcher searcher = new Searcher(saveHandler);

		CompanyInfo companyInfo = mongoTemplate.findOne(new Query(new Criteria("stockCode").is(stockCode)), CompanyInfo.class);
		if (companyInfo == null) {
			logger.warn("股票号　" + stockCode + "　不存在");
			return STOCK_CODE_NOT_EXISTS;
		}
		SearchRequest sr = new SearchRequest();
		sr.putExtra("company", companyInfo);
		sr.putExtra("keyword", keyword);
		sr.addKeyword("+\"" + keyword+"\"")// 加号表示这个关键词一定出现
				.addKeyword("\"" + companyInfo.getSname().replaceAll("[AB]$", "") + "\"");// 加上双引号避免被分词
		/**
		 * 校验股票号+关键词是否已经搜索
		 */
		long count = mongoTemplate.count(new Query(new Criteria("stockCode").is(stockCode))
				.addCriteria(new Criteria("keyword").is(keyword)), NewItem.class);
		if (count > 0) {
			logger.info(sr.getQuery() + " 已经采集过");
			return NEWS_HAS_CRAWLED;
		}
		searcher.setSearchRequest(sr);
		searcher.run();
		searcher.close();
		return OK;
	}
}
