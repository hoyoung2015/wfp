package net.hoyoung.wfp.spider.comweb;

import us.codecraft.webmagic.Page;

public class ComWebPage extends Page {

	private String filename;
	private long contentLength;
	private String contentType;
	
	
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public long getContentLength() {
		return contentLength;
	}
	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}

}
