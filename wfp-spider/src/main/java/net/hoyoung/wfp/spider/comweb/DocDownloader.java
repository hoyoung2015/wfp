package net.hoyoung.wfp.spider.comweb;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import net.hoyoung.wfp.core.utils.EncryptUtil;
import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;
import net.hoyoung.wfp.spider.downloader.BatchDownloadFile;
import net.hoyoung.wfp.spider.downloader.ContentType;
import net.hoyoung.wfp.spider.downloader.DownloadException;
import net.hoyoung.wfp.spider.downloader.DownloadInfo;

public class DocDownloader {
	protected static Logger LOG = LoggerFactory.getLogger(DocDownloader.class);

	public static void main(String[] args) {
//		String dbName = ComWebConstant.DB_NAME + "_test";
		String dbName = ComWebConstant.DB_NAME;
		List<String> collectionNames = MongoUtil.getCollectionNames(dbName, "\\d{6}");
		Pattern pattern = Pattern.compile("\\.(" + ComWebConstant.DOC_REGEX + ")$");

		for (String collectionName : collectionNames) {
			Map<ObjectId, String> map = Maps.newHashMap();
			MongoCollection<Document> collection = MongoUtil.getCollection(dbName, collectionName);
			Bson filter = Filters.regex(ComPage.URL, pattern);
			MongoCursor<Document> iterator = collection.find(filter).projection(Projections.include(ComPage.URL))
					.iterator();
			try {
				while (iterator.hasNext()) {
					Document document = iterator.next();
					map.put(document.getObjectId("_id"), document.getString(ComPage.URL));
				}
			} finally {
				iterator.close();
			}
			if (map.isEmpty()) {
				continue;
			}
			int total = map.size();
			int counter = 0;
			for (Entry<ObjectId, String> entry : map.entrySet()) {

				String url = entry.getValue();
				int i = url.lastIndexOf(".");
				String filename = url.substring(0, i);
				String ext = url.substring(i);
				DownloadInfo downloadInfo = new DownloadInfo(url);
				String dir = "/Users/baidu/tmp/downloader/" + collectionName;

				File dirFile = new File(dir);
				if (!dirFile.exists()) {
					dirFile.mkdirs();
				}
				long count = collection.count(filter);
				if (count == dirFile.list().length) {
					continue;
				}
				downloadInfo.setFilePath(dir);
				downloadInfo.addExcludeContentType(ContentType.PDF, ContentType.MSWORD);
				String sha1;
				try {
					sha1 = EncryptUtil.encryptSha1(filename);
					LOG.info("{}\t{}/{}\t{}\t{}", collectionName, ++counter, total, sha1, entry.getValue());
					String landingFile = sha1 + ext;

					if (new File(dir + File.separator + landingFile).exists()
							&& !new File(dir + File.separator + landingFile + ".position").exists()) {
						continue;
					}

					downloadInfo.setFileName(landingFile);
					// new Thread(new BatchDownloadFile(downloadInfo)).start();
					new BatchDownloadFile(downloadInfo).syncRun();
					TimeUnit.SECONDS.sleep(1);
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (DownloadException e) {
					collection.deleteOne(Filters.eq("_id", entry.getKey()));
				}

				// break;
			}
			// break;
		}
	}

}
