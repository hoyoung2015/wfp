package net.hoyoung.wfp.stockdown.greenorg;

import net.hoyoung.wfp.core.utils.DistanceUtil;
import net.hoyoung.wfp.core.utils.JDBCHelper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Created by Administrator on 2015/11/6.
 */
public class DistanceCalculate {
    private Logger log = Logger.getLogger(getClass());
    private List<Map<String,Object>> companies;
    private List<Map<String,Object>> greenOrgs;
    private JdbcTemplate jdbcTemplate;

    public DistanceCalculate() {
        jdbcTemplate = JDBCHelper.createMysqlTemplate("mysql1",
                "jdbc:mysql://localhost/wfp?useUnicode=true&characterEncoding=utf8",
                "root", "", 5, 30);
        log.info(getClass().getName()+" init");
        companies = jdbcTemplate.queryForList("SELECT stock_code,name,sname,pos_x,pos_y FROM company_info");
        greenOrgs = jdbcTemplate.queryForList("SELECT id as org_id,title,lng,lat FROM green_org");
    }
    public List<Map<String,Object>> calculate(double dis){
        double lngDiff = DistanceUtil.distance2lngDiff(dis,60);
        double latDiff = DistanceUtil.distance2latDiff(dis);
        int max = 0;

//        if(status != 1) return;
        for (Map<String,Object> company : companies){
            Object t_comPos = company.get("pos_x");
            if(t_comPos == null) {
                company.put("org_num",0);
                continue;
            }

            double comLng = (Float)t_comPos;//经度

            t_comPos = company.get("pos_y");
            if(t_comPos == null) {
                company.put("org_num",0);
                continue;
            }
            double comLat = (Float)t_comPos;//纬度

            int count = 0;

            String stock_code = (String)company.get("stock_code");
            for (Map<String,Object> map : greenOrgs){
                double orgLng = (Float)map.get("lng");
                double orgLat = (Float)map.get("lat");
                if(Math.abs(orgLng-comLng)>lngDiff) continue;//经度差距太大，淘汰
                if(Math.abs(orgLat-comLat)>latDiff) continue;//纬度差距太大，淘汰
                //计算距离
                double distance1 = DistanceUtil.GetLongDistance(comLng, comLat, orgLng, orgLat);
                if(distance1 > dis) continue;//距离过大，淘汰
                count++;
                int status = jdbcTemplate.update("INSERT INTO com_org(stock_code,green_org_id,distance1,distance2,distance3,distance4,create_time) VALUES (?,?,?,?,?,?,?)",
                        stock_code,
                        map.get("org_id"),
                        distance1,
                        0,
                        0,
                        0,
                        new Date());
                System.out.println("status:"+status);
            }
            company.put("org_num",count);
        }

        return companies;
    }

    public static void main(String[] args) {
        List<Map<String,Object>> companies = new DistanceCalculate().calculate(100000);
        for (Map<String,Object> map : companies){
            System.out.println(map);
        }
    }
}
