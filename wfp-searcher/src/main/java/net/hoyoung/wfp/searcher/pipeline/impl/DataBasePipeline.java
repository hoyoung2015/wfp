package net.hoyoung.wfp.searcher.pipeline.impl;

import net.hoyoung.wfp.core.entity.NewItem;
import net.hoyoung.wfp.core.service.NewItemService;
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
		NewItem newItem = new NewItem();
		newItem.setTargetUrl(resultItems.getRequest().getUrl());
		newItem.setTargetHtml(resultItems.get("body").toString());
		newItemService.updateByTargetUrl(newItem);
	}
}
