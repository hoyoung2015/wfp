package net.hoyoung.wfp.core.entity;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.sql.Date;

/**
 * Created by Administrator on 2015/11/10.
 */
@Entity
@javax.persistence.Table(name = "company_info")
public class CompanyInfo {
    private String stockCode;

    public CompanyInfo() {

    }

    public CompanyInfo(String stockCode, Float posX, Float posY) {
        this.stockCode = stockCode;
        this.posX = posX;
        this.posY = posY;
    }

    @Id
    @javax.persistence.Column(name = "stock_code")
    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    private String sname;

    @Basic
    @javax.persistence.Column(name = "sname")
    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    private String area;

    @Basic
    @javax.persistence.Column(name = "area")
    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    private Float posX;

    @Basic
    @javax.persistence.Column(name = "pos_x")
    public Float getPosX() {
        return posX;
    }

    public void setPosX(Float posX) {
        this.posX = posX;
    }

    private Float posY;

    @Basic
    @javax.persistence.Column(name = "pos_y")
    public Float getPosY() {
        return posY;
    }

    public void setPosY(Float posY) {
        this.posY = posY;
    }

    private String industry;

    @Basic
    @javax.persistence.Column(name = "industry")
    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    private BigDecimal institutional;

    @Override
    public String toString() {
        return "CompanyInfo{" +
                "stockCode='" + stockCode + '\'' +
                ", sname='" + sname + '\'' +
                ", area='" + area + '\'' +
                ", posX=" + posX +
                ", posY=" + posY +
                ", industry='" + industry + '\'' +
                ", institutional=" + institutional +
                ", lootchips=" + lootchips +
                ", pricelimit=" + pricelimit +
                ", shareholders=" + shareholders +
                ", name='" + name + '\'' +
                ", ename='" + ename + '\'' +
                ", registerDate=" + registerDate +
                ", addrReg='" + addrReg + '\'' +
                ", addrWork='" + addrWork + '\'' +
                ", market='" + market + '\'' +
                ", listingDate=" + listingDate +
                ", offerDate=" + offerDate +
                ", stockType='" + stockType + '\'' +
                ", addr='" + addr + '\'' +
                ", createDate=" + createDate +
                ", webSite='" + webSite + '\'' +
                '}';
    }

    @Basic
    @javax.persistence.Column(name = "institutional")
    public BigDecimal getInstitutional() {
        return institutional;
    }

    public void setInstitutional(BigDecimal institutional) {
        this.institutional = institutional;
    }

    private BigDecimal lootchips;

    @Basic
    @javax.persistence.Column(name = "lootchips")
    public BigDecimal getLootchips() {
        return lootchips;
    }

    public void setLootchips(BigDecimal lootchips) {
        this.lootchips = lootchips;
    }

    private BigDecimal pricelimit;

    @Basic
    @javax.persistence.Column(name = "pricelimit")
    public BigDecimal getPricelimit() {
        return pricelimit;
    }

    public void setPricelimit(BigDecimal pricelimit) {
        this.pricelimit = pricelimit;
    }

    private BigDecimal shareholders;

    @Basic
    @javax.persistence.Column(name = "shareholders")
    public BigDecimal getShareholders() {
        return shareholders;
    }

    public void setShareholders(BigDecimal shareholders) {
        this.shareholders = shareholders;
    }

    private String name;

    @Basic
    @javax.persistence.Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String ename;

    @Basic
    @javax.persistence.Column(name = "ename")
    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    private Date registerDate;

    @Basic
    @javax.persistence.Column(name = "register_date")
    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    private String addrReg;

    @Basic
    @javax.persistence.Column(name = "addr_reg")
    public String getAddrReg() {
        return addrReg;
    }

    public void setAddrReg(String addrReg) {
        this.addrReg = addrReg;
    }

    private String addrWork;

    @Basic
    @javax.persistence.Column(name = "addr_work")
    public String getAddrWork() {
        return addrWork;
    }

    public void setAddrWork(String addrWork) {
        this.addrWork = addrWork;
    }

    private String market;

    @Basic
    @javax.persistence.Column(name = "market")
    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    private java.util.Date listingDate;

    @Basic
    @javax.persistence.Column(name = "listing_date")
    public java.util.Date getListingDate() {
        return listingDate;
    }

    public void setListingDate(java.util.Date listingDate) {
        this.listingDate = listingDate;
    }

    private java.util.Date offerDate;

    @Basic
    @javax.persistence.Column(name = "offer_date")
    public java.util.Date getOfferDate() {
        return offerDate;
    }

    public void setOfferDate(java.util.Date offerDate) {
        this.offerDate = offerDate;
    }

    private String stockType;

    @Basic
    @javax.persistence.Column(name = "stock_type")
    public String getStockType() {
        return stockType;
    }

    public void setStockType(String stockType) {
        this.stockType = stockType;
    }



    private String addr;

    @Basic
    @javax.persistence.Column(name = "addr")
    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    private java.util.Date createDate;

    @Basic
    @javax.persistence.Column(name = "create_date")
    public java.util.Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(java.util.Date createDate) {
        this.createDate = createDate;
    }

    private String webSite;

    @Basic
    @javax.persistence.Column(name = "web_site")
    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompanyInfo that = (CompanyInfo) o;

        if (addr != null ? !addr.equals(that.addr) : that.addr != null) return false;
        if (addrReg != null ? !addrReg.equals(that.addrReg) : that.addrReg != null) return false;
        if (addrWork != null ? !addrWork.equals(that.addrWork) : that.addrWork != null) return false;
        if (area != null ? !area.equals(that.area) : that.area != null) return false;
        if (createDate != null ? !createDate.equals(that.createDate) : that.createDate != null) return false;
        if (ename != null ? !ename.equals(that.ename) : that.ename != null) return false;
        if (industry != null ? !industry.equals(that.industry) : that.industry != null) return false;
        if (institutional != null ? !institutional.equals(that.institutional) : that.institutional != null)
            return false;
        if (listingDate != null ? !listingDate.equals(that.listingDate) : that.listingDate != null) return false;
        if (lootchips != null ? !lootchips.equals(that.lootchips) : that.lootchips != null) return false;
        if (market != null ? !market.equals(that.market) : that.market != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (offerDate != null ? !offerDate.equals(that.offerDate) : that.offerDate != null) return false;
        if (posX != null ? !posX.equals(that.posX) : that.posX != null) return false;
        if (posY != null ? !posY.equals(that.posY) : that.posY != null) return false;
        if (pricelimit != null ? !pricelimit.equals(that.pricelimit) : that.pricelimit != null) return false;
        if (registerDate != null ? !registerDate.equals(that.registerDate) : that.registerDate != null) return false;
        if (shareholders != null ? !shareholders.equals(that.shareholders) : that.shareholders != null) return false;
        if (sname != null ? !sname.equals(that.sname) : that.sname != null) return false;
        if (stockCode != null ? !stockCode.equals(that.stockCode) : that.stockCode != null) return false;
        if (stockType != null ? !stockType.equals(that.stockType) : that.stockType != null) return false;
        if (webSite != null ? !webSite.equals(that.webSite) : that.webSite != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = stockCode != null ? stockCode.hashCode() : 0;
        result = 31 * result + (sname != null ? sname.hashCode() : 0);
        result = 31 * result + (area != null ? area.hashCode() : 0);
        result = 31 * result + (posX != null ? posX.hashCode() : 0);
        result = 31 * result + (posY != null ? posY.hashCode() : 0);
        result = 31 * result + (industry != null ? industry.hashCode() : 0);
        result = 31 * result + (institutional != null ? institutional.hashCode() : 0);
        result = 31 * result + (lootchips != null ? lootchips.hashCode() : 0);
        result = 31 * result + (pricelimit != null ? pricelimit.hashCode() : 0);
        result = 31 * result + (shareholders != null ? shareholders.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (ename != null ? ename.hashCode() : 0);
        result = 31 * result + (registerDate != null ? registerDate.hashCode() : 0);
        result = 31 * result + (addrReg != null ? addrReg.hashCode() : 0);
        result = 31 * result + (addrWork != null ? addrWork.hashCode() : 0);
        result = 31 * result + (market != null ? market.hashCode() : 0);
        result = 31 * result + (listingDate != null ? listingDate.hashCode() : 0);
        result = 31 * result + (offerDate != null ? offerDate.hashCode() : 0);
        result = 31 * result + (stockType != null ? stockType.hashCode() : 0);
        result = 31 * result + (addr != null ? addr.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (webSite != null ? webSite.hashCode() : 0);
        return result;
    }
}
