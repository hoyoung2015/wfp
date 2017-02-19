package net.hoyoung.wfp.spider.comweb.analyse;

import java.util.Collection;
import java.util.Set;

import org.bson.Document;

import com.google.common.collect.Sets;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.spider.comweb.ComWebConstant;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;
import us.codecraft.webmagic.utils.UrlUtils;

public class ComWebDomain {

	public static void main(String[] args) {
		MongoCollection<Document> collection = MongoUtil.getCollection(ComWebConstant.DB_NAME, ComWebConstant.COLLECTION_NAME_TMP);
		MongoCursor<Document> iterator = collection.find(Filters.eq(ComPage.STOCK_CODE, "600526"))
				.projection(Projections.include(ComPage.URL)).iterator();
		Set<String> set = Sets.newHashSet();
		
		try {
			int cnt = 0;
			while(iterator.hasNext()){
				Document document = iterator.next();
				System.out.println(++cnt);
				set.add(UrlUtils.getDomain(document.getString(ComPage.URL)));
			}
		} finally {
			iterator.close();
		}
		for (String s : set) {
			System.out.println(s);
		}
	}

}
