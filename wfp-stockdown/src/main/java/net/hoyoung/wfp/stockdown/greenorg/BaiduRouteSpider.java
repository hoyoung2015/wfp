package net.hoyoung.wfp.stockdown.greenorg;

import net.hoyoung.wfp.core.utils.JDBCHelper;
import org.springframework.jdbc.core.JdbcTemplate;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Administrator on 2015/10/27.
 */
public class BaiduRouteSpider implements PageProcessor{
    static String AK = "i39t59l7L6nzXlOZCfzwUFsK";
    JdbcTemplate jdbcTemplate;
    BlockingDeque<Map<String, Object>> comQueue = new LinkedBlockingDeque<Map<String, Object>>();
    BlockingDeque<Map<String,Object>> orgQueue = new LinkedBlockingDeque<Map<String, Object>>();
    Map<String, Object> currentCom;
    Map<String, Object> currentOrg;
    public BaiduRouteSpider() {
        jdbcTemplate = JDBCHelper.createMysqlTemplate("mysql1",
                "jdbc:mysql://localhost/wfp?useUnicode=true&characterEncoding=utf8",
                "root", "", 5, 30);
        //加载企业队列
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select stock_code,sname,area,pos_x as lng,pos_y as lat from company_info where stock_code in (select stock_code from com_org group by stock_code)");
        System.out.println("load all stock_code");
        for (Map<String, Object> map : list){
            comQueue.add(map);
        }
        System.out.println("stock_code size:" + comQueue.size());
    }
    private void injectOrgQueue(String stockCode){
        if(!orgQueue.isEmpty()) return;

        List<Map<String, Object>> list = jdbcTemplate.queryForList("select id as org_id,lng,lat from green_org where id in (select green_org_id from com_org where stock_code=? and distance2=0)", stockCode);
        try {
            for (Map<String, Object> map:list){
                orgQueue.put(map);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    public Request genereteReq(){
        if(currentCom==null){//初始状态
            if(comQueue.isEmpty()) return null;
            try {
                currentCom = comQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //注入orgQueue
            this.injectOrgQueue((String) currentCom.get("stock_code"));
        }
        //现在currentCom非空
        /**
         * 如果org队列空了，那么换下一个企业的org
         * 下一个企业的org可能已经爬过，查出的org队列还是空的，就接着换下一个
         */
        while(orgQueue.isEmpty()){//组织队列空了，取下一个企业
            System.out.println("inject orgQueue");
            if(comQueue.isEmpty()){
                System.out.println("comQueue is empty,program is shutdown");
                return null;
            }
            try {
                currentCom = comQueue.take();
                this.injectOrgQueue((String) currentCom.get("stock_code"));
                //这时候组织队列可能还是空的
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //现在是一般情况
        //一次取5个
        int size = orgQueue.size();
        String origins = currentCom.get("lat")+","+currentCom.get("lng");
        StringBuffer destinations = new StringBuffer("");
        List<Integer> orgIds = new ArrayList<Integer>();
        try {
            for(int i=0;i < 5 && i < size;i++){
                currentOrg = orgQueue.take();
                orgIds.add((Integer) currentOrg.get("org_id"));
                if(i>0){
                    destinations.append("%7C");
                }
                destinations.append(currentOrg.get("lat")+","+currentOrg.get("lng"));
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        String url = "http://api.map.baidu.com/direction/v1/routematrix?output=json&origins="+origins+"&destinations="+destinations.toString()+"&ak="+AK;
//        String url = "http://api.map.baidu.com/direction/v1/routematrix?output=json&origins=%E5%A4%A9%E5%AE%89%E9%97%A8%7C%E9%B8%9F%E5%B7%A2&destinations=%E5%8C%97%E4%BA%AC%E5%A4%A7%E5%AD%A6%7C%E4%B8%9C%E6%96%B9%E6%98%8E%E7%8F%A0&ak="+AK;

        Request req = new Request(url);
        req.putExtra("stock_code",currentCom.get("stock_code"));
        req.putExtra("orgIds",orgIds);
        return req;
    }
    int count = 1;
    AtomicInteger atomicInteger = new AtomicInteger(0);
    @Override
    public void process(Page page) {
//        System.out.println(count+++" >>>>>>>>>>>>>>>");
        System.out.println(atomicInteger.getAndIncrement()+" >>>>>>>>>>>>>>>");
        List<String> distanceList = page.getJson().jsonPath("$.result.elements[*].distance.value").all();
        String stockCode = (String) page.getRequest().getExtra("stock_code");
        List<Integer> orgIds = (List<Integer>) page.getRequest().getExtra("orgIds");
        for (int i=0;i<distanceList.size();i++){
            int status = jdbcTemplate.update("UPDATE com_org SET distance2=? WHERE stock_code=? AND green_org_id=?",
                    distanceList.get(i),
                    stockCode,
                    orgIds.get(i));
        }
        Request req = genereteReq();
        if(req != null){
            page.addTargetRequest(req);
        }else {
            return;
        }
    }
    private Site site = Site.me()
            .setRetryTimes(5)
            .setSleepTime(100)
//            .addHeader("Host", "api.map.baidu.com")
            .addHeader(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");
    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        BaiduRouteSpider baiduRouteSpider = new BaiduRouteSpider();
        Spider.create(baiduRouteSpider)
                .addRequest(baiduRouteSpider.genereteReq())
                .thread(1)
                .run();
        System.out.println("cost " + (System.currentTimeMillis() - start) / 1000
                + " seconds");
    }
}
