package net.hoyoung.wfp.core;

import net.hoyoung.wfp.core.utils.HibernateUtils;
import org.hibernate.Session;

/**
 * Created by Administrator on 2015/10/23.
 */
public class Test {
    public static void main(String[] args) {
        Session session = HibernateUtils.openSession();
    }
    public static class MyRunnable implements Runnable{

        @Override
        public void run() {

        }
    }
}
