package net.hoyoung.wfp.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Index;

@Entity
@Table(name="company_info")
public class CompanyInfo {
	@Id
	@GeneratedValue
	private int id;
	
	@Column(name="stock_code")
	private String stockCode;//股票号
	
	private String market;
	
	@Index(name="ix_name")
	private String name;//中文全称
	
	private String ename;//英文名称
	
	private String sname;//简称
	
	private String addr;//地址
	
	@Column(name="reg_capital")
	private long regCapital;//注册资本
	
	private String industry;//行业
	
	@Column(name="web_site")
	private String webSite;//网址
	
	@Temporal(TemporalType.DATE)
	@Column(name="listing_date")
	private Date listingDate;//上市日期
	
	@Temporal(TemporalType.DATE)
	@Column(name="offer_date")
	private Date offerDate;//招股日期
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_date")
	private Date createDate;//录入时间

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEname() {
		return ename;
	}

	public void setEname(String ename) {
		this.ename = ename;
	}

	public String getSname() {
		return sname;
	}

	public void setSname(String sname) {
		this.sname = sname;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public long getRegCapital() {
		return regCapital;
	}

	public void setRegCapital(Long regCapital) {
		this.regCapital = regCapital;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getWebSite() {
		return webSite;
	}

	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}

	public Date getListingDate() {
		return listingDate;
	}

	public void setListingDate(Date listingDate) {
		this.listingDate = listingDate;
	}

	public Date getOfferDate() {
		return offerDate;
	}

	public void setOfferDate(Date offerDate) {
		this.offerDate = offerDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	@Override
	public String toString() {
		return "CompanyInfo [id=" + id + ", stockCode=" + stockCode + ", name="
				+ name + ", ename=" + ename + ", sname=" + sname + ", addr="
				+ addr + ", regCapital=" + regCapital + ", industry="
				+ industry + ", webSite=" + webSite + ", listingDate="
				+ listingDate + ", offerDate=" + offerDate + ", createDate="
				+ createDate + "]";
	}
	
	
}
