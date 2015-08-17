package net.hoyoung.wfp.webdown.utils;
public class WfpUrlUtils {
	public static boolean isSameHost(String a,String b){
		String sa = a.replace("www.", "").replace("http://", "");
		String sb = b.replace("www.", "").replace("http://", "");
		int ia = a.indexOf("/");
		int ib = a.indexOf("/");
		sa = sa.substring(0, ia);
		sb = sb.substring(0, ib);
		return sa.equals(sb);
	}
}
