package net.hoyoung.wfp.envirorg.spider;

import net.hoyoung.wfp.core.entity.ComHporg;
import net.hoyoung.wfp.core.entity.CompanyInfo;
import net.hoyoung.wfp.core.entity.Hporg;
import net.hoyoung.wfp.core.utils.HibernateUtils;
import net.hoyoung.wfp.envirorg.webmagic.scheduler.ComHporgScheduler;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

/**
 * Created by Administrator on 2015/11/10.
 */
public class HporgDistanceSpider implements PageProcessor {
    public static final String AK = "i39t59l7L6nzXlOZCfzwUFsK";
    @Override
    public void process(Page page) {
        Session session = HibernateUtils.getLocalThreadSession();

        List<String> distanceList = page.getJson().jsonPath("$.result.elements[*].distance.value").all();
        CompanyInfo companyInfo = (CompanyInfo) page.getRequest().getExtra("companyInfo");
        List<Hporg> hporgs = (List<Hporg>) page.getRequest().getExtra("hporgs");
        session.beginTransaction();
        for (int i=0;i<distanceList.size();i++){
            ComHporg comHporg = (ComHporg) session.createCriteria(ComHporg.class)
                    .add(Restrictions.eq("stockCode", companyInfo.getStockCode()))
                    .add(Restrictions.eq("hporgId",hporgs.get(i).getId()))
                    .uniqueResult();
            comHporg.setDistance2(Float.valueOf(distanceList.get(i)));
            System.err.println(comHporg);
        }
        session.getTransaction().commit();
        HibernateUtils.closeSession();
    }

    @Override
    public Site getSite() {
        return site;
    }
    private Site site = Site.me()
            .setRetryTimes(5)
            .setSleepTime(300)
            .addHeader(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");


    public static void main(String[] args) {
        Spider.create(new HporgDistanceSpider())
                .setScheduler(new ComHporgScheduler())
                .thread(5)
                .run();
    }
}