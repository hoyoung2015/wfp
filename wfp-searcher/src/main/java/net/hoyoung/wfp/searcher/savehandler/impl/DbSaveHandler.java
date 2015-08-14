package net.hoyoung.wfp.searcher.savehandler.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.hoyoung.wfp.searcher.dao.NewItemDao;
import net.hoyoung.wfp.searcher.entity.NewItem;
import net.hoyoung.wfp.searcher.savehandler.SaveHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
@Component
@Scope("prototype")
public class DbSaveHandler implements SaveHandler {
	@Autowired
	private NewItemDao newItemDao;
	@Override
	public List<NewItem> save(Html html) {
		Html resultHtml = html;
		List<NewItem> list = new ArrayList<NewItem>();
		//开线程来保存
		
		Selectable selectable = resultHtml.xpath("//div[@class='result']");
		List<Selectable> nodes = selectable.nodes();
		for (Selectable sele : nodes) {
			Selectable titleSele = sele.xpath("//h3[@class='c-title']/a");
			String title = titleSele.get().replaceAll("(</?em[^>]*>)|(</?a[^>]*>)", "");
			String targetUrl = titleSele.xpath("//*/@href").get();
			Selectable sumSele = sele.xpath("//div[@class='c-summary']");
			String sourceAndDate = sumSele.xpath("//p/text()").get();
			
			String[] ss = sourceAndDate.split("\\u00A0\\u00A0");
			String sourceName = ss[0];
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
			Date publishDate = null;
			try {
				publishDate = sdf.parse(ss[1]);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String summary = sumSele.get().replaceAll("(<span.*/span>)|(<p.*/p>)|(</?div[^>]*>)|(</?em[^>]*>)", "");
			NewItem newItem = new NewItem();
			newItem.setCreateDate(new Date());
			newItem.setPublishDate(publishDate);
			newItem.setSourceName(sourceName);
			newItem.setTargetUrl(targetUrl);
			newItem.setTitle(title);
			newItem.setSummary(summary);
			newItemDao.save(newItem);
			list.add(newItem);
		}
		return list;
	}

}