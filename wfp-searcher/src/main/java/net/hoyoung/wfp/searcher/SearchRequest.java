package net.hoyoung.wfp.searcher;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import us.codecraft.webmagic.selector.Html;

public class SearchRequest {
	private Map<String,Object> extras;
	private Set<String> keywords;
	private String query;
	private Html html;
	public void putExtra(String key,Object o){
		if(extras==null){
			extras = new HashMap<String, Object>();
		}
		extras.put(key, o);
	}
	
	public Html getHtml() {
		return html;
	}

	public void setHtml(String text) {
		this.html = new Html(text);
	}

	public Object getExtra(String key){
		if(extras==null || extras.isEmpty()){
			return null;
		}
		return extras.get(key);
	}
	public SearchRequest addKeyword(String keyword){
		if(keywords==null){
			keywords = new TreeSet<String>();
		}
		keywords.add(keyword);
		return this;
	}
	public String getQuery(){
		if(keywords==null){
			return null;
		}
		if(query==null){
			StringBuffer sb = new StringBuffer();
			Iterator<String> ite = keywords.iterator();
			boolean isFirst = true;
			while(ite.hasNext()){
				String key = ite.next();
				if(isFirst){
					sb.append(key);
					isFirst = false;
				}else{
					sb.append(" "+key);
				}
			}
			query = sb.toString();
		}
		return query;
	}
	
}
