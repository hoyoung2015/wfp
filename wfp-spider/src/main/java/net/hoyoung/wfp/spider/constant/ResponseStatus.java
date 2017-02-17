package net.hoyoung.wfp.spider.constant;

public enum ResponseStatus {

	SUCCESS(0,"成功");
	
	private int code;
	private String message;
	
	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	private ResponseStatus(int code, String message) {
		this.code = code;
		this.message = message;
	}
	
}
