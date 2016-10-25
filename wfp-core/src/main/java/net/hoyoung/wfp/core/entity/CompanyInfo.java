package net.hoyoung.wfp.core.entity;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Administrator on 2015/11/10.
 */
@Document(collection = "company_info")
public class CompanyInfo {
	private String stockCode;

	public CompanyInfo() {

	}

	public CompanyInfo(String stockCode, Float posX, Float posY) {
		this.stockCode = stockCode;
		this.posX = posX;
		this.posY = posY;
	}

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	private String sname;

	public String getSname() {
		return sname;
	}

	public void setSname(String sname) {
		this.sname = sname;
	}

	private String area;

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	private Float posX;

	public Float getPosX() {
		return posX;
	}

	public void setPosX(Float posX) {
		this.posX = posX;
	}

	private Float posY;

	public Float getPosY() {
		return posY;
	}

	public void setPosY(Float posY) {
		this.posY = posY;
	}

	private String industry;

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	private Double institutional;

	public Double getInstitutional() {
		return institutional;
	}

	public void setInstitutional(Double institutional) {
		this.institutional = institutional;
	}

	private Double lootchips;

	public Double getLootchips() {
		return lootchips;
	}

	public void setLootchips(Double lootchips) {
		this.lootchips = lootchips;
	}

	private Double pricelimit;

	public Double getPricelimit() {
		return pricelimit;
	}

	public void setPricelimit(Double pricelimit) {
		this.pricelimit = pricelimit;
	}

	private Double shareholders;

	public Double getShareholders() {
		return shareholders;
	}

	public void setShareholders(Double shareholders) {
		this.shareholders = shareholders;
	}

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String ename;

	public String getEname() {
		return ename;
	}

	public void setEname(String ename) {
		this.ename = ename;
	}

	private Date registerDate;

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	private String addrReg;

	public String getAddrReg() {
		return addrReg;
	}

	public void setAddrReg(String addrReg) {
		this.addrReg = addrReg;
	}

	private String addrWork;

	public String getAddrWork() {
		return addrWork;
	}

	public void setAddrWork(String addrWork) {
		this.addrWork = addrWork;
	}

	private String market;

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	private java.util.Date listingDate;

	public java.util.Date getListingDate() {
		return listingDate;
	}

	public void setListingDate(java.util.Date listingDate) {
		this.listingDate = listingDate;
	}

	private java.util.Date offerDate;

	public java.util.Date getOfferDate() {
		return offerDate;
	}

	public void setOfferDate(java.util.Date offerDate) {
		this.offerDate = offerDate;
	}

	private String stockType;

	public String getStockType() {
		return stockType;
	}

	public void setStockType(String stockType) {
		this.stockType = stockType;
	}

	private String addr;

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	private java.util.Date createDate;

	public java.util.Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(java.util.Date createDate) {
		this.createDate = createDate;
	}

	private String webSite;

	public String getWebSite() {
		return webSite;
	}

	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}

}
