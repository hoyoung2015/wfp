package net.hoyoung.wfp.core.service;

import java.util.List;

import net.hoyoung.wfp.core.dao.CompanyInfoDao;
import net.hoyoung.wfp.core.entity.CompanyInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyInfoService {
	@Autowired
	private CompanyInfoDao companyInfoDao;
	
	public void add(CompanyInfo companyInfo){
		companyInfoDao.add(companyInfo);
	}
	public void updateByStockCode(CompanyInfo companyInfo){
		companyInfoDao.updateByStockCode(companyInfo);
	}
	public CompanyInfo getByStockCode(String stockCode){
		return companyInfoDao.getByStockCode(stockCode);
	}
	public List<CompanyInfo> findAll(){
		return companyInfoDao.findAll();
	}
}
