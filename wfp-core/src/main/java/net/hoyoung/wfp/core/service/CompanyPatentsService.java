package net.hoyoung.wfp.core.service;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.hoyoung.wfp.core.dao.CompanyPatentsDao;
import net.hoyoung.wfp.core.entity.CompanyPatents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyPatentsService {
	@Autowired
	private CompanyPatentsDao companyPatentsDao;
	
	public void add(CompanyPatents companyPatents){
		companyPatentsDao.add(companyPatents);
	}
	
	public void updateGreen(Map<String,String> patents){
		
		
		
		companyPatentsDao.updateGreen(patents);
		
		
		
		
	}
}
