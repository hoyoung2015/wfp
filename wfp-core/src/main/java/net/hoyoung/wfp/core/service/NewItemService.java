package net.hoyoung.wfp.core.service;

import org.springframework.beans.factory.annotation.Autowired;

import net.hoyoung.wfp.core.dao.NewItemDao;
import net.hoyoung.wfp.core.entity.NewItem;

public class NewItemService {
	@Autowired
	private NewItemDao newItemDao;
	public void save(NewItem newItem){
		newItemDao.save(newItem);
	}
	public void updateByStockCode(NewItem newItem){
		newItemDao.updateByStockCode(newItem);
	}
}
