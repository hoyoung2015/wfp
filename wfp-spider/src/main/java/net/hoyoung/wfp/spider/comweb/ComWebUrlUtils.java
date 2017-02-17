package net.hoyoung.wfp.spider.comweb;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class ComWebUrlUtils {

	public static final String DOC_REGEX = ".+\\.(pdf|PDF|doc|DOC|docx|DOCX)$";
	
	public static boolean isDoc(String url){
		if(StringUtils.isEmpty(url)){
			return false;
		}
		
		return Pattern.matches(DOC_REGEX, url);
	}
}
