package net.hoyoung.wfp.core.dao;

import java.util.List;

import net.hoyoung.wfp.core.entity.CompanyInfo;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

@Repository
public class CompanyInfoDao extends BaseDao{
	public void add(CompanyInfo companyInfo){
		getSession().save(companyInfo);
	}
	public void delete(CompanyInfo companyInfo){
		getSession().delete(companyInfo);
	}
	public void updateByStockCode(CompanyInfo companyInfo) {
		Session session = getSession();
		CompanyInfo c = (CompanyInfo) session.createQuery("from CompanyInfo c where c.stockCode='"+companyInfo.getStockCode()+"'")
		.uniqueResult();
		if(c!=null){
			if(companyInfo.getWebSite()!=null){
				c.setWebSite(companyInfo.getWebSite());
			}
		}
	}
	/**
	 * 根据股票号查询公司
	 * @param stockCode
	 * @return
	 */
	public CompanyInfo getByStockCode(String stockCode) {
		return (CompanyInfo) getSession().createQuery("from CompanyInfo where stockCode=?")
				.setParameter(0, stockCode)
				.uniqueResult();
	}
	public List<CompanyInfo> findAll() {
		return getSession().createCriteria(CompanyInfo.class).list();
	}
}
