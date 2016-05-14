package net.hoyoung.wfp.stockdown.greenorg;

import net.hoyoung.wfp.core.entity.ComOrg;
import net.hoyoung.wfp.core.utils.HibernateUtils;
import net.hoyoung.wfp.core.utils.JDBCHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by hoyoung on 2015/10/28.
 */
public class BaiduRouteSpiderMultiThread2 implements PageProcessor{
    protected Logger logger = LoggerFactory.getLogger(getClass());
    BlockingQueue<ComOrg> comOrgQueue = new LinkedBlockingQueue<ComOrg>();

    @Override
    public void process(Page page) {

    }

    private synchronized Request generateReq(){
        if(comOrgQueue.isEmpty()){//队列是空的
            Session session = HibernateUtils.getCurrentSession();
            session.beginTransaction();
            List<ComOrg> list = session
                    .createQuery("from ComOrg where distance2=0 group by stockCode,greenOrgId")
                    .setFirstResult(0)
                    .setMaxResults(20)
                    .list();
            session.getTransaction().commit();
            comOrgQueue.addAll(list);
        }
        for (int i=0;i<5&&i<comOrgQueue.size();i++){

        }


        return null;
    }


    private Site site = Site.me()
            .setRetryTimes(5)
            .setSleepTime(100)
            .addHeader(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");
    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider spider;
        new BaiduRouteSpiderMultiThread2().generateReq();
    }
}
