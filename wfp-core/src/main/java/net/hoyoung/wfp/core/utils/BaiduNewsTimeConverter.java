package net.hoyoung.wfp.core.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaiduNewsTimeConverter {

	public static Date convert(String s,Date now){
		Pattern p = Pattern.compile("(\\d+)小时前");
		Matcher m = p.matcher(s);
		if(m.find()){
			int h = Integer.valueOf(m.group(1));
			Calendar c = Calendar.getInstance();
			c.setTime(now);
			c.add(Calendar.HOUR, 0-h);
			return c.getTime();
		}
		return null;
	}
	public static void main(String[] args) {
		System.out.println(BaiduNewsTimeConverter.convert("4小时前",new Date()));
	}
}
