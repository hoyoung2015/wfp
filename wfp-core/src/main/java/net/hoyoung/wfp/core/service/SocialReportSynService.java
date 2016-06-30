package net.hoyoung.wfp.core.service;

import java.util.List;

import net.hoyoung.wfp.core.dao.SocialReportSynDao;
import net.hoyoung.wfp.core.entity.SocialReportSyn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SocialReportSynService {
	@Autowired
	private SocialReportSynDao socialReportSynDao;
	public void add(SocialReportSyn socialReportSyn){
		socialReportSynDao.add(socialReportSyn);
	}
	public List<SocialReportSyn> findAll(){
		return socialReportSynDao.findAll();
	}
}
