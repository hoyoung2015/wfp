package net.hoyoung.patents;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import net.hoyoung.wfp.core.entity.CompanyPatents;
import net.hoyoung.wfp.core.service.CompanyPatentsService;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class PatentsPipeline implements Pipeline {

	private CompanyPatentsService companyPatentsService;
	
	public PatentsPipeline(CompanyPatentsService companyPatentsService) {
		super();
		this.companyPatentsService = companyPatentsService;
	}


	public void setCompanyPatentsService(CompanyPatentsService companyPatentsService) {
		this.companyPatentsService = companyPatentsService;
	}


	public void process(ResultItems resultItems, Task task) {
		CompanyPatents info = new CompanyPatents();
		Map<String, Object> map = resultItems.getAll();
		
		if(map.isEmpty()){
			return;
		}
		
		for( Entry<String, Object> entry :map.entrySet()){
			String key = entry.getKey();
			String value = (String) entry.getValue();
			String methodName = key.substring(0, 1).toUpperCase()+key.substring(1);
			try {
				Method method = CompanyPatents.class.getMethod("set"+methodName, String.class);
				method.invoke(info, value);
				
			} catch (Exception  e) {
				e.printStackTrace();
			}
		}
		companyPatentsService.add(info);
	}

}
