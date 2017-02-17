package net.hoyoung.wfp.spider.test;

import java.util.List;

import com.google.common.collect.Lists;

import net.hoyoung.wfp.spider.comweb.ComWebProcessor;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

public class TestComWebSpider {

	public static void main(String[] args) {
		Request request = new Request("http://www.xemc.com.cn");
//		Request request = new Request("http://www.hoyoung.net");
		request.putExtra(ComPage.STOCK_CODE, "111111");
		ComWebProcessor processor = new ComWebProcessor();
		processor.getSite().setSleepTime(5000);
//		processor.getSite().addHeader("Host", "www.7cf.com");
		List<String[]> proxies = Lists.newArrayList();
//		proxies.add(new String[] { "hoyoung", "QWerASdf", "139.129.93.2", "8128" });// 杨鹏的阿里云
//		proxies.add(new String[] { "hoyoung", "QWerASdf", "123.206.58.101", "8128" });// 我的腾讯云
//		proxies.add(new String[] { "hoyoung", "QWerASdf", "182.61.20.189", "8128" });// 我的百度云，首月9.9
//		proxies.add(new String[] { "hoyoung", "QWerASdf", "118.89.238.129", "8128" });// 余启林的腾讯学生机
//		proxies.add(new String[] { "hoyoung", "QWerASdf", "115.159.121.124", "8128" });// 刘威浩的腾讯学生机
		proxies.add(new String[] { "hoyoung", "QWerASdf", "119.29.62.75", "8128" });// 邹小燕的腾讯学生机
		proxies.add(new String[] { "hoyoung", "QWerASdf", "115.159.92.73", "8128" });// 马晶苗的腾讯学生机
		processor.getSite().setHttpProxyPool(proxies, false);
		Spider.create(processor).addRequest(request).thread(1).run();
	}

}
