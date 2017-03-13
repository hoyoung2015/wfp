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

import org.apache.commons.lang3.StringUtils;

import us.codecraft.webmagic.utils.UrlUtils;

public class DomainUrlFilter {

	private String configFile = "domain_url_black_list.txt";
	private Map<String, String> notAccessRegexMap = new HashMap<>();
	private Map<String, String> accessRegexMap = new HashMap<>();
	private Pattern domainPattern = Pattern.compile("^\\[([0-9a-zA-Z_\\-\\.]+)\\]$");

	private static String EN_REGEX = "bbs|esitecn|eng|en|EN|En|ru|fr|ar|sp|jp|py|Es|vn|eng|tw|TW|english|ENGLISH|japanese|newenglish|erp|BYDEnglish|English|\\w+_english";

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

				Map<String, String> tmp = null;
				if (line.startsWith("+")) {
					tmp = accessRegexMap;
					line = line.substring(1);
				} else {
					tmp = notAccessRegexMap;
				}
				String regex = tmp.get(cusorDomain);
				if (regex == null) {
					regex = line;
				} else {
					regex += "|" + line;
				}
				tmp.put(cusorDomain, regex);
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
		for (Entry<String, String> entry : notAccessRegexMap.entrySet()) {
			System.out.println(entry.getKey() + "\t" + entry.getValue());
		}
	}

	public boolean isRejectFileUrl(String url) {
		if (Pattern.matches(".+(\\.|/)(" + EXCEPT_SUFFIX + ")\\?.*", url) // 排除非html文件
				|| Pattern.matches(".+\\.(" + EXCEPT_SUFFIX + ")$", url)) { // 排除非html文件后缀
			return true;
		}
		return false;
	}

	/**
	 * 返回true给通过，否则丢弃该url
	 * 
	 * @param domain
	 * @param url
	 * @return
	 */
	public boolean accept(String domain, String url) {

		String acceptRegex = accessRegexMap.get(domain);

		if (StringUtils.isNotEmpty(acceptRegex) && !Pattern.matches("(" + accessRegexMap.get(domain) + ")", url)) {
			return false;
		}
		// 不能单纯的认为出现在白名单中就是合法的，因为还要验证后缀

		String domainThis = UrlUtils.getDomain(url);
		/**
		 * .css?v=1 .css,.jpg 站内 包含#，锚记 "mailto"开头 英文页，繁体
		 */
		if (!url.startsWith("http") // 不是http协议
				|| isRootDomainSame(domainThis, domain) == false // 顶级域名不一样
				|| isRejectFileUrl(url) || isInBlackList(domain, url) // 在黑名单中
				|| isbbs(domainThis, domain) // 排除bbs
				|| Pattern.matches("http(s?)://" + domainThis + "/(html/)?(" + EN_REGEX + ")(/.*)?", url) // 排除非中文
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

		// 去掉端口
		if (domainThis.indexOf(":") > -1) {
			domainThis = domainThis.replaceAll(":\\d+$", "");
		}
		if (domain.indexOf(":") > -1) {
			domain = domain.replaceAll(":\\d+$", "");
		}

		if (domainThis.equals(domain))
			return true;

		if (domainThis.length() > domain.length() && domainThis.replace(domain, "").endsWith("."))
			return true;
		return false;
	}

	static final String EXCEPT_SUFFIX = "xls|xlsx|gif|GIF|jpg|JPG|png|PNG|ico|ICO|css|CSS|sit|SIT|eps|EPS|wmf|WMF|zip|ZIP|rar|RAR|ppt|PPT|mpg|MPG|xls|XLS|gz|GZ|rpm|RPM|tgz|TGZ|mov|MOV|exe|EXE|jpeg|JPEG|bmp|BMP|js|JS|swf|SWF|flv|FLV|mp4|MP4|mp3|MP3|wmv|WMV|apk|dmg|pptx";

	private boolean isInBlackList(String domain, String url) {
		String regex = notAccessRegexMap.get(domain);
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
				|| Pattern.matches(
						"(bbs|kr|es|sp|mail|video|zhaopin|oa|newoa|hospital|english|esp|en|email|de|jp|erp|ru|sp|english)",
						prefix))
			return true;
		return false;
	}

	public static void main(String[] args) {
		DomainUrlFilter urlFilter = new DomainUrlFilter();
		System.out.println(
				urlFilter.accept("unilumin.cn", "http://www.unilumin.cn/channel/High-end%20Solutions_545.html"));
	}

}
