package net.hoyoung.wfp.spider.comweb.process;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.spider.comweb.ComWebConstant;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;
import net.hoyoung.wfp.spider.comweb.urlfilter.DomainUrlFilter;
import us.codecraft.webmagic.utils.UrlUtils;

public class StaticUrlFilter {

	public static void main(String[] args) {
		MongoDatabase db = MongoUtil.getClient().getDatabase(ComWebConstant.DB_NAME);
		List<String> collectionNames = Lists.newArrayList();

		MongoCursor<String> iterator = db.listCollectionNames().iterator();
		try {
			while (iterator.hasNext()) {
				String name = iterator.next();
				if (Pattern.matches("\\d{6}", name)) {
					collectionNames.add(name);
				}
			}
		} finally {
			iterator.close();
		}
		if (CollectionUtils.isEmpty(collectionNames)) {
			return;
		}

		Map<String, String> stockCode2Domain = Maps.newHashMap();

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(
					new InputStreamReader(new FileInputStream("src/main/shell/bin/web_source.txt")));
			String tmp = null;
			while ((tmp = reader.readLine()) != null) {
				tmp = tmp.trim();
				if (tmp.startsWith("#") || "".equals(tmp)) {
					continue;
				}
				stockCode2Domain.put(tmp.split("\t")[0], UrlUtils.getDomain(tmp.split("\t")[2]).replaceAll("^www\\.", ""));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		DomainUrlFilter urlFilter = new DomainUrlFilter();
		int cnt = 0;
		for (String name : collectionNames) {
			if(cnt > 1000){
				break;
			}
			name = "300088";
			String domain = stockCode2Domain.get(name);
			System.out.println(String.format("%d\tstart to process %s[%s]", ++cnt, name, domain));
			if (domain == null) {
				continue;
			}
			name += "_tmp";
			MongoCollection<Document> collection = MongoUtil.getCollection(ComWebConstant.DB_NAME, name);
			List<ObjectId> oids = Lists.newArrayList();
			MongoCursor<Document> docIte = collection.find().projection(Projections.include(ComPage.URL)).iterator();

			try {
				while (docIte.hasNext()) {
					Document doc = docIte.next();
					String url = doc.getString(ComPage.URL);
					
					if (urlFilter.accept(domain, url) == false && Pattern.matches(".+\\.(" + ComWebConstant.DOC_REGEX + ")$", url) == false) {
						System.out.println(doc.getString(ComPage.URL));
						oids.add(doc.getObjectId("_id"));
					}
				}
			} finally {
				docIte.close();
			}
			if(CollectionUtils.isNotEmpty(oids)){
				collection.deleteMany(Filters.in("_id", oids));
			}
			break;
		}
	}
}
