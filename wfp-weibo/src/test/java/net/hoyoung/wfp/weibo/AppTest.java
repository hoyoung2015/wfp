package net.hoyoung.wfp.weibo;


import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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
    	String url = "apple=232&page=88";
    	System.out.println(url.replaceAll("\\d+$", "2"));
    }
    public void testApp2()
    {
    	String json = "{\"ok\":1,\"count\":5053,\"cards\":[{\"mod_type\":\"mod/empty\",\"msg\":\"没有内容\"}]}";
    	
    	String o = (String) JSONPath.compile("$.cards[0].mod_type").eval(JSON.parseObject(json));
		System.out.println(o);
    }
    
    public void testApp3()
    {
    	
    }
    
}
