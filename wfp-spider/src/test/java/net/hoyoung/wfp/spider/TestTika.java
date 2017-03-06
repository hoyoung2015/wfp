package net.hoyoung.wfp.spider;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

import org.apache.tika.Tika;

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

}
