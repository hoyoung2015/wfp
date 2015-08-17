package net.hoyoung.wfp.core.dao;

import java.util.List;

import net.hoyoung.wfp.core.entity.NewItem;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class NewItemDao extends BaseDao {
	public void save(NewItem newItem) {
		getSession().save(newItem);
	}

	public void insertTargetHtml(String targetUrl, String htmlContent) {
		List<NewItem> list = this.getSession().createCriteria(NewItem.class)
				.add(Restrictions.eq("targetUrl", targetUrl)).list();
		for (NewItem newItem : list) {
			newItem.setTargetHtml(htmlContent);
		}
		/*
		 * this.getSession().createQuery("update NewItem n set n.targetHtml='"+
		 * htmlContent+"' where n.targetUrl='"+targetUrl+"'") .executeUpdate();
		 */
	}
	public void updateByStockCode(NewItem newItem) {
		Session session = getSession();
		NewItem n = (NewItem) session.createQuery("from NewItem where stockCode=?")
				.setParameter(0, newItem.getStockCode())
				.uniqueResult();
		if(n!=null){
			if(newItem.getTargetHtml()!=null){
				n.setTargetHtml(newItem.getTargetHtml());
			}
		}
	}
}
