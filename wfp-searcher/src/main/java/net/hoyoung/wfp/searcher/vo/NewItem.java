package net.hoyoung.wfp.searcher.vo;

import java.util.Date;

public class NewItem {
	private int id;
	
	private String stockCode;//股票号
	private String query;//搜索关键词
	
	private String title;
	private String summary;
	
	private String targetUrl;

	private String keyword;
	
	private String targetHtml;
	
	//创建时间
	private Date createDate;
	
	public String getTargetHtml() {
		return targetHtml;
	}
	public void setTargetHtml(String targetHtml) {
		this.targetHtml = targetHtml;
	}
	//新闻发布时间
	private Date publishDate;

	private String publishDateStr;
	
	//新闻来源
	private String sourceName;

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Date getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getTargetUrl() {
		return targetUrl;
	}
	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getStockCode() {
		return stockCode;
	}
	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public String getPublishDateStr() {
		return publishDateStr;
	}

	public void setPublishDateStr(String publishDateStr) {
		this.publishDateStr = publishDateStr;
	}
}
