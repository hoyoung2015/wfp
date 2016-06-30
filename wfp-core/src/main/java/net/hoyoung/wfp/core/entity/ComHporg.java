package net.hoyoung.wfp.core.entity;

import javax.persistence.*;

/**
 * Created by Administrator on 2015/11/10.
 */
@Entity
@Table(name = "com_hporg", schema = "", catalog = "wfp")
public class ComHporg {
    private String stockCode;
    private int hporgId;
    private Float distance1;
    private Float distance2;
    private int id;

    @Basic
    @Column(name = "stock_code")
    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    @Basic
    @Column(name = "hporg_id")
    public int getHporgId() {
        return hporgId;
    }

    public void setHporgId(int hporgId) {
        this.hporgId = hporgId;
    }

    @Override
    public String toString() {
        return "ComHporg{" +
                "stockCode='" + stockCode + '\'' +
                ", hporgId=" + hporgId +
                ", distance1=" + distance1 +
                ", distance2=" + distance2 +
                ", id=" + id +
                '}';
    }

    @Basic
    @Column(name = "distance1")
    public Float getDistance1() {
        return distance1;
    }

    public void setDistance1(Float distance1) {
        this.distance1 = distance1;
    }

    @Basic
    @Column(name = "distance2")
    public Float getDistance2() {
        return distance2;
    }

    public void setDistance2(Float distance2) {
        this.distance2 = distance2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComHporg comHporg = (ComHporg) o;

        if (hporgId != comHporg.hporgId) return false;
        if (distance1 != null ? !distance1.equals(comHporg.distance1) : comHporg.distance1 != null) return false;
        if (distance2 != null ? !distance2.equals(comHporg.distance2) : comHporg.distance2 != null) return false;
        if (stockCode != null ? !stockCode.equals(comHporg.stockCode) : comHporg.stockCode != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = stockCode != null ? stockCode.hashCode() : 0;
        result = 31 * result + hporgId;
        result = 31 * result + (distance1 != null ? distance1.hashCode() : 0);
        result = 31 * result + (distance2 != null ? distance2.hashCode() : 0);
        return result;
    }

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
