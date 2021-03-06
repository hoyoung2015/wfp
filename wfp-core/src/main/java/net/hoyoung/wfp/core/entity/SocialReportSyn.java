package net.hoyoung.wfp.core.entity;

import java.util.Date;

/**
 * Created by hoyoung on 2015/11/29.
 */
public class SocialReportSyn {
    private String stockCode;
    private Float empScore;
    private Float equityScore;
    private Float gdScore;
    private Float hjScore;
    private String level;
    private String publishDate;
    private String reportFileUrl;
    private Float socialScore;
    private Float totalScore;
    private Date createDate;

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    
    
    public Float getEmpScore() {
        return empScore;
    }

    public void setEmpScore(Float empScore) {
        this.empScore = empScore;
    }

    
    
    public Float getEquityScore() {
        return equityScore;
    }

    public void setEquityScore(Float equityScore) {
        this.equityScore = equityScore;
    }

    
    
    public Float getGdScore() {
        return gdScore;
    }

    public void setGdScore(Float gdScore) {
        this.gdScore = gdScore;
    }

    
    
    public Float getHjScore() {
        return hjScore;
    }

    public void setHjScore(Float hjScore) {
        this.hjScore = hjScore;
    }

    
    
    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    
    
    public String getReportFileUrl() {
        return reportFileUrl;
    }

    public void setReportFileUrl(String reportFileUrl) {
        this.reportFileUrl = reportFileUrl;
    }

    
    
    public Float getSocialScore() {
        return socialScore;
    }

    public void setSocialScore(Float socialScore) {
        this.socialScore = socialScore;
    }

    
    
    public Float getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Float totalScore) {
        this.totalScore = totalScore;
    }

    
    
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SocialReportSyn that = (SocialReportSyn) o;

        if (stockCode != null ? !stockCode.equals(that.stockCode) : that.stockCode != null) return false;
        if (empScore != null ? !empScore.equals(that.empScore) : that.empScore != null) return false;
        if (equityScore != null ? !equityScore.equals(that.equityScore) : that.equityScore != null) return false;
        if (gdScore != null ? !gdScore.equals(that.gdScore) : that.gdScore != null) return false;
        if (hjScore != null ? !hjScore.equals(that.hjScore) : that.hjScore != null) return false;
        if (level != null ? !level.equals(that.level) : that.level != null) return false;
        if (publishDate != null ? !publishDate.equals(that.publishDate) : that.publishDate != null) return false;
        if (reportFileUrl != null ? !reportFileUrl.equals(that.reportFileUrl) : that.reportFileUrl != null)
            return false;
        if (socialScore != null ? !socialScore.equals(that.socialScore) : that.socialScore != null) return false;
        if (totalScore != null ? !totalScore.equals(that.totalScore) : that.totalScore != null) return false;
        if (createDate != null ? !createDate.equals(that.createDate) : that.createDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = stockCode != null ? stockCode.hashCode() : 0;
        result = 31 * result + (empScore != null ? empScore.hashCode() : 0);
        result = 31 * result + (equityScore != null ? equityScore.hashCode() : 0);
        result = 31 * result + (gdScore != null ? gdScore.hashCode() : 0);
        result = 31 * result + (hjScore != null ? hjScore.hashCode() : 0);
        result = 31 * result + (level != null ? level.hashCode() : 0);
        result = 31 * result + (publishDate != null ? publishDate.hashCode() : 0);
        result = 31 * result + (reportFileUrl != null ? reportFileUrl.hashCode() : 0);
        result = 31 * result + (socialScore != null ? socialScore.hashCode() : 0);
        result = 31 * result + (totalScore != null ? totalScore.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        return result;
    }
}
