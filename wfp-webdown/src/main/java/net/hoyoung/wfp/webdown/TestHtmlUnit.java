package net.hoyoung.wfp.webdown;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestHtmlUnit {

	public static void main(String[] args) {
		WebClient webClient = new WebClient(BrowserVersion.CHROME);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			
	        webClient.getOptions().setCssEnabled(false);
	        try {
	        	String[] url = {"http://127.0.0.1:81/","http://www.wisco.com.cn/wgxw2015/index.jhtml"};
				HtmlPage htmlPage = null;
				try {
					htmlPage = webClient.getPage(url[1]);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				List<HtmlAnchor> anchors = htmlPage.getAnchors();
				for (HtmlAnchor htmlAnchor : anchors) {
					System.err.println("----------------------"+htmlAnchor.getTextContent());
				}
				for (HtmlAnchor htmlAnchor : anchors) {
					if(htmlAnchor.getHrefAttribute().startsWith("https")){
						continue;
					}
					if("下一页".equals(htmlAnchor.getTextContent())){
						Page page = null;
						try {
							page = htmlAnchor.click();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.err.println("----------------------"+page.getUrl());
						System.err.println("----------------------"+htmlAnchor.getTextContent());
						try {
							FileUtils.writeStringToFile(new File("E:\\huyang\\webfootprint\\downloads\\"+htmlAnchor.getTextContent()+"_"+Math.round(Math.random()*10000000)+".html"), page.getWebResponse().getContentAsString(), "UTF-8");
						} catch (IOException e) {
							System.err.println("IOException e");
							e.printStackTrace();
						}
					}
//						break;
				}
			} finally{
				webClient.closeAllWindows();
			}
	}

}
