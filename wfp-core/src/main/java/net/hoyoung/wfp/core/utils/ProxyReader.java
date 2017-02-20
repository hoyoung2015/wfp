package net.hoyoung.wfp.core.utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.google.common.collect.Lists;

public class ProxyReader {

	public static List<String[]> read() {
		List<String[]> proxies = Lists.newArrayList();
		BufferedReader reader = new BufferedReader(new InputStreamReader(ProxyReader.class.getClassLoader().getResourceAsStream("proxy.txt")));
		try {
			String s = null;
			while((s=reader.readLine())!=null){
				s = s.trim();
				if (org.apache.commons.lang.StringUtils.isEmpty(s) || s.startsWith("#")) {
					continue;
				}
				proxies.add(s.split(","));
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return proxies;
	}

	public static void main(String[] args) throws IOException {
		List<String[]> list = ProxyReader.read();
		for (String[] strings : list) {
			System.out.println(strings[0]);
		}
	}
}