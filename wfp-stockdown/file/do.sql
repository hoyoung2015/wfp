-- 查询地表距离至新表com_org_dis
DROP TABLE IF EXISTS `com_org_dis`;
create table com_org_dis (select stock_code,b.id,cal_dis(a.pos_x,a.pos_y,b.lng,b.lat) as 'dis'
from company_info a,green_org b);
ALTER TABLE `com_org_dis` ENGINE = MyISAM ;

-- 创建报表
DROP TABLE IF EXISTS `baobiao`;
create table baobiao (
select a.stock_code,a.name,a.sname,a.area,a.industry  ,if(t_100.green_org_num is null,0,t_100.green_org_num) as travel_100  ,if(t_90.green_org_num is null,0,t_90.green_org_num) as travel_90  ,if(t_80.green_org_num is null,0,t_80.green_org_num) as travel_80  ,if(t_70.green_org_num is null,0,t_70.green_org_num) as travel_70  ,if(t_60.green_org_num is null,0,t_60.green_org_num) as travel_60  ,if(t_50.green_org_num is null,0,t_50.green_org_num) as travel_50  ,if(t_40.green_org_num is null,0,t_40.green_org_num) as travel_40  ,if(t_30.green_org_num is null,0,t_30.green_org_num) as travel_30  ,if(t_20.green_org_num is null,0,t_20.green_org_num) as travel_20  ,if(t_10.green_org_num is null,0,t_10.green_org_num) as travel_10  ,if(t_5.green_org_num is null,0,t_5.green_org_num) as travel_5  from company_info a  left join (select stock_code,count(*) as green_org_num from com_org where distance2<=100*1000 group by stock_code) t_100 on a.stock_code=t_100.stock_code  left join (select stock_code,count(*) as green_org_num from com_org where distance2<=90*1000 group by stock_code) t_90 on a.stock_code=t_90.stock_code  left join (select stock_code,count(*) as green_org_num from com_org where distance2<=80*1000 group by stock_code) t_80 on a.stock_code=t_80.stock_code  left join (select stock_code,count(*) as green_org_num from com_org where distance2<=70*1000 group by stock_code) t_70 on a.stock_code=t_70.stock_code  left join (select stock_code,count(*) as green_org_num from com_org where distance2<=60*1000 group by stock_code) t_60 on a.stock_code=t_60.stock_code  left join (select stock_code,count(*) as green_org_num from com_org where distance2<=50*1000 group by stock_code) t_50 on a.stock_code=t_50.stock_code  left join (select stock_code,count(*) as green_org_num from com_org where distance2<=40*1000 group by stock_code) t_40 on a.stock_code=t_40.stock_code  left join (select stock_code,count(*) as green_org_num from com_org where distance2<=30*1000 group by stock_code) t_30 on a.stock_code=t_30.stock_code  left join (select stock_code,count(*) as green_org_num from com_org where distance2<=20*1000 group by stock_code) t_20 on a.stock_code=t_20.stock_code  left join (select stock_code,count(*) as green_org_num from com_org where distance2<=10*1000 group by stock_code) t_10 on a.stock_code=t_10.stock_code  left join (select stock_code,count(*) as green_org_num from com_org where distance2<=5*1000 group by stock_code) t_5 on a.stock_code=t_5.stock_code
);
ALTER TABLE `baobiao` ENGINE = MyISAM ;


-- 报表 带最小距离
select dis_set.*,min_set.min_dis from (

select a.stock_code,a.name,a.sname,a.area,a.industry  ,if(t_100.green_org_num is null,0,t_100.green_org_num) as dis_100  ,if(t2_100.green_org_num is null,0,t2_100.green_org_num) as travel_100  from company_info a  left join (select stock_code,count(*) as green_org_num from com_org where distance1<=100*1000 group by stock_code) t_100 on a.stock_code=t_100.stock_code  left join (select stock_code,count(*) as green_org_num from com_org where distance2<=100*1000 group by stock_code) t2_100 on a.stock_code=t2_100.stock_code

 ) dis_set ,
 (
 select stock_code,min(dis) as `min_dis` from com_org_dis group by stock_code
 ) min_set
 where dis_set.stock_code=min_set.stock_code