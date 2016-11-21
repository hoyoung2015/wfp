package net.hoyoung.wfp.searcher.parse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.mongodb.WriteResult;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import net.hoyoung.wfp.searcher.vo.NewItem;

@Component
public class ContentParser extends BreadthCrawler {
	
	public ContentParser() {
		super("news_parser", false);
	}
	
	public ContentParser(String crawlPath, boolean autoParse) {
		super(crawlPath, autoParse);
	}

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private MongoTemplate mongoTemplate;
	@Override
	public void visit(Page page, CrawlDatums next) {
		try {
			String content = ContentExtractor.getContentByHtml(page.getHtml());
			String stockCode = page.meta("stockCode");
			String targetUrl = page.getCrawlDatum().getUrl();
			WriteResult rs = mongoTemplate.updateMulti(new Query()
					.addCriteria(new Criteria("stockCode").is(stockCode))
					.addCriteria(new Criteria("targetUrl").is(targetUrl)), 
					new Update()
					.set("content", content), NewItem.class);
			logger.info("更新了"+rs.getN()+"个文档");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
