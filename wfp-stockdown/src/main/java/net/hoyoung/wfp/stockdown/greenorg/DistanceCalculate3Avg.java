package net.hoyoung.wfp.stockdown.greenorg;

import net.hoyoung.wfp.core.utils.DistanceUtil;
import net.hoyoung.wfp.core.utils.JDBCHelper;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/6.
 */
public class DistanceCalculate3Avg {
    private Logger log = Logger.getLogger(getClass());
    private List<Map<String,Object>> companies;
    private List<Map<String,Object>> greenOrgs;
    private JdbcTemplate jdbcTemplate;

    public DistanceCalculate3Avg() {
        jdbcTemplate = JDBCHelper.createMysqlTemplate("mysql1",
                "jdbc:mysql://localhost/wfp?useUnicode=true&characterEncoding=utf8",
                "root", "", 5, 30);
        log.info(getClass().getName() + " init");

        companies = jdbcTemplate.queryForList("SELECT stock_code,name,sname,pos_x,pos_y FROM company_info");
        greenOrgs = jdbcTemplate.queryForList("SELECT id as org_id,title,lng,lat FROM green_org");
    }
    public List<Map<String,Object>> calculate(){
        jdbcTemplate.execute("DROP TABLE if EXISTS `com_org_dis_avg`");
        jdbcTemplate.execute("create table com_org_dis_avg(stock_code VARCHAR(10),avg_dis int,PRIMARY KEY (stock_code))");
        int count = 1;
        for (Map<String,Object> com : companies){
            int dis = avgDis(com);
            jdbcTemplate.update("INSERT INTO com_org_dis_avg(stock_code,avg_dis) VALUES (?,?)",com.get("stock_code"),dis);
            System.out.println((count++) +" "+com.get("stock_code")+" "+dis);
        }
        return null;
    }

    private int avgDis(Map<String,Object> com){
        Integer tmp = jdbcTemplate.queryForObject("select avg(dis) from (SELECT dis FROM com_org_dis WHERE stock_code=? ORDER BY dis asc limit 3) t",
                Integer.class, com.get("stock_code"));
        return tmp;
    }

    public static void main(String[] args) {
        new DistanceCalculate3Avg().calculate();
    }
}
