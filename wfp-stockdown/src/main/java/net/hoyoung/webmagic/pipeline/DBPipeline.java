package net.hoyoung.webmagic.pipeline;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.data.mongodb.core.MongoTemplate;

import net.hoyoung.wfp.core.entity.CompanyInfo;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class DBPipeline implements Pipeline {

	private MongoTemplate mongoTemplate;
	
	public DBPipeline(MongoTemplate mongoTemplate) {
		super();
		this.mongoTemplate = mongoTemplate;
	}
	@Override
	public void process(ResultItems resultItems, Task task) {
		Map<String,Object> map = resultItems.getAll();
		System.out.println(map.toString());
		if(map.isEmpty()){
			return;
		}
		
		CompanyInfo c = new CompanyInfo();
		c.setCreateDate(new Date());
		for (Entry<String, Object> data : map.entrySet()) {
//			System.out.println(data.getKey()+":"+data.getValue());
			
			if("statusCode".equals(data.getKey())){
				continue;
			}
			
			try {
				Method method = null;
				if (data.getValue() instanceof Float) {
					method = CompanyInfo.class.getMethod("set"+captureName(data.getKey()), float.class);
				}else if (data.getValue() instanceof Double) {
					method = CompanyInfo.class.getMethod("set"+captureName(data.getKey()), double.class);
				}else if (data.getValue() instanceof Long) {
					method = CompanyInfo.class.getMethod("set"+captureName(data.getKey()), long.class);
				}else if (data.getValue() instanceof Integer) {
					method = CompanyInfo.class.getMethod("set"+captureName(data.getKey()), int.class);
				}else{
					method = CompanyInfo.class.getMethod("set"+captureName(data.getKey()), data.getValue().getClass());
				}
				method.invoke(c, data.getValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mongoTemplate.insert(c);
	}
	private  String captureName(String name) {
       return  name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
