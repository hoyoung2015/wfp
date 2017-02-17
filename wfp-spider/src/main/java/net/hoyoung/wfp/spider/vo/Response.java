package net.hoyoung.wfp.spider.vo;

import net.hoyoung.wfp.spider.constant.ResponseStatus;

public class Response<T> {

	private T data;
	private int code;
	private String message;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	public void setStatus(ResponseStatus status){
		this.code = status.getCode();
		this.message = status.getMessage();
	}
	public static <T> Response<T> success(T data){
		Response<T> response = new Response<T>();
		response.setStatus(ResponseStatus.SUCCESS);
		response.setData(data);
		return response;
	}
	public static <T> Response<T> success(){
		Response<T> response = new Response<T>();
		response.setStatus(ResponseStatus.SUCCESS);
		return response;
	}
}
