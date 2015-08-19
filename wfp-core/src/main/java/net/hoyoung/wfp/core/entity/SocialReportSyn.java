package net.hoyoung.wfp.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
/**
 * 社会报告综合
 * @author hoyoung
 *
 */
@Entity
@Table(name = "social_report_syn")
public class SocialReportSyn {
	@Id
	@GeneratedValue
	private int id;
	
	@Column(name="stock_code")
	private String stockCode;
	
	@Column(name="publish_date")
	private String publishDate;

	@Temporal(TemporalType.DATE)
	@Column(name="create_date")
	private Date createDate;
	
	@Column(name="total_score")
	private float totalScore;//总得分
	
	private String level;// 等级
	
	@Column(name="gd_score")
	private float gdScore;//股东责任得分
	
	@Column(name="emp_score")
	private float empScore;//员工责任得分
	
	@Column(name="equity_score")
	private float equityScore;//权益得分
	
	@Column(name="hj_score")
	private float hjScore;//环境得分
	
	@Column(name="social_score")
	private float socialScore;//社会得分
	
	@Column(name="report_file_url")
	private String reportFileUrl;//报告下载路径

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public String getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public float getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(float totalScore) {
		this.totalScore = totalScore;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public float getGdScore() {
		return gdScore;
	}

	public void setGdScore(float gdScore) {
		this.gdScore = gdScore;
	}

	public float getEmpScore() {
		return empScore;
	}

	public void setEmpScore(float empScore) {
		this.empScore = empScore;
	}

	public float getEquityScore() {
		return equityScore;
	}

	public void setEquityScore(float equityScore) {
		this.equityScore = equityScore;
	}

	public float getHjScore() {
		return hjScore;
	}

	public void setHjScore(float hjScore) {
		this.hjScore = hjScore;
	}

	public float getSocialScore() {
		return socialScore;
	}

	public void setSocialScore(float socialScore) {
		this.socialScore = socialScore;
	}

	public String getReportFileUrl() {
		return reportFileUrl;
	}

	public void setReportFileUrl(String reportFileUrl) {
		this.reportFileUrl = reportFileUrl;
	}
	
	
	
}
