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
 * 社会报告详情
 * @author hoyoung
 *
 */
@Entity
@Table(name = "social_report")
public class SocialReport {
	@Id
	@GeneratedValue
	private int id;
	@Column(name="stock_code")
	private String stockCode;
	@Column(name="publish_date")
	private String publishDate;

	@Column(columnDefinition="TEXT",name="gd_resp")
	private String gdResp;// 股东社会责任json

	@Column(columnDefinition="TEXT",name="emp_resp")
	private String empResp;// 员工

	@Column(columnDefinition="TEXT",name="equity_resp")
	private String equityResp;// 权益责任

	@Column(columnDefinition="TEXT",name="hj_resp")
	private String hjResp;// 环境

	@Column(columnDefinition="TEXT",name="social_resp")
	private String socialResp;// 社会

	@Temporal(TemporalType.DATE)
	@Column(name="create_date")
	private Date createDate;
	
	
	
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

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}

	public String getGdResp() {
		return gdResp;
	}

	public void setGdResp(String gdResp) {
		this.gdResp = gdResp;
	}

	public String getEmpResp() {
		return empResp;
	}

	public void setEmpResp(String empResp) {
		this.empResp = empResp;
	}

	public String getEquityResp() {
		return equityResp;
	}

	public void setEquityResp(String equityResp) {
		this.equityResp = equityResp;
	}

	public String getHjResp() {
		return hjResp;
	}

	public void setHjResp(String hjResp) {
		this.hjResp = hjResp;
	}

	public String getSocialResp() {
		return socialResp;
	}

	public void setSocialResp(String socialResp) {
		this.socialResp = socialResp;
	}

}
