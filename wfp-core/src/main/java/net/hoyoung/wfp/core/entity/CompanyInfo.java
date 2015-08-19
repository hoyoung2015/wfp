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
	
	@Column(name="stock_code",updatable=false,unique=true)
	private String stockCode;//股票号
	
	@Column(name="stock_type")
	private String stockType;
	
	@Column(updatable=false)
	private String market;
	
	@Index(name="ix_name")
	private String name;//中文全称
	
	private String ename;//英文名称
	
	@Index(name="ix_sname")
	private String sname;//简称
	
	@Index(name="ix_addr")
	private String addr;//地址
	
	private float lootchips;//流通股本（亿股）
	
	private double institutional;//注册资本，单位万元
	
	private float pricelimit;//总股本（亿股）
	
	private float shareholders;//流通市值（亿元）
	
	@Index(name="ix_industry")
	private String industry;//行业
	
	public float getLootchips() {
		return lootchips;
	}

	public void setLootchips(float lootchips) {
		this.lootchips = lootchips;
	}

	public double getInstitutional() {
		return institutional;
	}

	public void setInstitutional(double institutional) {
		this.institutional = institutional;
	}

	public String getStockType() {
		return stockType;
	}

	public void setStockType(String stockType) {
		this.stockType = stockType;
	}

	public float getPricelimit() {
		return pricelimit;
	}

	public void setPricelimit(float pricelimit) {
		this.pricelimit = pricelimit;
	}

	public float getShareholders() {
		return shareholders;
	}

	public void setShareholders(float shareholders) {
		this.shareholders = shareholders;
	}

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
	
	private String area;
	
	@Temporal(TemporalType.DATE)
	@Column(name="register_date")
	private Date registerDate;

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

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

	public CompanyInfo() {
		super();
	}

	public CompanyInfo(int id, String stockCode, String webSite) {
		super();
		this.id = id;
		this.stockCode = stockCode;
		this.webSite = webSite;
	}

	@Override
	public String toString() {
		return "CompanyInfo [id=" + id + ", stockCode=" + stockCode
				+ ", market=" + market + ", name=" + name + ", ename=" + ename
				+ ", sname=" + sname + ", addr=" + addr + ", lootchips="
				+ lootchips + ", institutional=" + institutional
				+ ", pricelimit=" + pricelimit + ", shareholders="
				+ shareholders + ", industry=" + industry + ", webSite="
				+ webSite + ", listingDate=" + listingDate + ", offerDate="
				+ offerDate + ", createDate=" + createDate + ", area=" + area
				+ ", registerDate=" + registerDate + "]";
	}
	
}
