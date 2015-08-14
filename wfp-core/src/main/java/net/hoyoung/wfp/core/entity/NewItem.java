package net.hoyoung.wfp.core.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Index;
@Entity
@Table(name="new_item")
public class NewItem {
	@Id
	@GeneratedValue
	private int id;
	
	@Column(name="stock_code")
	private String stockCode;//股票号
	
	private String title;
	
	@Column(columnDefinition="TEXT")
	private String summary;
	
	@Column(name="target_url")
	private String targetUrl;
	
	@Basic(fetch=FetchType.LAZY)
	@Column(name="target_html",length=16777215)
	@Index(name="html_index")//创建索引
	private String targetHtml;
	
	//创建时间
	@Column(name="create_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	public String getTargetHtml() {
		return targetHtml;
	}
	public void setTargetHtml(String targetHtml) {
		this.targetHtml = targetHtml;
	}
	//新闻发布时间
	@Column(name="publish_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date publishDate;
	
	//新闻来源
	private String sourceName;
	
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
	
}
