package net.hoyoung.wfp.stockdown;

import java.util.List;

import org.hibernate.Session;

import net.hoyoung.wfp.core.entity.CompanyInfo;
import net.hoyoung.wfp.core.utils.HibernateUtils;
/**
 *给网址添加http://前缀，是网址符合标准的url格式
 * @author hoyoung
 *注： 执行之前必须进行url合法性验证和修正
 */
public class WebSiteAddHttpHead {

	public static void main(String[] args) {
		
		Session session = HibernateUtils.openSession();
		
		List<CompanyInfo> list = session.createQuery("select new CompanyInfo(id,stockCode,webSite) from CompanyInfo").list();
		for (CompanyInfo companyInfo : list) {
			if(!companyInfo.getWebSite().startsWith("http://") && !companyInfo.getWebSite().startsWith("https://")){
				session.beginTransaction();
				session.createQuery("update CompanyInfo set webSite=? where id=?")
				.setParameter(0, "http://"+companyInfo.getWebSite())
				.setParameter(1, companyInfo.getId())
				.executeUpdate();
				session.getTransaction().commit();
			}
		}
	}

}
