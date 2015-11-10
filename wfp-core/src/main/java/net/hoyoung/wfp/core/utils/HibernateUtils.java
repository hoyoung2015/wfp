package net.hoyoung.wfp.core.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class HibernateUtils {
	private static SessionFactory sessionFactory;
	public static SessionFactory getSessionFactory(){
		if(sessionFactory==null){
            synchronized (HibernateUtils.class){
                if(sessionFactory==null){
                    Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
                    ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
                    sessionFactory = configuration.buildSessionFactory(serviceRegistry);
                }
            }
		}
		return sessionFactory;
	}
	public static Session openSession(){
		if(sessionFactory==null){
			getSessionFactory();
		}
		return sessionFactory.openSession();
	}
    public static Session getCurrentSession(){
		if(sessionFactory==null){
			getSessionFactory();
		}
		return sessionFactory.getCurrentSession();
	}
    private static ThreadLocal<Session> session = new ThreadLocal<Session>();
    public static Session getLocalThreadSession() {
        Session s = session.get();// 获取当前线程下的SESSION
        if (s == null || !s.isOpen()) {
            s = null;
            s = getSessionFactory().openSession();// 获取当前线程中的SESSION， 需在在Hibernate.cfg.xml文件，具体请看面的说明
            session.set(s);// 将当前SESSION放入到当前线程的容器中保存
        }
        return s;
    }


    public static void closeSession() {
        Session s = (Session) session.get();// 获取当前线程下的SESSION
        if (s != null) {
            // s.close();//这里无需将Session关闭，因为该Session是保存在当前线程//中的，线程执行完毕Session自然会销毁
            session.set(null);// 将当前线程中的会话清除
        }
    }
}
