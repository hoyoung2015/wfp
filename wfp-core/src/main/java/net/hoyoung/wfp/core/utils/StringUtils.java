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
	public static boolean isUrlCorrect(String source){
	/*	String regEx = "^(http|https|ftp)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-"   
		           + "Z0-9\\.&%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{"   
		           + "2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}"   
		           + "[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|"   
		           + "[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-"   
		           + "4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0"   
		           + "-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/"   
		           + "[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&%\\$\\=~_\\-@]*)*$"; */
		Pattern p = Pattern.compile("^(http|www|ftp|)?(://)?(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*((:\\d+)?)(/(\\w+(-\\w+)*))*(\\.?(\\w)*)(\\?)?(((\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*(\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*)*(\\w*)*)$",Pattern.CASE_INSENSITIVE ); 
		return p.matcher(source).matches();
	}
}
