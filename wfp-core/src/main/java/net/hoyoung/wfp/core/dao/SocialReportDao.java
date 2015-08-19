package net.hoyoung.wfp.core.dao;

import net.hoyoung.wfp.core.entity.SocialReport;

import org.springframework.stereotype.Repository;

@Repository
public class SocialReportDao extends BaseDao {
	public void add(SocialReport entity){
		getSession().save(entity);
	}
	public void delete(SocialReport entity){
		getSession().delete(entity);
	}
}
