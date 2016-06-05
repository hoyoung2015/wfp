package net.hoyoung.wfp.searcher.vo;

import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="new_item")
public class NewItem {
	@Id
	@GeneratedValue
	private int id;
	
	@Column(name="stock_code",length = 10)
	@Index(name = "idx_stock_code")
	private String stockCode;//股票号
	@Index(name = "idx_query")
	private String query;//搜索关键词
	
	private String title;
	
	@Column(columnDefinition="TEXT")
	private String summary;
	
	@Column(name="target_url")
	@Index(name="idx_target_url")
	private String targetUrl;

	@Index(name = "idx_keyword")
	private String keyword;
	
	@Basic(fetch=FetchType.LAZY)
	@Column(name="target_html",length=16777215)
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

	@Column(name = "publish_date_str")
	private String publishDateStr;
	
	//新闻来源
	@Column(name = "source_name")
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
