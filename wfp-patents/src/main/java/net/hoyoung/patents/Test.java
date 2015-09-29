package net.hoyoung.patents;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void main(String[] args) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		
		String url = "http://librarian.wanfangdata.com.cn/Patent.aspx?dbhit=&q=[query]&db=patent&p=23";
		
		System.err.println(Test.getPage(url));;
		
	}
	
	private static String getPage(String url){
		String s ="^http://librarian.wanfangdata.com.cn/Patent.aspx\\?dbhit=.*(&p=([0-9]+))$";
		
		Pattern p = Pattern.compile(s);
		Matcher m = p.matcher(url);
		if(m.find()){
			return m.group(2);
		}
		return null;
	}

}
