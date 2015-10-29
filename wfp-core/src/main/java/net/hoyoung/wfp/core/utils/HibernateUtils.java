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
}
