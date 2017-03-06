package net.hoyoung.wfp.spider.comweb.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.tika.Tika;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;

import net.hoyoung.wfp.core.utils.EncryptUtil;
import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.spider.comweb.ComWebConstant;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;

public class ExtractDocContentMain {
	protected static Logger LOG = LoggerFactory.getLogger(ExtractDocContentMain.class);
	private static Tika tika = new Tika();

	private static String parseDoc(File file) {
		String content = null;
		try {
			Reader reader = tika.parse(file);
			BufferedReader br = new BufferedReader(reader);
			String tmp = null;
			StringBuffer sb = new StringBuffer();
			while ((tmp = br.readLine()) != null) {
				if ("".equals(tmp) || Pattern.matches("\\s+", tmp)) {
					continue;
				}
				sb.append(tmp);
			}
			content = sb.toString();
		} catch (IOException e) {
			LOG.error("parse error {}", e);
		}
		return content;
	}

	public static void main(String[] args) {
		 String dbname = ComWebConstant.DB_NAME + "_test";
//		String dbname = ComWebConstant.DB_NAME;
		String root = "/Users/baidu/tmp/compage/";
		List<String> collectionNames = MongoUtil.getCollectionNames(dbname, "\\d{6}$");
		for (String collectionName : collectionNames) {
			MongoCollection<Document> collection = MongoUtil.getCollection(dbname, collectionName);
			Bson filter = Filters.and(Filters.regex(ComPage.URL, "\\.(" + ComWebConstant.DOC_REGEX + ")$"),
					Filters.exists(ComPage.CONTENT, false));

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
					String filePath = root + collectionName + File.separator + sha1 + ext;
					File file = new File(filePath);
					if (!file.exists()) {
						continue;
					}
					LOG.info("parse collection {},file {},url {}", collectionName, filePath, url);
					String content = parseDoc(file);
					if (content == null) {
						continue;
					}
					collection.updateOne(Filters.eq("_id", doc.getObjectId("_id")),
							Updates.set(ComPage.CONTENT, content));
				}
			} finally {
				iterator.close();
			}
			// break;
		}
	}

}
