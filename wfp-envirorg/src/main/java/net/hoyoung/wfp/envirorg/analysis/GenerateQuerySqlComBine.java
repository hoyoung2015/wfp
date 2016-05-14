package net.hoyoung.wfp.envirorg.analysis;

/**
 * Created by Administrator on 2015/11/6.
 */
public class GenerateQuerySqlComBine {
    public static void main(String[] args) {

        String fieldTemp = " ,if(t_[dis].hporg_num is null,0,t_[dis].hporg_num) as dis_[dis] ";
        String joinTemp = " left join (select stock_code,count(*) as hporg_num from com_hporg where distance1<=[dis]*1000 group by stock_code) t_[dis] on a.stock_code=t_[dis].stock_code ";
        int[] diss = {100,90,80,70,60,50,40,30,20,10,5};
//        int[] diss = {100};
        StringBuffer field = new StringBuffer();
        StringBuffer join = new StringBuffer();
        //直线距离
        for (int i = 0; i < diss.length; i++) {
            field.append(fieldTemp.replace("[dis]",diss[i]+""));
            join.append(joinTemp.replace("[dis]",diss[i]+""));
        }
        //行车距离
        StringBuffer fieldTravel = new StringBuffer();
        StringBuffer joinTravel = new StringBuffer();
        String fieldTravelTemp = " ,if(t2_[dis].hporg_num is null,0,t2_[dis].hporg_num) as travel_[dis] ";
        String joinTravelTemp = " left join (select stock_code,count(*) as hporg_num from com_hporg where distance2<=[dis]*1000 group by stock_code) t2_[dis] on a.stock_code=t2_[dis].stock_code ";
        for (int i = 0; i < diss.length; i++) {
            fieldTravel.append(fieldTravelTemp.replace("[dis]",diss[i]+""));
            joinTravel.append(joinTravelTemp.replace("[dis]",diss[i]+""));
        }
        String sql = "select a.stock_code,a.name,a.sname,a.area,a.industry "+field.toString()+fieldTravel.toString()+" from company_info a "+join.toString()+joinTravel.toString();
        System.out.println(sql);
    }
}
