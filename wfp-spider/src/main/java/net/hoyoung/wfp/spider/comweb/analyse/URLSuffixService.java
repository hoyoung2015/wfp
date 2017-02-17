package net.hoyoung.wfp.spider.comweb.analyse;

import java.util.HashSet;
import java.util.Set;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Projections;

import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.spider.comweb.ComWebConstant;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;

public class URLSuffixService {
	public static void main(String[] args) {
		MongoCollection<Document> collection = MongoUtil.getCollection(ComWebConstant.DB_NAME,
				ComWebConstant.COLLECTION_NAME);
		long total = collection.count();
		/**
		 * content不存在 html存在
		 */

		MongoCursor<Document> iterator = collection.find().projection(Projections.include("url")).iterator();
		try {
			Set<String> set = new HashSet<>();

			while (iterator.hasNext()) {
				Document document = iterator.next();
				String url = document.getString(ComPage.URL);
//				if(url.contains("Object")){
//					System.out.println(url);
//					break;
//				}
				int i = url.lastIndexOf(".");
				if (i < 0) {
					continue;
				}
				url = url.substring(i);
				int i2 = url.indexOf("?");
				if (i2 > -1) {
					url = url.substring(0, i2);
				}
				int i3 = url.indexOf("/");
				if (i3 > -1) {
					url = url.substring(0, i3);
				}
				int i4 = url.indexOf("&");
				if (i4 > -1) {
					url = url.substring(0, i4);
				}
				set.add(url);
//				System.out.println(total--);
			}
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();
			for (String s : set) {
				System.out.println(s);
			}
		} finally {
			iterator.close();
		}

	}
}
