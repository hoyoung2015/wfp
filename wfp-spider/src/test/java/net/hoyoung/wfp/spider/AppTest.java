package net.hoyoung.wfp.spider;

import org.bson.Document;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Projections;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.spider.comweb.ComWebConstant;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	MongoCollection<Document> collection = MongoUtil.getCollection(ComWebConstant.DB_NAME,
				ComWebConstant.COLLECTION_NAME);
		MongoCollection<Document> collectionTmp = MongoUtil.getCollection(ComWebConstant.DB_NAME,
				ComWebConstant.COLLECTION_NAME_TMP);
		System.out.println(collection.getNamespace());
		System.out.println(collectionTmp.getNamespace());
		
		String stockCode = "600971";
		MongoCursor<Document> iterator = collectionTmp.find(Filters.eq(ComPage.STOCK_CODE, stockCode))
				.projection(Projections.exclude("_id")).iterator();
		try {
			while (iterator.hasNext()) {
				Document document = iterator.next();
				try {
					collection.insertOne(document);
				} catch (MongoWriteException e) {
					System.err.println("duplicate key +" + stockCode + " " + document.getString(ComPage.URL));
				}
			}
		} finally {
			iterator.close();
			collectionTmp.deleteMany(Filters.eq(ComPage.STOCK_CODE, stockCode));
			System.err.println("finish " + stockCode);
		}
    }
    public void testc(){
    	String index = MongoUtil.getCollection(ComWebConstant.DB_NAME, ComWebConstant.COLLECTION_NAME_TMP)
		.createIndex(Indexes.ascending(ComPage.STOCK_CODE, ComPage.URL), new IndexOptions().unique(true));
    	System.out.println(index);
    	MongoUtil.getCollection(ComWebConstant.DB_NAME, ComWebConstant.COLLECTION_NAME)
		.createIndex(Indexes.ascending(ComPage.STOCK_CODE, ComPage.URL), new IndexOptions().unique(true));
    }
}