SELECT * FROM wfp.company_patents;
SELECT count(*) FROM wfp.company_patents where comName like '%工商银行%' and green = 1;

SELECT count(*) FROM wfp.company_patents where comName like '%恒生%';

SELECT * FROM wfp.company_patents where green=1;

SELECT count(*) FROM wfp.company_patents;

SELECT count(*) FROM wfp.company_patents where green=1;

set sql_safe_updates=0;
delete from wfp.company_patents where comName like '%天马微电子%';
delete from wfp.company_patents where comName is null;

select count(*) from company_patents where green=1 group by comName;

SELECT * FROM wfp.company_patents where comName like '%石油科技%';

update company_patents set comName='武汉钢铁(集团)公司' where comName like '%武汉钢铁%';
update company_patents set comName='三一重工股份有限公司' where comName like '%三一重工%';
update company_patents set comName='西安通源石油科技股份有限公司' where comName like '%西安通源石油%';
update company_patents set comName='广州汽车集团股份有限公司' where comName like '%广州汽车%';

select distinct(comName) from company_patents;

select * from (
select t1.comName,t1.total,t2.gtotal as 'green_total',t2.gtotal/t1.total as 'green_percent' from (
select count(a.patentsId) as 'total',a.comName from company_patents a group by comName
) t1
left join (
select count(*) as 'gtotal',comName from company_patents where green=1 group by comName
) t2 on t1.comName = t2.comName
) tab order by tab.green_percent desc;