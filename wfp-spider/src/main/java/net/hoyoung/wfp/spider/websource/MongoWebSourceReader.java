package net.hoyoung.wfp.spider.websource;

import java.util.List;

import org.bson.Document;

import com.beust.jcommander.internal.Lists;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Projections;

import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;
import net.hoyoung.wfp.spider.comweb.vo.ComVo;

public class MongoWebSourceReader implements WebSourceReader {

	private String dbName;
	private String collectionName;

	public MongoWebSourceReader(String dbName, String collectionName) {
		super();
		this.dbName = dbName;
		this.collectionName = collectionName;
	}

	@Override
	public List<ComVo> read() {
		List<ComVo> rs = Lists.newArrayList();
		MongoClient client = MongoUtil.getClient();
		MongoCollection<Document> collection = client.getDatabase(dbName).getCollection(collectionName);
		MongoCursor<Document> iterator = collection.find().projection(Projections.exclude("_id")).iterator();
		try {
			while (iterator.hasNext()) {
				Document document = iterator.next();
				ComVo vo = new ComVo(document.getString(ComPage.STOCK_CODE), document.getString("sname"),
						document.getString("webSite"), document.getInteger("sleepTime"));
				vo.setUserAgent(document.getString("userAgent"));
				rs.add(vo);
			}
		} finally {
			iterator.close();
		}
		return rs;
	}

	public static void main(String[] args) {

		MongoWebSourceReader reader = new MongoWebSourceReader("wfp", "web_source");
		List<ComVo> list = reader.read();
		for (ComVo vo : list) {
			System.out.println(vo);
		}
	}

}
