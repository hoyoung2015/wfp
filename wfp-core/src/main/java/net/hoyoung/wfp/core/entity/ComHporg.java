package net.hoyoung.wfp.core.entity;

/**
 * Created by Administrator on 2015/11/10.
 */
public class ComHporg {
    private String stockCode;
    private int hporgId;
    private Float distance1;
    private Float distance2;
    private int id;

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public int getHporgId() {
        return hporgId;
    }

    public void setHporgId(int hporgId) {
        this.hporgId = hporgId;
    }


    public Float getDistance1() {
        return distance1;
    }

    public void setDistance1(Float distance1) {
        this.distance1 = distance1;
    }

    public Float getDistance2() {
        return distance2;
    }

    public void setDistance2(Float distance2) {
        this.distance2 = distance2;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
