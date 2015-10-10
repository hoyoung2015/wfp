package net.hoyoung.wfp.core.dao;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import net.hoyoung.wfp.core.entity.CompanyPatents;
@Repository
public class CompanyPatentsDao extends BaseDao {
	public void add(CompanyPatents companyPatents){
		getSession().save(companyPatents);
	}

	public void updateGreen(Map<String, String> patents) {
		Session session = getSession();
		Set<Entry<String, String>> sets = patents.entrySet();
		for (Entry<String, String> entry : sets) {
//			System.err.println(entry.getKey()+"|"+entry.getValue());
			session.createQuery("update CompanyPatents cp set cp.green=true where cp.patMainStdmode like '%"+entry.getKey()+"%'")
			.executeUpdate();
		}
		
	}
}
