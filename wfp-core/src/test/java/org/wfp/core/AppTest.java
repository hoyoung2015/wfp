package org.wfp.core;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import net.hoyoung.wfp.core.entity.ComOrg;
import net.hoyoung.wfp.core.utils.HibernateUtils;
import net.hoyoung.wfp.core.utils.Md5Util;
import net.hoyoung.wfp.core.utils.StringUtils;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.hibernate.Criteria;
import org.hibernate.Session;

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
//        Session session = HibernateUtils.openSession();

    }
}
