package net.hoyoung.wfp.core.service;

import net.hoyoung.wfp.core.dao.SocialReportDao;
import net.hoyoung.wfp.core.entity.SocialReport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SocialReportService {
	@Autowired
	private SocialReportDao socialReportDao;
	public void add(SocialReport socialReport){
		socialReportDao.add(socialReport);
	}
}
