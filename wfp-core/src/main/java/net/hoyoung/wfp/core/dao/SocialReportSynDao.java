package net.hoyoung.wfp.core.dao;

import org.springframework.stereotype.Repository;

import net.hoyoung.wfp.core.entity.SocialReportSyn;
@Repository
public class SocialReportSynDao extends BaseDao {

	public void add(SocialReportSyn socialReportSyn) {
		getSession().save(socialReportSyn);
	}
}
