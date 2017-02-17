package net.hoyoung.wfp.spider.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

public class ProxyReader {

	public static List<String[]> read(String path) throws IOException {
		List<String[]> proxies = Lists.newArrayList();
		String proxyPath = new File(ProxyReader.class.getResource("/").getPath()).getPath()
				+ System.getProperty("file.separator") + path;
		@SuppressWarnings("unchecked")
		List<String> lines = FileUtils.readLines(new File(proxyPath));
		for (String s : lines) {
			s = s.trim();
			if (StringUtils.isEmpty(s) || s.startsWith("#")) {
				continue;
			}
			proxies.add(s.split(","));
		}

		return proxies;
	}

	public static void main(String[] args) throws IOException {
		ProxyReader.read("proxy.txt");
	}
}
