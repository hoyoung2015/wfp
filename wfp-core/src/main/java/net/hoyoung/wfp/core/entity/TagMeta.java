package net.hoyoung.wfp.core.entity;

import java.util.Date;
/**
 * 用来在数据库中记录非机构信息的
 * @author Administrator
 */
public class TagMeta {
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

	
	private String value;
	
	
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
