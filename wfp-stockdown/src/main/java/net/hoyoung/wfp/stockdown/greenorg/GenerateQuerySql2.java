package net.hoyoung.wfp.stockdown.greenorg;

/**
 * Created by Administrator on 2015/11/6.
 */
public class GenerateQuerySql2 {
    public static void main(String[] args) {

        String fieldTemp = " ,if(t_[dis].green_org_num is null,0,t_[dis].green_org_num) as dis_[dis] ";
        String joinTemp = " left join (select stock_code,count(*) as green_org_num from com_org where distance1<=[dis]*1000 group by stock_code) t_[dis] on a.stock_code=t_[dis].stock_code ";
        int[] diss = {100,90,80,70,60,50,40,30,20,10,5};
        StringBuffer field = new StringBuffer();
        StringBuffer join = new StringBuffer();
        for (int i = 0; i < diss.length; i++) {
            field.append(fieldTemp.replace("[dis]",diss[i]+""));
            join.append(joinTemp.replace("[dis]",diss[i]+""));
        }
        String sql = "select a.stock_code"+field.toString()+" from company_info a "+join.toString();
        System.out.println(sql);
    }
}
