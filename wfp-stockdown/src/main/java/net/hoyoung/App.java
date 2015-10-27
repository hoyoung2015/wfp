package net.hoyoung;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by hoyoung on 2015/10/24.
 */
public class App implements Runnable{
    BlockingDeque<String> queue;
    public App() {
        queue = new LinkedBlockingDeque<String>();
        try {
            queue.add("hello");
                queue.put("tomcat");
                queue.put("jetty");
                queue.put("java");
                queue.put("cpp");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            System.out.println(">>");
            System.out.println(queue.take());
            System.out.println(queue.size());
            System.out.println("----------");System.out.println(">>");
            System.out.println(queue.take());
            System.out.println(queue.size());
            System.out.println("----------");System.out.println(">>");
            System.out.println(queue.take());
            System.out.println(queue.size());
            System.out.println("----------");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        new Thread(new App()).start();
    }


}
