package net.hoyoung.wfp.core.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by hoyoung on 2015/11/29.
 */
public class SocialReportSynPK implements Serializable {
    private String stockCode;
    private String publishDate;

    @Column(name = "stock_code")
    @Id
    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    @Column(name = "publish_date")
    @Id
    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SocialReportSynPK that = (SocialReportSynPK) o;

        if (stockCode != null ? !stockCode.equals(that.stockCode) : that.stockCode != null) return false;
        if (publishDate != null ? !publishDate.equals(that.publishDate) : that.publishDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = stockCode != null ? stockCode.hashCode() : 0;
        result = 31 * result + (publishDate != null ? publishDate.hashCode() : 0);
        return result;
    }
}
