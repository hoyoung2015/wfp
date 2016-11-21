package net.hoyoung.wfp.envirorg.webmagic.scheduler;

import net.hoyoung.wfp.core.entity.ComHporg;
import net.hoyoung.wfp.core.entity.CompanyInfo;
import net.hoyoung.wfp.core.entity.Hporg;
import net.hoyoung.wfp.core.utils.HibernateUtils;
import net.hoyoung.wfp.envirorg.spider.HporgDistanceSpider;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Administrator on 2015/11/10.
 */
public class ComHporgScheduler implements Scheduler {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 如果超过并发限制，停一会儿可以可以接着调用接口
     * 当发现查过限制的时候，全局休息
     */
    public static volatile boolean isTrack = false;
    BlockingQueue<CompanyInfo> comQueue;
    CompanyInfo companyInfo;
    BlockingQueue<Hporg> hpQueue;
    Session session;

    public ComHporgScheduler() {
        comQueue = new LinkedBlockingQueue<CompanyInfo>();
        hpQueue = new LinkedBlockingQueue<Hporg>();
        session = HibernateUtils.openSession();
        List<String> list = (List<String>) session.createSQLQuery("SELECT distinct(stock_code) as stock_code FROM wfp.com_hporg where distance2 is null")
                .list();

        List coms = session.createQuery("select new CompanyInfo(stockCode,posX,posY) from CompanyInfo where stockCode in(:alist)")
                .setParameterList("alist", list)
                .list();
        comQueue.addAll(coms);
        logger.info("companies inject complete");
    }

    @Override
    public synchronized void push(Request request, Task task) {
        List<Hporg> hporgs = (List<Hporg>) request.getExtra("hporgs");

        try {
            for (Hporg hporg : hporgs) {
                hpQueue.put(hporg);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Request poll(Task task) {
        if (hpQueue.isEmpty()) {
            companyInfo = comQueue.poll();
            if (companyInfo == null) {
                logger.info("there is no company");
                return null;//没有企业了
            }

            //1:先查出hporg
            List<Integer> list = session.createCriteria(ComHporg.class)
                    .setProjection(Projections.projectionList()
                            .add(Property.forName("hporgId")))
                    .add(Restrictions.eq("stockCode", companyInfo.getStockCode()))
                    .list();//理论上不可能为空
            List<Hporg> hporgs = session.createQuery("select new Hporg(id,posX,posY) from Hporg where id in (:alist)")
                    .setParameterList("alist", list)
                    .list();
            hpQueue.addAll(hporgs);
            logger.info("new Hporg inject complete");
        }
        if (isTrack) {
            try {
                logger.info("Exceeding the limit, sleeps 15 seconds");
                for (int i = 0; i < 20; i++) {
                    logger.info("sleep " + (i + 1));
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isTrack = false;
        }

        String origins = companyInfo.getPosY() + "," + companyInfo.getPosX();
        StringBuffer destinations = new StringBuffer("");
        List<Hporg> hporgs = new ArrayList<Hporg>();
        for (int i = 0; i < hpQueue.size() && i < 5; i++) {
            Hporg hporg = hpQueue.poll();
            hporgs.add(hporg);
            if (i > 0) {
                destinations.append("%7C");
            }
            destinations.append(hporg.getPosY() + "," + hporg.getPosX());
        }
        String url = "http://api.map.baidu.com/direction/v1/routematrix?output=json&origins=" + origins + "&destinations=" + destinations.toString() + "&ak=" + HporgDistanceSpider.AK;

        Request req = new Request(url);
        req.putExtra("companyInfo", companyInfo);
        req.putExtra("hporgs", hporgs);
        return req;
    }
}
