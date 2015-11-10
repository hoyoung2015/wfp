package net.hoyoung.wfp.core.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by Administrator on 2015/11/10.
 */
@Entity
public class Hporg {
    private int id;
    private String name;
    private String province;
    private String city;
    private String area;
    private Float posX;
    private Float posY;
    private String addr;
    private String certLevel;
    private String certNo;
    private String evalRange;
    private String tel;
    private String email;
    private Integer projectNum;
    private Integer permitNum;
    private Integer engineerNum;
    private Integer recordNum;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Hporg() {
    }

    public Hporg(int id, Float posX, Float posY) {
        this.id = id;
        this.posX = posX;
        this.posY = posY;
    }

    @Override
    public String toString() {
        return "Hporg{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", area='" + area + '\'' +
                ", posX=" + posX +
                ", posY=" + posY +
                ", addr='" + addr + '\'' +
                ", certLevel='" + certLevel + '\'' +
                ", certNo='" + certNo + '\'' +
                ", evalRange='" + evalRange + '\'' +
                ", tel='" + tel + '\'' +
                ", email='" + email + '\'' +
                ", projectNum=" + projectNum +
                ", permitNum=" + permitNum +
                ", engineerNum=" + engineerNum +
                ", recordNum=" + recordNum +
                '}';
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "province")
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Basic
    @Column(name = "city")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Basic
    @Column(name = "area")
    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    @Basic
    @Column(name = "pos_x")
    public Float getPosX() {
        return posX;
    }

    public void setPosX(Float posX) {
        this.posX = posX;
    }

    @Basic
    @Column(name = "pos_y")
    public Float getPosY() {
        return posY;
    }

    public void setPosY(Float posY) {
        this.posY = posY;
    }

    @Basic
    @Column(name = "addr")
    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    @Basic
    @Column(name = "cert_level")
    public String getCertLevel() {
        return certLevel;
    }

    public void setCertLevel(String certLevel) {
        this.certLevel = certLevel;
    }

    @Basic
    @Column(name = "cert_no")
    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    @Basic
    @Column(name = "eval_range")
    public String getEvalRange() {
        return evalRange;
    }

    public void setEvalRange(String evalRange) {
        this.evalRange = evalRange;
    }

    @Basic
    @Column(name = "tel")
    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @Basic
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "project_num")
    public Integer getProjectNum() {
        return projectNum;
    }

    public void setProjectNum(Integer projectNum) {
        this.projectNum = projectNum;
    }

    @Basic
    @Column(name = "permit_num")
    public Integer getPermitNum() {
        return permitNum;
    }

    public void setPermitNum(Integer permitNum) {
        this.permitNum = permitNum;
    }

    @Basic
    @Column(name = "engineer_num")
    public Integer getEngineerNum() {
        return engineerNum;
    }

    public void setEngineerNum(Integer engineerNum) {
        this.engineerNum = engineerNum;
    }

    @Basic
    @Column(name = "record_num")
    public Integer getRecordNum() {
        return recordNum;
    }

    public void setRecordNum(Integer recordNum) {
        this.recordNum = recordNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hporg hporg = (Hporg) o;

        if (id != hporg.id) return false;
        if (addr != null ? !addr.equals(hporg.addr) : hporg.addr != null) return false;
        if (area != null ? !area.equals(hporg.area) : hporg.area != null) return false;
        if (certLevel != null ? !certLevel.equals(hporg.certLevel) : hporg.certLevel != null) return false;
        if (certNo != null ? !certNo.equals(hporg.certNo) : hporg.certNo != null) return false;
        if (city != null ? !city.equals(hporg.city) : hporg.city != null) return false;
        if (email != null ? !email.equals(hporg.email) : hporg.email != null) return false;
        if (engineerNum != null ? !engineerNum.equals(hporg.engineerNum) : hporg.engineerNum != null) return false;
        if (evalRange != null ? !evalRange.equals(hporg.evalRange) : hporg.evalRange != null) return false;
        if (name != null ? !name.equals(hporg.name) : hporg.name != null) return false;
        if (permitNum != null ? !permitNum.equals(hporg.permitNum) : hporg.permitNum != null) return false;
        if (posX != null ? !posX.equals(hporg.posX) : hporg.posX != null) return false;
        if (posY != null ? !posY.equals(hporg.posY) : hporg.posY != null) return false;
        if (projectNum != null ? !projectNum.equals(hporg.projectNum) : hporg.projectNum != null) return false;
        if (province != null ? !province.equals(hporg.province) : hporg.province != null) return false;
        if (recordNum != null ? !recordNum.equals(hporg.recordNum) : hporg.recordNum != null) return false;
        if (tel != null ? !tel.equals(hporg.tel) : hporg.tel != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (province != null ? province.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (area != null ? area.hashCode() : 0);
        result = 31 * result + (posX != null ? posX.hashCode() : 0);
        result = 31 * result + (posY != null ? posY.hashCode() : 0);
        result = 31 * result + (addr != null ? addr.hashCode() : 0);
        result = 31 * result + (certLevel != null ? certLevel.hashCode() : 0);
        result = 31 * result + (certNo != null ? certNo.hashCode() : 0);
        result = 31 * result + (evalRange != null ? evalRange.hashCode() : 0);
        result = 31 * result + (tel != null ? tel.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (projectNum != null ? projectNum.hashCode() : 0);
        result = 31 * result + (permitNum != null ? permitNum.hashCode() : 0);
        result = 31 * result + (engineerNum != null ? engineerNum.hashCode() : 0);
        result = 31 * result + (recordNum != null ? recordNum.hashCode() : 0);
        return result;
    }
}
