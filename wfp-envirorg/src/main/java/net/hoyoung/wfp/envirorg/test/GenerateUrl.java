package net.hoyoung.wfp.envirorg.test;

import net.hoyoung.wfp.core.entity.ComHporg;
import net.hoyoung.wfp.core.entity.CompanyInfo;
import net.hoyoung.wfp.core.entity.Hporg;
import net.hoyoung.wfp.core.utils.HibernateUtils;
import net.hoyoung.wfp.envirorg.spider.HporgDistanceSpider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.hibernate.Session;
import us.codecraft.webmagic.selector.Json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/11/12.
 */
public class GenerateUrl {
    public static void main(String[] args) {
        Session session = HibernateUtils.getLocalThreadSession();

//        List<Integer> comHporgIds = session.createSQLQuery("SELECT id FROM com_hporg ORDER BY RAND()")
        List<Integer> comHporgIds = session.createSQLQuery("SELECT id FROM com_hporg WHERE distance2 is not null ORDER BY RAND()")
                .setMaxResults(30)
                .list();

        List<ComHporg> comHporgs =  session.createQuery("from ComHporg where id in (:alist)")
                .setParameterList("alist",comHporgIds)
                .list();
        CloseableHttpClient client = HttpClientBuilder.create().build();
        List<String> list = new ArrayList<String>();
        for (ComHporg comHporg:comHporgs){
            CompanyInfo companyInfo = (CompanyInfo) session.createQuery("from CompanyInfo where stockCode=?")
                    .setParameter(0,comHporg.getStockCode())
                    .uniqueResult();
            System.out.println(companyInfo);
            Hporg hporg = (Hporg) session.createQuery("from Hporg where id=?")
                    .setParameter(0,comHporg.getHporgId())
                    .uniqueResult();
            System.out.println(hporg);
            String origins = companyInfo.getPosY()+","+companyInfo.getPosX();
            String destinations = hporg.getPosY()+","+hporg.getPosX();
            String url = "http://api.map.baidu.com/direction/v1/routematrix?output=json&origins=" + origins + "&destinations=" + destinations + "&ak=" + HporgDistanceSpider.AK;
            System.err.println(url);


            try {
                CloseableHttpResponse response = client.execute(new HttpGet(url));
                if(response.getStatusLine().getStatusCode()==200){
                    BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    String s = br.readLine();
                    response.getEntity().getContent().close();
                    System.out.println(s);
                    Json json = new Json(s);

                    String temp = json.jsonPath("$.result.elements[*].distance.value").get();
                    int dis = Integer.valueOf(temp);
                    boolean result = dis==comHporg.getDistance2();
                    list.add(companyInfo.getStockCode()+"\t"+hporg.getId()+"\t"+comHporg.getDistance2()+"=="+dis+"\t"+result);
                    System.out.println(list.get(list.size()-1));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
//            break;
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (String rs : list){
            System.out.println(rs);
        }
        HibernateUtils.closeSession();
    }
}
