package net.hoyoung.wfp.envirorg.test;

import net.hoyoung.wfp.core.entity.*;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/11/12.
 */
public class GenerateUrlGreenOrg {
    public static void main(String[] args) {
        Session session = HibernateUtils.getLocalThreadSession();

//        List<Integer> comHporgIds = session.createSQLQuery("SELECT id FROM com_hporg ORDER BY RAND()")
        List<Integer> comOrgIds = session.createSQLQuery("SELECT id FROM com_org")
                .setMaxResults(50)
                .list();

        List<ComOrg> comOrgs =  session.createQuery("from ComOrg where id in (:alist)")
                .setParameterList("alist",comOrgIds)
                .list();
        CloseableHttpClient client = HttpClientBuilder.create().build();
        List<String> list = new ArrayList<String>();
        for (ComOrg comOrg:comOrgs){
            CompanyInfo companyInfo = (CompanyInfo) session.createQuery("from CompanyInfo where stockCode=?")
                    .setParameter(0,comOrg.getStockCode())
                    .uniqueResult();
            System.out.println(companyInfo);
            GreenOrg greenOrg = (GreenOrg) session.createQuery("from GreenOrg where id=?")
                    .setParameter(0, comOrg.getGreenOrgId())
                    .uniqueResult();
            System.out.println(greenOrg);
            String origins = companyInfo.getPosY()+","+companyInfo.getPosX();
            String destinations = greenOrg.getLat()+","+greenOrg.getLng();
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
                    boolean result = dis==comOrg.getDistance2();
                    list.add(companyInfo.getStockCode()+"\t"+greenOrg.getId()+"\t"+comOrg.getDistance2()+"=="+dis+"\t"+result);
                    System.out.println(list.get(list.size() - 1));
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
