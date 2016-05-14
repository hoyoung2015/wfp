select count(*) from company_info where web_site='';

set sql_safe_updates=0;
delete from company_info where web_site='';

select name,stock_code,web_site from company_info order by length(web_site) desc;