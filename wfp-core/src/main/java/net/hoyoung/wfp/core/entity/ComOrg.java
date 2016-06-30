package net.hoyoung.wfp.core.entity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by Administrator on 2015/10/29.
 */
@Entity
@Table(name = "com_org", schema = "", catalog = "wfp")
public class ComOrg {
    private int id;
    private String stockCode;
    private Integer greenOrgId;
    private Integer distance1;
    private Integer distance2;
    private Integer distance3;
    private Integer distance4;
    private Timestamp createTime;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "stock_code")
    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    @Basic
    @Column(name = "green_org_id")
    public Integer getGreenOrgId() {
        return greenOrgId;
    }

    public void setGreenOrgId(Integer greenOrgId) {
        this.greenOrgId = greenOrgId;
    }

    @Basic
    @Column(name = "distance1")
    public Integer getDistance1() {
        return distance1;
    }

    public void setDistance1(Integer distance1) {
        this.distance1 = distance1;
    }

    @Basic
    @Column(name = "distance2")
    public Integer getDistance2() {
        return distance2;
    }

    public void setDistance2(Integer distance2) {
        this.distance2 = distance2;
    }

    @Basic
    @Column(name = "distance3")
    public Integer getDistance3() {
        return distance3;
    }

    public void setDistance3(Integer distance3) {
        this.distance3 = distance3;
    }

    @Basic
    @Column(name = "distance4")
    public Integer getDistance4() {
        return distance4;
    }

    public void setDistance4(Integer distance4) {
        this.distance4 = distance4;
    }

    @Basic
    @Column(name = "create_time")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComOrg comOrg = (ComOrg) o;

        if (id != comOrg.id) return false;
        if (createTime != null ? !createTime.equals(comOrg.createTime) : comOrg.createTime != null) return false;
        if (distance1 != null ? !distance1.equals(comOrg.distance1) : comOrg.distance1 != null) return false;
        if (distance2 != null ? !distance2.equals(comOrg.distance2) : comOrg.distance2 != null) return false;
        if (distance3 != null ? !distance3.equals(comOrg.distance3) : comOrg.distance3 != null) return false;
        if (distance4 != null ? !distance4.equals(comOrg.distance4) : comOrg.distance4 != null) return false;
        if (greenOrgId != null ? !greenOrgId.equals(comOrg.greenOrgId) : comOrg.greenOrgId != null) return false;
        if (stockCode != null ? !stockCode.equals(comOrg.stockCode) : comOrg.stockCode != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (stockCode != null ? stockCode.hashCode() : 0);
        result = 31 * result + (greenOrgId != null ? greenOrgId.hashCode() : 0);
        result = 31 * result + (distance1 != null ? distance1.hashCode() : 0);
        result = 31 * result + (distance2 != null ? distance2.hashCode() : 0);
        result = 31 * result + (distance3 != null ? distance3.hashCode() : 0);
        result = 31 * result + (distance4 != null ? distance4.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        return result;
    }
}
