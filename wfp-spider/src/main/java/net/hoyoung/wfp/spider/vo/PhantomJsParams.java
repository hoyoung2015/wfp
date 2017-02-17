package net.hoyoung.wfp.spider.vo;

import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

import com.alibaba.fastjson.JSON;

public class PhantomJsParams {

	private String url;
	private Map<String, String> headers;
	private int timeOut;

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int readTimeout) {
		this.timeOut = readTimeout;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public static void main(String[] args) {
		
		PhantomJsParams vo = new PhantomJsParams();
		vo.setUrl("http://localhost:8888");
		String json = JSON.toJSONString(vo);
		
		System.out.println(StringEscapeUtils.escapeEcmaScript(json));
	}
}
