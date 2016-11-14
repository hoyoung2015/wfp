package net.hoyoung.wfp.searcher;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.WriteResult;

import net.hoyoung.wfp.core.entity.CompanyInfo;

public class MongoQueryTest extends BaseTest {

	@Autowired
	MongoTemplate mongoTemplate;

	@Test
	public void test() {
		// mongoTemplate.find(new Query(new Criteria().is))
		CompanyInfo c = new CompanyInfo();
		c.setStockCode("666677");
		c.setAddr("this is addr2");
		c.setCreateDate(new Date());
		c.setName("长江七号");
		mongoTemplate.insert(c);
	}

	@Test
	public void test2() {
		List<CompanyInfo> list = mongoTemplate.find(new Query(new Criteria("name").is(null)), CompanyInfo.class);
		for (CompanyInfo companyInfo : list) {
			System.out.println(ToStringBuilder.reflectionToString(companyInfo));
		}
	}

	@Test
	public void test3() {
		CompanyInfo c = new CompanyInfo();
		c.setStockCode("666666");
		WriteResult rs = mongoTemplate.updateFirst(new Query(new Criteria("stockCode").is(c.getStockCode())),
				new Update().set("name", "小狗"), CompanyInfo.class);

	}
	@Test
	public void test6() {
		CompanyInfo c = new CompanyInfo();
		c.setStockCode("002594");
		CompanyInfo t = mongoTemplate.findOne(new Query().addCriteria(new Criteria("stockCode").is(c.getStockCode())), CompanyInfo.class);

		System.out.println(ToStringBuilder.reflectionToString(t));
	}
}
