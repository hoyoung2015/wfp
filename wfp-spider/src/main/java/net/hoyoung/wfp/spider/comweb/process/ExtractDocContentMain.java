package net.hoyoung.wfp.spider.comweb.process;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import net.hoyoung.wfp.core.utils.EncryptUtil;
import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.spider.comweb.ComWebConstant;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;

public class ExtractDocContentMain {
	protected static Logger LOG = LoggerFactory.getLogger(ExtractDocContentMain.class);

	public static void main(String[] args) {
		String dbname = ComWebConstant.DB_NAME+"_test";
		String root = "/Users/baidu/tmp/compage/";
		List<String> collectionNames = MongoUtil.getCollectionNames(dbname, "\\d{6}$");
		for (String collectionName : collectionNames) {
			MongoCollection<Document> collection = MongoUtil.getCollection(dbname, collectionName);
			Bson filter = Filters.and(Filters.regex(ComPage.URL, "\\.(" + ComWebConstant.DOC_REGEX + ")$"),Filters.exists(ComPage.CONTENT,false));
			
			long count = collection.count(filter);
			if (count == 0) {
				continue;
			}
			LOG.info("process collection {},total {}", collectionName, count);
			MongoCursor<Document> iterator = collection.find(filter).projection(Projections.include(ComPage.URL))
					.iterator();
			try {
				while (iterator.hasNext()) {
					Document doc = iterator.next();
					String url = doc.getString(ComPage.URL);
					int i = url.lastIndexOf(".");
					String prefix = url.substring(0, i);
					String ext = url.substring(i);
					String sha1 = null;
					try {
						sha1 = EncryptUtil.encryptSha1(prefix).toLowerCase();
						
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
						continue;
					}
					String filePath = root+collectionName+File.separator+sha1+ext;
					File file = new File(filePath);
					if(!file.exists()){
						continue;
					}
					
				}
			} finally {
				iterator.close();
			}
			break;
		}
	}

}
