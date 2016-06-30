package net.hoyoung.wfp.searcher.test;

import net.hoyoung.wfp.core.entity.CompanyInfo;
import net.hoyoung.wfp.core.utils.HibernateUtils;
import net.hoyoung.wfp.searcher.vo.NewItem;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.Locale;

/**
 * Created by hoyoung on 16-6-5.
 */
public class TestSession {
    public static void main(String[] args) {
        Session session = HibernateUtils.getCurrentSession();
        session.beginTransaction();
//        CompanyInfo companyInfo = (CompanyInfo)session.get(CompanyInfo.class, "601857");
        Long count = (Long)session.createCriteria(NewItem.class)
                .setProjection(Projections.rowCount())
                .add(Restrictions.eq("stockCode", "100"))
                .add(Restrictions.eq("targetUrl", "20"))
                .uniqueResult();
        System.out.println((Long)count);
        session.getTransaction().commit();
//        System.out.println(companyInfo);
    }
}
