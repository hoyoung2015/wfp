package net.hoyoung.wfp.core.entity;

import java.util.Date;
/**
 * 社会报告详情
 * @author hoyoung
 *
 */
public class SocialReport {
	private int id;
	
	private String stockCode;
	
	private String publishDate;

	
	private String gdResp;// 股东社会责任json

	
	private String empResp;// 员工

	
	private String equityResp;// 权益责任

	
	private String hjResp;// 环境

	
	private String socialResp;// 社会

	
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
