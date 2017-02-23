package net.hoyoung.wfp.spider.comweb.urlfilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import us.codecraft.webmagic.utils.UrlUtils;

public class DomainUrlFilter {

	private String configFile = "domain_url_black_list.txt";
	private Map<String, String> regexMap = new HashMap<>();
	private Pattern domainPattern = Pattern.compile("\\[([0-9a-zA-Z_\\-\\.]+)\\]");

	public DomainUrlFilter() {
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(configFile);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String cusorDomain = null;
		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.startsWith("#")) {
					continue;
				}
				Matcher matcher = domainPattern.matcher(line);
				if (matcher.find()) {
					cusorDomain = matcher.group(1);
					continue;
				}
				String regex = regexMap.get(cusorDomain);
				if (regex == null) {
					regex = line;
				} else {
					regex += "|" + line;
				}
				regexMap.put(cusorDomain, regex);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (Entry<String, String> entry : regexMap.entrySet()) {
			System.out.println(entry.getKey() + "\t" + entry.getValue());
		}
	}

	/**
	 * 返回true给通过，否则丢弃该url
	 * 
	 * @param domain
	 * @param url
	 * @return
	 */
	public boolean accept(String domain, String url) {
		String domainThis = UrlUtils.getDomain(url);
		/**
		 * .css?v=1 .css,.jpg 站内 包含#，锚记 "mailto"开头 英文页，繁体
		 */
		if (!url.startsWith("http") // 不是http协议
				|| Pattern.matches(".+(\\.|/)(" + EXCEPT_SUFFIX + ")\\?.*", url) // 排除非html文件
				|| Pattern.matches(".+\\.(" + EXCEPT_SUFFIX + ")$", url) // 排除非html文件后缀
				|| isRootDomainSame(domainThis, domain) == false // 顶级域名不一样
				|| isInBlackList(domain, url) // 在黑名单中
				|| isbbs(domainThis, domain) // 排除bbs
				|| Pattern.matches("http(s?)://" + domainThis + "/(bbs|en|EN|tw|TW|english|ENGLISH|newenglish|erp)(/.*)?", url) // 排除非中文
				|| Pattern.matches(".+(&|\\?)id=\\-\\d+.*", url)) {
			return false;
		}
		return true;
	}

	/**
	 * 判断两个顶级域名是否相同
	 * 
	 * @param domainThis
	 * @param domain
	 * @return
	 */
	private boolean isRootDomainSame(String domainThis, String domain) {
		if (domainThis.equals(domain))
			return true;
		if (domainThis.length() > domain.length() && domainThis.replace(domain, "").endsWith("."))
			return true;
		return false;
	}

	static final String EXCEPT_SUFFIX = "xls|xlsx|gif|GIF|jpg|JPG|png|PNG|ico|ICO|css|CSS|sit|SIT|eps|EPS|wmf|WMF|zip|ZIP|rar|RAR|ppt|PPT|mpg|MPG|xls|XLS|gz|GZ|rpm|RPM|tgz|TGZ|mov|MOV|exe|EXE|jpeg|JPEG|bmp|BMP|js|JS|swf|SWF|flv|FLV|mp4|MP4|mp3|MP3|wmv|WMV";

	private boolean isInBlackList(String domain, String url) {
		String regex = regexMap.get(domain);
		if (regex == null) {
			return false;
		}
		return Pattern.matches("(" + regex + ")", url);
	}

	private boolean isbbs(String domainThis, String domain) {
		int i = domainThis.indexOf(domain);
		if (i == 0)
			return false;
		String prefix = domainThis.substring(0, i - 1);
		if (prefix.startsWith("bbs") || prefix.endsWith("bbs")
				|| Pattern.matches("(bbs|mail|video|oa|newoa|hospital|english|en|email|de|jp|erp)", prefix))
			return true;
		return false;
	}

	public static void main(String[] args) {
		DomainUrlFilter urlFilter = new DomainUrlFilter();
		System.out.println(urlFilter.accept("jingda.cn", "http://www.ejingda.cn/catalog/qibaotongxian/"));
	}

}
