package net.hoyoung.wfp.spider;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.tika.Tika;
import org.junit.Test;

import com.ctc.wstx.util.URLUtil;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.AudioSpecificConfig.sbr_header;

import us.codecraft.webmagic.utils.UrlUtils;

public class TestTika {
	public static void main(String[] args) {
		Tika tika = new Tika();
		String pathname = "/Users/baidu/tmp/compage/000060/b055cefe923356936d17e6d26a91375878710ae3.pdf";
		try {
			Reader reader = tika.parse(new File(pathname));
			BufferedReader br = new BufferedReader(reader);
			String tmp = null;
			StringBuffer sb = new StringBuffer();
			while ((tmp = br.readLine()) != null) {
				if("".equals(tmp) || Pattern.matches("\\s+", tmp)){
					continue;
				}
				sb.append(tmp);
			}
			System.out.println(sb);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test(){
		URI uri = URI.create("http://zhaopin.fangda.com/alljob?p=1^1");
		
		
	}
	@Test
	public void test2(){
		String url = "http://zhaopin.fangda.com/alljob?p=1^1";
		
		System.out.println(UrlUtils.encodeIllegalCharacterInUrl(url));
//		for (Entry<String, String> entry : queryString.entrySet()) {
//			System.out.println(entry.getKey()+'='+entry.getValue());
//		}
//		
//		try {
//			System.out.println(URLEncoder.encode(url, "utf-8"));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
	}

}
