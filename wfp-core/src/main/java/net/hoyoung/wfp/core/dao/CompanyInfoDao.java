package net.hoyoung.wfp.core.dao;

import net.hoyoung.wfp.core.entity.CompanyInfo;

import org.springframework.stereotype.Repository;

@Repository
public class CompanyInfoDao extends BaseDao{
	public void add(CompanyInfo companyInfo){
		getSession().save(companyInfo);
	}
	public void delete(CompanyInfo companyInfo){
		getSession().delete(companyInfo);
	}
}
