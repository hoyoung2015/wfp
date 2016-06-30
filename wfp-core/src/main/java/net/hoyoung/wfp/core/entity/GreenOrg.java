package net.hoyoung.wfp.core.entity;

import javax.persistence.*;

/**
 * Created by Administrator on 2015/11/12.
 */
@Entity
@Table(name = "green_org", schema = "", catalog = "wfp")
public class GreenOrg {
    private Integer id;
    private String title;
    private Float lng;
    private Float lat;
    private String province;
    private String city;
    private String area;

    @Id
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "lng")
    public Float getLng() {
        return lng;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }

    @Basic
    @Column(name = "lat")
    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GreenOrg greenOrg = (GreenOrg) o;

        if (area != null ? !area.equals(greenOrg.area) : greenOrg.area != null) return false;
        if (city != null ? !city.equals(greenOrg.city) : greenOrg.city != null) return false;
        if (id != null ? !id.equals(greenOrg.id) : greenOrg.id != null) return false;
        if (lat != null ? !lat.equals(greenOrg.lat) : greenOrg.lat != null) return false;
        if (lng != null ? !lng.equals(greenOrg.lng) : greenOrg.lng != null) return false;
        if (province != null ? !province.equals(greenOrg.province) : greenOrg.province != null) return false;
        if (title != null ? !title.equals(greenOrg.title) : greenOrg.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (lng != null ? lng.hashCode() : 0);
        result = 31 * result + (lat != null ? lat.hashCode() : 0);
        result = 31 * result + (province != null ? province.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (area != null ? area.hashCode() : 0);
        return result;
    }
}
