package net.hoyoung.wfp.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
/**
 * 用来在数据库中记录非机构信息的
 * @author Administrator
 */
@Entity
@Table(name="tag_meta")
public class TagMeta {
	@Id
	@GeneratedValue
	private int id;
	private String tag;
	
	public TagMeta() {
		super();
	}
	
	public TagMeta(int id, String tag, String value, Date createDate) {
		super();
		this.id = id;
		this.tag = tag;
		this.value = value;
		this.createDate = createDate;
	}

	public TagMeta(String tag, String value, Date createDate) {
		super();
		this.tag = tag;
		this.value = value;
		this.createDate = createDate;
	}

	@Column(name="tag_value")
	private String value;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_date")
	private Date createDate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "TagMeta [id=" + id + ", tag=" + tag + ", value=" + value
				+ ", createDate=" + createDate + "]";
	}
	
}
