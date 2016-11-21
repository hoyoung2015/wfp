package net.hoyoung.wfp.core.entity;

import java.sql.Timestamp;

/**
 * Created by Administrator on 2015/10/29.
 */
public class ComOrg {
    private int id;
    private String stockCode;
    private Integer greenOrgId;
    private Integer distance1;
    private Integer distance2;
    private Integer distance3;
    private Integer distance4;
    private Timestamp createTime;

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

    public Integer getGreenOrgId() {
        return greenOrgId;
    }

    public void setGreenOrgId(Integer greenOrgId) {
        this.greenOrgId = greenOrgId;
    }

    public Integer getDistance1() {
        return distance1;
    }

    public void setDistance1(Integer distance1) {
        this.distance1 = distance1;
    }

    public Integer getDistance2() {
        return distance2;
    }

    public void setDistance2(Integer distance2) {
        this.distance2 = distance2;
    }

    public Integer getDistance3() {
        return distance3;
    }

    public void setDistance3(Integer distance3) {
        this.distance3 = distance3;
    }

    public Integer getDistance4() {
        return distance4;
    }

    public void setDistance4(Integer distance4) {
        this.distance4 = distance4;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

}
