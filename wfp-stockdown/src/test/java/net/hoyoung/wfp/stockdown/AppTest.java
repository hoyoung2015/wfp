package net.hoyoung.wfp.stockdown;

import us.codecraft.webmagic.selector.SmartContentSelector;
import us.codecraft.webmagic.selector.XpathSelector;
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
    	String s = "<a href=jhy-8355.shtml target=\"_blank\">银行业（IV）</a>";
//    	System.out.println(new SmartContentSelector().select(s));
    	System.out.println(new XpathSelector("//a/text()").select(s).replaceAll(" ", ""));
        assertTrue( true );
    }
}
