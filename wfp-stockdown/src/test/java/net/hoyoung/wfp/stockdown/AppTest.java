package net.hoyoung.wfp.stockdown;

import net.hoyoung.wfp.core.utils.RegexUtils;
import net.hoyoung.wfp.core.utils.StringUtils;
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
//    	String s = StringUtils.addHttpHead("hoyoung.net/h.html");
//    	System.out.println(s);
    	
    	System.out.println(RegexUtils.checkURL("www.xxx-a.com?name=ggg"));
        assertTrue( true );
    }
}
