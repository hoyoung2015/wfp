package net.hoyoung.wfp.envirorg.spider;

import com.jayway.jsonpath.InvalidPathException;
import net.hoyoung.wfp.core.entity.ComHporg;
import net.hoyoung.wfp.core.entity.CompanyInfo;
import net.hoyoung.wfp.core.entity.Hporg;
import net.hoyoung.wfp.core.utils.HibernateUtils;
import net.hoyoung.wfp.envirorg.webmagic.downloader.MyHttpClientDownloader;
import net.hoyoung.wfp.envirorg.webmagic.scheduler.ComHporgScheduler;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

/**
 * Created by Administrator on 2015/11/10.
 */
public class HporgDistanceSpider implements PageProcessor {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    public static final String AK = "i39t59l7L6nzXlOZCfzwUFsK";
//    public static final String AK = "lfLs8Vvqo7LL1CoLnojXR81E";
    @Override
    public void process(Page page) {



        Session session = HibernateUtils.getLocalThreadSession();
        session.beginTransaction();
        try{

            List<String> distanceList = page.getJson().jsonPath("$.result.elements[*].distance.value").all();
            CompanyInfo companyInfo = (CompanyInfo) page.getRequest().getExtra("companyInfo");
            List<Hporg> hporgs = (List<Hporg>) page.getRequest().getExtra("hporgs");



            for (int i=0;i<distanceList.size();i++){
                session.createQuery("update ComHporg set distance2=? where stockCode=? and hporgId=?")
                        .setParameter(0,Float.valueOf(distanceList.get(i)))
                        .setParameter(1,companyInfo.getStockCode())
                        .setParameter(2,hporgs.get(i).getId())
                        .executeUpdate();
            }

        }catch (InvalidPathException e){
            e.printStackTrace();
            logger.error(page.getRawText());
            if(!ComHporgScheduler.isTrack){
                ComHporgScheduler.isTrack = true;//通知调度器暂停
            }
            //重新加入队列
//            Request req = new Request(page.getRequest().getUrl());
//            req.putExtra("companyInfo",page.getRequest().getExtra("companyInfo"));
//            req.putExtra("hporgs",page.getRequest().getExtra("hporgs"));
//            page.addTargetRequest(req);
        }finally {
            if(session!=null && session.isOpen()){
                session.getTransaction().commit();
            }
            HibernateUtils.closeSession();
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
    private Site site = Site.me()
            .setCharset("GBK")
            .setRetryTimes(3)
            .setSleepTime(300)
//            .addHeader("Host","api.map.baidu.com")
            .addHeader(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");


    public static void main(String[] args) {
        Spider.create(new HporgDistanceSpider())
//                .setDownloader(new MyHttpClientDownloader())
                .setScheduler(new ComHporgScheduler())
                .thread(8)
                .run();
    }
}
