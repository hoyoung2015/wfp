package net.hoyoung.webmagic.pipeline;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import net.hoyoung.wfp.core.entity.CompanyInfo;
import net.hoyoung.wfp.core.service.CompanyInfoService;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class DBPipeline implements Pipeline {

	private CompanyInfoService companyInfoService;
	
	public DBPipeline(CompanyInfoService companyInfoService) {
		super();
		this.companyInfoService = companyInfoService;
	}
	@Override
	public void process(ResultItems resultItems, Task task) {
		Map<String,Object> map = resultItems.getAll();
		if(map.isEmpty()){
			return;
		}
		
		CompanyInfo c = new CompanyInfo();
		c.setCreateDate(new Date());
		for (Entry<String, Object> data : map.entrySet()) {
//			System.out.println(data.getKey()+":"+data.getValue());
			try {
				Method method = CompanyInfo.class.getMethod("set"+captureName(data.getKey()), data.getValue().getClass());
				method.invoke(c, data.getValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		companyInfoService.add(c);
//		System.out.println(c.toString());
	}
	public void setCompanyInfoService(CompanyInfoService companyInfoService) {
		this.companyInfoService = companyInfoService;
	}
	private  String captureName(String name) {
       return  name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
