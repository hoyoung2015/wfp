package net.hoyoung.webmagic.pipeline;

import java.util.List;
import java.util.regex.Pattern;

import org.hibernate.Session;

import net.hoyoung.wfp.core.entity.SocialReportSyn;
import net.hoyoung.wfp.core.utils.HibernateUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class SocialReportPipeline implements Pipeline {
	@Override
	public void process(ResultItems resultItems, Task task) {
		Session session = HibernateUtils.getCurrentSession();
		if(Pattern.matches(".*zrbg/data/zrbList.aspx.*", resultItems.getRequest().getUrl())){
			List<SocialReportSyn> srsynList = resultItems.get("srsynList");
			session.beginTransaction();
			for (SocialReportSyn socialReportSyn : srsynList) {
				session.saveOrUpdate(socialReportSyn);
			}
			session.getTransaction().commit();
		}

		session.close();
	}

}
