package net.hoyoung.wfp.webdown;

import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestHtmlUnit2 {

	public static void main(String[] args) {
		WebClient webClient = new WebClient(BrowserVersion.CHROME);
	      //htmlunit 对css和javascript的支持不好，所以请关闭之
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
//			webClient.getOptions().setThrowExceptionOnScriptError(false);
	        webClient.getOptions().setCssEnabled(false);
	        try {
	        	String[] url = {"http://127.0.0.1:81/","http://www.wisco.com.cn/wgxw2015/index.jhtml"};
				HtmlPage htmlPage = webClient.getPage(url[1]);
				List<HtmlAnchor> anchors = htmlPage.getAnchors();
				Page page = anchors.get(1).click();
				System.out.println(page.getWebResponse().getContentAsString());
				System.out.println(webClient.getWebWindows().size());
			} catch (Exception e) {
				e.printStackTrace();
			}
	        webClient.closeAllWindows();
	        webClient = null;
	}

}
