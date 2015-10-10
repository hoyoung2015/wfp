package net.hoyoung.wfp.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

@Entity
@Table(name="company_patents")
public class CompanyPatents {
	@Id
	@GeneratedValue
	private int patentsId;
	private String patCode;//专利号
	private String comName;
	private String patName;
	
	@Column(columnDefinition="TEXT")
	@Index(name="ix_pat_info")
	private String patInfo;
	
	private String patMainStdmode;//主分类号
	private String patStdmode;//分类号
	
	
	private boolean green;//是否为绿色专利
	private String source;
	private String remarks;
	
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public boolean isGreen() {
		return green;
	}
	public void setGreen(boolean green) {
		this.green = green;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public int getPatentsId() {
		return patentsId;
	}
	public void setPatentsId(int patentsId) {
		this.patentsId = patentsId;
	}
	
	public String getPatInfo() {
		return patInfo;
	}
	public void setPatInfo(String patInfo) {
		this.patInfo = patInfo;
	}
	public String getPatCode() {
		return patCode;
	}
	public void setPatCode(String patCode) {
		this.patCode = patCode;
	}
	public String getComName() {
		return comName;
	}
	public void setComName(String comName) {
		this.comName = comName;
	}
	public String getPatName() {
		return patName;
	}
	public void setPatName(String patName) {
		this.patName = patName;
	}
	public String getPatMainStdmode() {
		return patMainStdmode;
	}
	public void setPatMainStdmode(String patMainStdmode) {
		this.patMainStdmode = patMainStdmode;
	}
	public String getPatStdmode() {
		return patStdmode;
	}
	public void setPatStdmode(String patStdmode) {
		this.patStdmode = patStdmode;
	}
	
	
	
}
