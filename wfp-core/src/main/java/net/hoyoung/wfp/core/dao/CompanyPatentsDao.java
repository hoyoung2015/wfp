package net.hoyoung.wfp.core.dao;

import org.springframework.stereotype.Repository;

import net.hoyoung.wfp.core.entity.CompanyPatents;
@Repository
public class CompanyPatentsDao extends BaseDao {
	public void add(CompanyPatents companyPatents){
		getSession().save(companyPatents);
	}
}
