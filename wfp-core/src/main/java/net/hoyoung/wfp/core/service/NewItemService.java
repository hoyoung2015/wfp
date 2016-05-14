package net.hoyoung.wfp.core.service;

import java.util.List;

import net.hoyoung.wfp.core.dao.NewItemDao;
import net.hoyoung.wfp.core.entity.NewItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class NewItemService {
	@Autowired
	private NewItemDao newItemDao;
	public void save(NewItem newItem){
		newItemDao.save(newItem);
	}
	public void updateByStockCode(NewItem newItem){
		newItemDao.updateByStockCode(newItem);
	}
	public List<NewItem> findAll(){
		return newItemDao.findAll();
	}
	public void updateByTargetUrl(NewItem newItem) {
		newItemDao.updateByTargetUrl(newItem);
	}
}
