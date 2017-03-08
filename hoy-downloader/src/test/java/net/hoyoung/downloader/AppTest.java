package net.hoyoung.downloader;

import java.util.concurrent.PriorityBlockingQueue;

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
    	PriorityBlockingQueue<Integer> queue = new PriorityBlockingQueue<>();
    	queue.put(2);
    	queue.put(5);
    	queue.put(1);
    	
    	System.out.println(queue.poll());
    	System.out.println(queue.poll());
    	System.out.println(queue.poll());
    	System.out.println(queue.poll());
    }
}
