package net.hoyoung.wfp.core.utils;

import java.util.regex.Pattern;

public class StringUtils {
	/**
	 * 清除空串
	 * 在html中有一种ascii嘛为160的空格也要替换掉
	 * @param source
	 * @return
	 */
	public static String clearEmptyStr(String source){
		if(source==null){
			return null;
		}
		return source.replaceAll(" |\\u00A0", "");
	}
	public static String addHttpHead(String source){
		if(Pattern.compile("(https?:\\/\\/)?(\\w+\\.?)+(\\/[-a-zA-Z0-9\\?%=_\\-\\+\\/]+)?").matcher(source).matches()){
			return "http://"+source;
		}
		return source;
	}
	public static String removeBrackets(String s){
		return s==null?null:s.replaceAll("\\(.*\\)", "");
	}
	public static void main(String[] args) {
		System.out.println(removeBrackets("房价开始大(fsdfsdf)"));
	}
}
