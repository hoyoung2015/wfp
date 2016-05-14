-- 查询地表距离至新表com_hporg_dis【环评组织的】
DROP TABLE IF EXISTS `com_hporg_dis`;
create table com_hporg_dis (select stock_code,b.id,cal_dis(a.pos_x,a.pos_y,b.pos_x,b.pos_y) as 'dis'
from company_info a,hporg b);
ALTER TABLE `com_hporg_dis` ENGINE = MyISAM ;