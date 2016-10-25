package net.hoyoung.wfp.stockdown.spider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import net.hoyoung.wfp.core.entity.CompanyInfo;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

/**
 * 企业基础信息爬取主类 name, ename, register_date, area, addr_reg, addr_work, market,
 * listing_date, offer_date, stock_type, stock_code
 * 
 * @author hoyoung
 *
 */
@Component
public class CompanyInfoDetailSpiderPageProcessor implements PageProcessor {
	static Logger logger = LoggerFactory.getLogger(CompanyInfoDetailSpiderPageProcessor.class);
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void process(Page page) {
		String stock_code = (String) page.getRequest().getExtra("stock_code");
		if (stock_code == null) {
			logger.error("Request中必须携带stock_code");
			System.exit(1);
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<Selectable> tableSelector = page.getHtml().xpath("//table[@class='tab_xtable']").nodes();
		Selectable infoSelect = tableSelector.get(0);
		String name = infoSelect.xpath("/table/tbody/tr[3]/td[2]/text()").get();
		String ename = infoSelect.xpath("/table/tbody/tr[4]/td[2]/text()").get();
		String registerDateStr = infoSelect.xpath("/table/tbody/tr[6]/td[2]/a/text()").get();
		Date register_date = null;
		if (registerDateStr != null) {
			try {
				register_date = sdf.parse(registerDateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		String area = infoSelect.xpath("/table/tbody/tr[9]/td[2]/a/text()").get();
		// 工商信息
		Selectable gsSelect = tableSelector.get(1);
		String addr_reg = gsSelect.xpath("/table/tbody/tr[2]/td[2]/text()").get();
		String addr_work = gsSelect.xpath("/table/tbody/tr[4]/td[2]/text()").get();
		// 证券信息
		Selectable zqSelect = tableSelector.get(2);
		String market = zqSelect.xpath("/table/tbody/tr[3]/td[2]/a/text()").get();
		Date listing_date = null;

		String t_date = zqSelect.xpath("/table/tbody/tr[2]/td[2]/text()").get();
		if (!StringUtils.isEmpty(t_date)) {
			try {
				listing_date = sdf.parse(t_date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		Date offer_date = null;
		t_date = zqSelect.xpath("/table/tbody/tr[1]/td[2]/text()").get();
		if (!StringUtils.isEmpty(t_date)) {
			try {
				offer_date = sdf.parse(t_date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		String stock_type = zqSelect.xpath("/table/tbody/tr[4]/td[2]/text()").get();

		// 联系信息
		Selectable contactSelect = tableSelector.get(3);
		String webSite = contactSelect.xpath("/table/tbody/tr[4]/td[2]/a/@href").get();

		
		

		mongoTemplate.upsert(new Query(new Criteria("stockCode").is(stock_code)),
				new Update()
				.set("name", name)
				.set("ename", ename)
				.set("registerDate", register_date)
				.set("area", area)
				.set("addrReg", addr_reg)
				.set("addrWork", addr_work)
				.set("market", market)
				.set("listingDate", listing_date)
				.set("offerDate", offer_date)
				.set("webSite", webSite),CompanyInfo.class);
	}

	private Site site = Site.me().setRetryTimes(5).setSleepTime(1000).addHeader("Host", "stockdata.stock.hexun.com")
			.addHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");

	@Override
	public Site getSite() {
		return site;
	}

}
