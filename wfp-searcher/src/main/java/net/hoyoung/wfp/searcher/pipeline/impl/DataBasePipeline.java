package net.hoyoung.wfp.searcher.pipeline.impl;

import net.hoyoung.wfp.core.service.NewItemService;
import net.hoyoung.wfp.searcher.dao.NewItemDao;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class DataBasePipeline implements Pipeline {
	private NewItemService newItemService;
	
	public void setNewItemService(NewItemService newItemService) {
		this.newItemService = newItemService;
	}

	@Override
	public void process(ResultItems resultItems, Task task) {
		// System.err.println(resultItems.getRequest().getUrl());
				
		newItemDao.insertTargetHtml(resultItems.getRequest().getUrl(),
				resultItems.get("body").toString());
	}
}
