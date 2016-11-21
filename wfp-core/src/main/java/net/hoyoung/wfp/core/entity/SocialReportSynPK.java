package net.hoyoung.wfp.core.entity;

import java.io.Serializable;

/**
 * Created by hoyoung on 2015/11/29.
 */
public class SocialReportSynPK implements Serializable {
    private String stockCode;
    private String publishDate;

    
    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    
    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

}
